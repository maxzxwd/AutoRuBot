package com.maxzxwd.autoruc.dto;

import com.maxzxwd.autoruc.dto.autoru.AutoRuGeoId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public class CreateBotRequest {

    @Nullable
    public final Long proxyId;

    @Nullable
    public final Long priceFrom;

    @Nullable
    public final Long priceTo;

    @Nullable
    public final int geoRadius;

    @NonNull
    public final List<AutoRuGeoId> geoIds;

    public CreateBotRequest(@Nullable Long proxyId, @Nullable Long priceFrom, @Nullable Long priceTo, int geoRadius,
                            @NonNull List<AutoRuGeoId> geoIds) {
        this.proxyId = proxyId;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.geoRadius = geoRadius;
        this.geoIds = geoIds;
    }
}
