package com.maxzxwd.autoruc.controller;

import com.maxzxwd.autoruc.domain.AutoRuBot;
import com.maxzxwd.autoruc.domain.AutoRuSessionStorage;
import com.maxzxwd.autoruc.dto.CreateBotRequest;
import com.maxzxwd.autoruc.dto.CreateResponse;
import com.maxzxwd.autoruc.repository.AutoRuBotRepository;
import com.maxzxwd.autoruc.repository.AutoRuSessionStorageRepository;
import com.maxzxwd.autoruc.service.AutoRuHttpClientSessionFactory;
import com.maxzxwd.autoruc.service.HttpClientSessionFactory;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(value = "/bot", produces = MediaType.APPLICATION_JSON_VALUE)
public class AutoRuBotController {

    @NonNull
    private final AutoRuHttpClientSessionFactory autoRuHttpClientSessionFactory;

    @NonNull
    private final AutoRuBotRepository autoRuBotRepository;

    @NonNull
    private final AutoRuSessionStorageRepository autoRuSessionStorageRepository;

    @Autowired
    public AutoRuBotController(@NonNull AutoRuHttpClientSessionFactory autoRuHttpClientSessionFactory,
                               @NonNull AutoRuBotRepository autoRuBotRepository,
                               @NonNull AutoRuSessionStorageRepository autoRuSessionStorageRepository) {
        this.autoRuHttpClientSessionFactory = autoRuHttpClientSessionFactory;
        this.autoRuBotRepository = autoRuBotRepository;
        this.autoRuSessionStorageRepository = autoRuSessionStorageRepository;
    }

    @Operation(summary = "Create auto ru bot")
    @PutMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateResponse createBot(@RequestBody CreateBotRequest body, HttpServletRequest httpServletRequest)
            throws HttpClientSessionFactory.HttpClientSessionCreateException {

        var newBot = new AutoRuBot();
        newBot.geoIds = body.geoIds;
        newBot.geoRadius = body.geoRadius == 0 ? null : body.geoRadius;
        newBot.priceFrom = Objects.equals(body.priceFrom, 0L) ? null : body.priceFrom;
        newBot.priceTo = Objects.equals(body.priceTo, 0L) ? null : body.priceTo;

        if (body.proxyId == null) {
            var client = autoRuHttpClientSessionFactory.create(new HttpClientSessionFactory.BaseHeadersSessionStorage(
                    "Accept", httpServletRequest.getHeader("Accept"),
                    "User-Agent", httpServletRequest.getHeader("User-Agent"),
                    "Accept-Language", httpServletRequest.getHeader("Accept-Language")
            ));

            var storage = new AutoRuSessionStorage();
            client.fillStorage(storage);

            newBot.sessionStorage = Objects.requireNonNull(autoRuSessionStorageRepository.save(storage));
        } else {
            newBot.sessionStorage = Objects.requireNonNull(autoRuSessionStorageRepository.get(body.proxyId));
        }

        return new CreateResponse(Objects.requireNonNull(autoRuBotRepository.save(newBot).id));
    }
}