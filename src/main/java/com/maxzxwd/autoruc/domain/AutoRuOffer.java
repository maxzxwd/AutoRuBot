package com.maxzxwd.autoruc.domain;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public class AutoRuOffer {

    @Nullable
    public Long id;

    @NonNull
    public LocalDateTime creationTime = LocalDateTime.now();

    @NonNull
    public String url;
}
