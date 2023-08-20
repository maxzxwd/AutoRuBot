package com.maxzxwd.autoruc.repository;

import com.maxzxwd.autoruc.domain.AutoRuSessionStorage;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AutoRuSessionStorageRepository {
    private final Map<Long, AutoRuSessionStorage> repository = new ConcurrentHashMap<>();

    @Nullable
    public AutoRuSessionStorage get(@NonNull Long id) {
        return repository.get(id);
    }

    public AutoRuSessionStorage save(@NonNull AutoRuSessionStorage httpClientSession) {

        if (httpClientSession.id == null) {
            httpClientSession.id = (long) repository.size();
        }
        repository.put(httpClientSession.id, httpClientSession);

        return httpClientSession;
    }

    @NonNull
    public Collection<Long> list() {
        return repository.keySet();
    }
}
