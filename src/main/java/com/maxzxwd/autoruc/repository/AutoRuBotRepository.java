package com.maxzxwd.autoruc.repository;

import com.maxzxwd.autoruc.domain.AutoRuBot;
import com.maxzxwd.autoruc.domain.AutoRuSessionStorage;
import com.maxzxwd.autoruc.service.HttpClientSessionFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AutoRuBotRepository {
    private final Map<Long, AutoRuBot> repository = new ConcurrentHashMap<>();

    @Nullable
    public AutoRuBot get(@NonNull Long id) {
        return repository.get(id);
    }

    @NonNull
    public AutoRuBot save(@NonNull AutoRuBot bot) {

        if (bot.id == null) {
            bot.id = (long) repository.size();
        }
        repository.put(bot.id, bot);

        return bot;
    }

    @NonNull
    public Collection<Long> list() {
        return repository.keySet();
    }
}
