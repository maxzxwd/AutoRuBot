package com.maxzxwd.autoruc.dto.autoru;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

public class ListingRequest {

    @NonNull
    @JsonProperty("top_days")
    public final String topDays;

    @Nullable
    @JsonProperty("price_from")
    public final Long priceFrom;

    @Nullable
    @JsonProperty("price_to")
    public final Long priceTo;

    @NonNull
    public final String section;

    @NonNull
    public final String category;

    @Nullable
    public final String sort;

    @NonNull
    @JsonProperty("output_type")
    public final String outputType;

    @Nullable
    @JsonProperty("geo_radius")
    public final Integer geoRadius;

    @NonNull
    @JsonProperty("geo_id")
    public final List<Integer> geoId;

    public ListingRequest(@NonNull String topDays, @Nullable Long priceFrom, @Nullable Long priceTo,
                          @NonNull String section, @NonNull String category, @Nullable String sort,
                          @NonNull String outputType, @Nullable Integer geoRadius, @NonNull List<Integer> geoId) {
        this.topDays = topDays;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.section = section;
        this.category = category;
        this.sort = sort;
        this.outputType = outputType;
        this.geoRadius = geoRadius;
        this.geoId = Collections.unmodifiableList(geoId);
    }
}
