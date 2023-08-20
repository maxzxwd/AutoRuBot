package com.maxzxwd.autoruc.domain;

import com.maxzxwd.autoruc.dto.autoru.AutoRuGeoId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public class AutoRuBot {

    @Nullable
    public Long id;

    @Nullable
    public Long priceFrom;

    @Nullable
    public Long priceTo;

    @Nullable
    public Integer geoRadius;

    @NonNull
    public List<AutoRuGeoId> geoIds;

    public boolean enabled;

    @NonNull
    public AutoRuSessionStorage sessionStorage;
}
