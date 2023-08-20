package com.maxzxwd.autoruc.dto.autoru;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ListingResponse {

    @NonNull
    public final List<ListingResponseOffer> offers;

    @JsonCreator
    public ListingResponse(@NonNull @JsonProperty("offers") List<ListingResponseOffer> offers) {
        this.offers = offers;
    }

    public static class ListingResponseOffer {

        @NonNull
        public final ListingResponseOfferPriceInfo priceInfo;

        @NonNull
        public final ListingResponseOfferVehicleInfo vehicleInfo;

        @NonNull
        public final ListingResponseOfferDocuments documents;

        @NonNull
        public final ListingResponseOfferPredictedPriceRanges predictedPriceRanges;

        @NonNull
        public final String saleId;

        @JsonCreator
        public ListingResponseOffer(@NonNull @JsonProperty("price_info") ListingResponseOfferPriceInfo priceInfo,
                                    @NonNull @JsonProperty("vehicle_info") ListingResponseOfferVehicleInfo vehicleInfo,
                                    @NonNull @JsonProperty("documents") ListingResponseOfferDocuments documents,
                                    @NonNull @JsonProperty("predicted_price_ranges") ListingResponseOfferPredictedPriceRanges predictedPriceRanges,
                                    @NonNull @JsonProperty("saleId") String saleId) {
            this.priceInfo = priceInfo;
            this.vehicleInfo = vehicleInfo;
            this.documents = documents;
            this.predictedPriceRanges = predictedPriceRanges;
            this.saleId = saleId;
        }

        public static class ListingResponseOfferPredictedPriceRanges {

            @Nullable
            public final ListingResponseOfferPredictedPriceRangesTagRange tagRange;

            @JsonCreator
            public ListingResponseOfferPredictedPriceRanges(@Nullable @JsonProperty("tag_range") ListingResponseOfferPredictedPriceRangesTagRange tagRange) {
                this.tagRange = tagRange;
            }

            public static class ListingResponseOfferPredictedPriceRangesTagRange {

                @Nullable
                public final BigDecimal from;

                @JsonCreator
                public ListingResponseOfferPredictedPriceRangesTagRange(@Nullable @JsonProperty("from") BigDecimal from) {
                    this.from = from;
                }
            }
        }

        public static class ListingResponseOfferDocuments {
            public final int year;

            @JsonCreator
            public ListingResponseOfferDocuments(@JsonProperty("year") int year) {
                this.year = year;
            }
        }

        public static class ListingResponseOfferVehicleInfo {

            @NonNull
            public final ListingResponseOfferVehicleInfoMarkInfo markInfo;

            @NonNull
            public final ListingResponseOfferVehicleInfoModelInfo modelInfo;

            @JsonCreator
            public ListingResponseOfferVehicleInfo(@NonNull @JsonProperty("mark_info") ListingResponseOfferVehicleInfoMarkInfo markInfo,
                                                   @NonNull @JsonProperty("model_info") ListingResponseOfferVehicleInfoModelInfo modelInfo) {
                this.markInfo = markInfo;
                this.modelInfo = modelInfo;
            }

            public static class ListingResponseOfferVehicleInfoMarkInfo {
                @NonNull
                public final String code;

                @NonNull
                public final String name;

                @NonNull
                public final String ruName;

                @JsonCreator
                public ListingResponseOfferVehicleInfoMarkInfo(@NonNull @JsonProperty("code") String code,
                                                               @NonNull @JsonProperty("name") String name,
                                                               @JsonProperty("ru_name") @NonNull String ruName) {
                    this.code = code;
                    this.name = name;
                    this.ruName = ruName;
                }
            }

            public static class ListingResponseOfferVehicleInfoModelInfo {
                @NonNull
                public final String code;

                @NonNull
                public final String name;

                @NonNull
                public final String ruName;

                @JsonCreator
                public ListingResponseOfferVehicleInfoModelInfo(@NonNull @JsonProperty("code") String code,
                                                                @NonNull @JsonProperty("name") String name,
                                                                @NonNull @JsonProperty("ru_name") String ruName) {
                    this.code = code;
                    this.name = name;
                    this.ruName = ruName;
                }
            }
        }

        public static class ListingResponseOfferPriceInfo {

            @NonNull
            public final BigDecimal priceEUR;

            @NonNull
            public final BigDecimal priceRUB;

            @NonNull
            public final BigDecimal priceUSD;

            @JsonCreator
            public ListingResponseOfferPriceInfo(@NonNull @JsonProperty("EUR") BigDecimal priceEUR,
                                                 @NonNull @JsonProperty("RUR") BigDecimal priceRUB,
                                                 @NonNull @JsonProperty("USD") BigDecimal priceUSD) {
                this.priceEUR = priceEUR;
                this.priceRUB = priceRUB;
                this.priceUSD = priceUSD;
            }
        }
    }
}
