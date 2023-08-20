package com.maxzxwd.autoruc.domain;

import com.maxzxwd.autoruc.service.HttpClientSessionFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoRuSessionStorage implements HttpClientSessionFactory.SessionStorage {

    @Nullable
    public Long id;

    @NonNull
    public Map<String, List<String>> cookies;

    @NonNull
    public List<String> baseHeaders;

    @Override
    public void setCookies(@NonNull Map<String, List<String>> cookies) {

        var tmp = new HashMap<String, List<String>>(cookies.size());

        cookies.forEach((key, value) -> {
            tmp.put(key, List.copyOf(value));
        });

        this.cookies = Collections.unmodifiableMap(tmp);
    }

    @Override
    @NonNull
    public Map<String, List<String>> getCookies() {
        return cookies;
    }

    @Override
    public void setBaseHeaders(@NonNull List<String> baseHeaders) {
        this.baseHeaders = List.copyOf(baseHeaders);
    }

    @Override
    @NonNull
    public List<String> getBaseHeaders() {
        return baseHeaders;
    }
}
