package com.maxzxwd.autoruc.repository;

import com.maxzxwd.autoruc.domain.AutoRuBot;
import com.maxzxwd.autoruc.domain.AutoRuOffer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AutoRuOfferRepository {
    private final Map<Long, AutoRuOffer> repository = new ConcurrentHashMap<>();
    private final Map<String, AutoRuOffer> repositoryUrlIndex = new ConcurrentHashMap<>();

    @Nullable
    public AutoRuOffer findByUrl(@NonNull String url) {
        return repositoryUrlIndex.get(url);
    }

    @NonNull
    public AutoRuOffer save(@NonNull AutoRuOffer offer) {

        if (offer.id == null) {
            offer.id = (long) repository.size();
        }
        repository.put(offer.id, offer);
        repositoryUrlIndex.put(offer.url, offer);

        return offer;
    }
}
