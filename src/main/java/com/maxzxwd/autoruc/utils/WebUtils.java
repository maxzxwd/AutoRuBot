package com.maxzxwd.autoruc.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.lang.NonNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class WebUtils {

    private WebUtils() {}

    @NonNull
    public static <T> T fetchPage(@NonNull String url, Function<WebDriver, T> transformer) {

        var options = new ChromeOptions();
        options.addArguments("--headless=new");
        var driver = new ChromeDriver(options);
        driver.get(url);
        var result = transformer.apply(driver);
        driver.quit();

        return result;
    }

    @NonNull
    public static String createFormUrlEncoded(@NonNull Map<String, String> data) {

        return data.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}
