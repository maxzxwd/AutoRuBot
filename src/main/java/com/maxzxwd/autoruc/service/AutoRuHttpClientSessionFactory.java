package com.maxzxwd.autoruc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxzxwd.autoruc.cookie.CustomCookieManager;
import com.maxzxwd.autoruc.cookie.CustomCookieStore;
import com.maxzxwd.autoruc.dto.RedirectData;
import com.maxzxwd.autoruc.utils.JavaUtils;
import com.maxzxwd.autoruc.utils.WebUtils;
import com.maxzxwd.autoruc.utils.XmlUtils;
import org.openqa.selenium.By;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AutoRuHttpClientSessionFactory implements HttpClientSessionFactory {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("var\\sit\\s=\\s(.*);\\(function\\(\\).*element2\\.value =\\s'(.*)';\\sform\\.appendChild");
    public static final String URL = "https://auto.ru/";

    @Override
    @NonNull
    public HttpClientSession create(@NonNull SessionStorage storage) throws HttpClientSessionCreateException {

        var clientSession = new AutoRuHttpClientSession(storage.getBaseHeaders().toArray(new String[0]));

        if (storage.getCookies().isEmpty()) {
            tryInit(clientSession);
        } else {
            storage.getCookies().forEach((uri, cookies) -> {
                cookies.forEach(cookie -> {
                    clientSession.cookieStore.add(URI.create(uri), HttpCookie.parse(cookie).get(0));
                });
            });
        }

        return clientSession;
    }

    private void tryInit(@NonNull HttpClientSession clientSession) throws HttpClientSessionCreateException {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .headers(clientSession.getBaseHeaders())
                .GET()
                .build();

        var response = JavaUtils.uncheck(
                () -> clientSession.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString()),
                e -> new RuntimeException("Unable to make init request: ", e)
        );

        String captchaUrl = null;

        if (response.statusCode() == 302) {

            var location = response.headers().firstValue("Location");

            if (location.isPresent()) {

                var redirectRequest = HttpRequest.newBuilder()
                        .uri(URI.create(location.get()))
                        .headers(clientSession.getBaseHeaders())
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();

                response = JavaUtils.uncheck(
                        () -> clientSession.getHttpClient().send(redirectRequest, HttpResponse.BodyHandlers.ofString()),
                        e -> new RuntimeException("Unable to make redirect request: ", e)
                );

                if (isCaptcha(response.body())) {

                    response = tryResolveCaptcha(location.get(), clientSession);

                    if (isCaptcha(response.body())) {
                        captchaUrl = response.request().uri().toString();
                    }
                }
            }
        }

        if (captchaUrl == null) {

            var responseBody = response.body();

            var xmlResponse = JavaUtils.uncheck(
                    () -> XmlUtils.parse("<root>" + responseBody + "</root>"),
                    e -> new RuntimeException("Unable parse html: ", e)
            );

            var redirectScript = xmlResponse.getElementsByTagName("script").item(0).getTextContent().trim();

            var matcher = SCRIPT_PATTERN.matcher(redirectScript);

            if (matcher.find()) {
                var redirectScriptData = JavaUtils.uncheck(
                        () -> OBJECT_MAPPER.readValue(matcher.group(1), RedirectData.class),
                        e -> new RuntimeException("Unable parse redirect data: ", e)
                );
                var redirectScriptKey = matcher.group(2);

                var form = WebUtils.createFormUrlEncoded(Map.of(
                        "retpath", redirectScriptData.retPath,
                        "container", redirectScriptKey
                ));

                var redirectRequest = HttpRequest.newBuilder()
                        .uri(URI.create(redirectScriptData.host))
                        .headers("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(form))
                        .build();

                JavaUtils.uncheck(
                        () -> clientSession.getHttpClient().send(redirectRequest, HttpResponse.BodyHandlers.ofString()),
                        e -> new RuntimeException("Unable to execute redirect script: ", e)
                );
            } else {
                throw new HttpClientSessionCreateException("Unable to parse redirect script: " + redirectScript);
            }
        } else {
            throw new HttpClientSessionCreateException("Captcha detected. Open " + captchaUrl + " in browser in privacy mode and resolve captcha then try again");
        }
    }

    private static boolean isCaptcha(String html) {
        return html.contains("ym(") && html.contains("yaDisableGDPR") && html.contains("https://mc.yandex.ru/watch/");
    }

    private static HttpResponse<String> tryResolveCaptcha(String url, HttpClientSession clientSession) {

        var resolveRequest = WebUtils.fetchPage(url, driver-> {

            var form = WebUtils.createFormUrlEncoded(Map.of(
                    "aesKey", driver.findElement(By.name("aesKey")).getAttribute("value"),
                    "signKey", driver.findElement(By.name("signKey")).getAttribute("value"),
                    "pdata", driver.findElement(By.name("pdata")).getAttribute("value"),
                    "tdata", driver.findElement(By.name("tdata")).getAttribute("value")
            ));

            return HttpRequest.newBuilder()
                    .uri(URI.create(driver.findElement(By.id("checkbox-captcha-form")).getAttribute("action")))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();
        });

        var response = JavaUtils.uncheck(
                () -> clientSession.getHttpClient().send(resolveRequest, HttpResponse.BodyHandlers.ofString()),
                e -> new RuntimeException("Unable to make resolve captcha request: ", e)
        );

        if (response.statusCode() == 302) {

            var location = response.headers().firstValue("Location");

            if (location.isPresent()) {

                var redirectRequest = HttpRequest.newBuilder()
                        .uri(URI.create(location.get()))
                        .headers(clientSession.getBaseHeaders())
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();

                response = JavaUtils.uncheck(
                        () -> clientSession.getHttpClient().send(redirectRequest, HttpResponse.BodyHandlers.ofString()),
                        e -> new RuntimeException("Unable to make redirect request: ", e)
                );
            }
        }

        return response;
    }

    private static class AutoRuHttpClientSession implements HttpClientSession {

        private final CustomCookieStore cookieStore;

        private final HttpClient httpClient;

        @NonNull
        private final String[] baseHeaders;

        private AutoRuHttpClientSession(@NonNull String[] baseHeaders) {

            this.baseHeaders = baseHeaders;

            var cookieManager = new CustomCookieManager();
            this.httpClient = HttpClient.newBuilder()
                    .cookieHandler(cookieManager)
                    .build();
            this.cookieStore = cookieManager.cookieJar;
        }

        @Override
        @NonNull
        public CustomCookieStore getCookieStore() {
            return cookieStore;
        }

        @Override
        @NonNull
        public HttpClient getHttpClient() {
            return httpClient;
        }

        @Override
        @NonNull
        public String getCsrfToken() {

            return cookieStore.getCookies().stream()
                    .filter(cookie -> "_csrf_token".equals(cookie.getName()))
                    .findAny()
                    .orElseThrow()
                    .getValue();
        }

        @Override
        @NonNull
        public String[] getBaseHeaders() {
            return baseHeaders;
        }

        @Override
        public void fillStorage(@NonNull SessionStorage storage) {

            var cookies = new HashMap<String, List<String>>();
            var uriIndex = cookieStore.getUriIndex();
            var cookieJar = cookieStore.getCookieInfoJar();

            uriIndex.forEach((uri, httpCookies) -> {

                var actualCookies = httpCookies.stream()
                        .filter(cookie -> !cookie.hasExpired() && cookieJar.containsKey(cookie))
                        .map(httpCookie -> cookieJar.get(httpCookie).header())
                        .toList();

                if (!actualCookies.isEmpty()) {
                    cookies.put(uri.toString(), actualCookies);
                }
            });

            storage.setBaseHeaders(List.of(baseHeaders));
            storage.setCookies(cookies);
        }
    }
}
