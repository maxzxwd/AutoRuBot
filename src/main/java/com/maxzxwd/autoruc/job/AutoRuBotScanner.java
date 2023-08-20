package com.maxzxwd.autoruc.job;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxzxwd.autoruc.domain.AutoRuOffer;
import com.maxzxwd.autoruc.dto.autoru.AutoRuGeoId;
import com.maxzxwd.autoruc.dto.autoru.ListingRequest;
import com.maxzxwd.autoruc.dto.autoru.ListingResponse;
import com.maxzxwd.autoruc.repository.AutoRuBotRepository;
import com.maxzxwd.autoruc.repository.AutoRuOfferRepository;
import com.maxzxwd.autoruc.service.AutoRuHttpClientSessionFactory;
import com.maxzxwd.autoruc.service.HttpClientSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class AutoRuBotScanner {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NonNull
    private final AutoRuHttpClientSessionFactory autoRuHttpClientSessionFactory;

    @NonNull
    private final AutoRuBotRepository autoRuBotRepository;

    @NonNull
    private final AutoRuOfferRepository autoRuOfferRepository;

    @Autowired
    public AutoRuBotScanner(@NonNull AutoRuHttpClientSessionFactory autoRuHttpClientSessionFactory,
                            @NonNull AutoRuBotRepository autoRuBotRepository,
                            @NonNull AutoRuOfferRepository autoRuOfferRepository) {
        this.autoRuHttpClientSessionFactory = autoRuHttpClientSessionFactory;
        this.autoRuBotRepository = autoRuBotRepository;
        this.autoRuOfferRepository = autoRuOfferRepository;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void scan() throws HttpClientSessionFactory.HttpClientSessionCreateException, IOException, InterruptedException {

        for (var botId : autoRuBotRepository.list()) {
            var bot = Objects.requireNonNull(autoRuBotRepository.get(botId));

            var listingRequest = new ListingRequest("1", bot.priceFrom, bot.priceTo, "used",
                    "cars", "cr_date-desc", "list", bot.geoRadius,
                    bot.geoIds.stream().map(AutoRuGeoId::code).collect(Collectors.toList()));

            var client = autoRuHttpClientSessionFactory.create(bot.sessionStorage);

            var autoRuRequest = HttpRequest.newBuilder()
                    .uri(URI.create(AutoRuHttpClientSessionFactory.URL + "-/ajax/desktop/listing"))
                    .headers(client.getBaseHeaders())
                    .header("x-csrf-token", client.getCsrfToken())
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(listingRequest)))
                    .build();

            var responseText = client.getHttpClient()
                    .send(autoRuRequest, HttpResponse.BodyHandlers.ofString())
                    .body();

            var response = OBJECT_MAPPER.readValue(responseText, ListingResponse.class);

            for (var offer : response.offers) {

                var url = AutoRuHttpClientSessionFactory.URL + "cars/used/sale/" +
                        offer.vehicleInfo.markInfo.code.toLowerCase() + "/" +
                        offer.vehicleInfo.modelInfo.code.toLowerCase() + "/" + offer.saleId;

                var hasPrediction = offer.predictedPriceRanges.tagRange != null && offer.predictedPriceRanges.tagRange.from != null;

                var priceLessThanPredict = hasPrediction &&
                        offer.priceInfo.priceRUB.compareTo(offer.predictedPriceRanges.tagRange.from) < 0;

                var notExisted = autoRuOfferRepository.findByUrl(url) == null;

                if (priceLessThanPredict && notExisted) {
                    var persistOffer = new AutoRuOffer();
                    persistOffer.url = url;
                    autoRuOfferRepository.save(persistOffer);
                    logger.info("\n{}\n{} {}, {}\nRUB: {}\nPredicted: {}", url, offer.vehicleInfo.markInfo.name, offer.vehicleInfo.modelInfo.name,
                            offer.documents.year, offer.priceInfo.priceRUB, offer.predictedPriceRanges.tagRange.from);
                }
            }
        }
    }
}
