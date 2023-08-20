package com.maxzxwd.autoruc.service;

import com.maxzxwd.autoruc.cookie.CustomCookieStore;
import org.springframework.lang.NonNull;

import java.net.CookieManager;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public interface HttpClientSessionFactory {

    @NonNull
    HttpClientSession create(@NonNull SessionStorage storage) throws HttpClientSessionCreateException;

    interface HttpClientSession {

        @NonNull
        CustomCookieStore getCookieStore();

        @NonNull
        HttpClient getHttpClient();

        @NonNull
        String getCsrfToken();

        @NonNull String[] getBaseHeaders();

        void fillStorage(@NonNull SessionStorage storage);
    }

    interface SessionStorage {

        void setCookies(@NonNull Map<String, List<String>> cookies);

        @NonNull
        Map<String, List<String>> getCookies();

        void setBaseHeaders(@NonNull List<String> baseHeaders);

        @NonNull
        List<String> getBaseHeaders();
    }

    class BaseHeadersSessionStorage implements SessionStorage {

        private final String[] baseHeaders;

        public BaseHeadersSessionStorage(String... baseHeaders) {
            this.baseHeaders = baseHeaders;
        }

        @Override
        public void setCookies(@NonNull Map<String, List<String>> cookies) {
            throw new IllegalStateException("BaseHeadersHttpClientSessionStorage only for creating session");
        }

        @Override
        @NonNull
        public Map<String, List<String>> getCookies() {
            return Map.of();
        }

        @Override
        public void setBaseHeaders(@NonNull List<String> baseHeaders) {
            throw new IllegalStateException("BaseHeadersHttpClientSessionStorage only for creating session");
        }

        @Override
        @NonNull
        public List<String> getBaseHeaders() {
            return List.of(baseHeaders);
        }
    }

    class HttpClientSessionCreateException extends Exception {

        public HttpClientSessionCreateException() {
            super();
        }

        public HttpClientSessionCreateException(String message) {
            super(message);
        }

        public HttpClientSessionCreateException(String message, Throwable cause) {
            super(message, cause);
        }

        public HttpClientSessionCreateException(Throwable cause) {
            super(cause);
        }
    }
}
