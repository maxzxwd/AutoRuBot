package com.maxzxwd.autoruc.controller;

import com.maxzxwd.autoruc.domain.AutoRuSessionStorage;
import com.maxzxwd.autoruc.dto.CreateResponse;
import com.maxzxwd.autoruc.repository.AutoRuSessionStorageRepository;
import com.maxzxwd.autoruc.service.AutoRuHttpClientSessionFactory;
import com.maxzxwd.autoruc.service.HttpClientSessionFactory;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping(value = "/proxy", produces = MediaType.APPLICATION_JSON_VALUE)
public class AutoRuProxyController {

    @NonNull
    private final AutoRuHttpClientSessionFactory autoRuHttpClientSessionFactory;

    @NonNull
    private final AutoRuSessionStorageRepository autoRuSessionStorageRepository;

    @Autowired
    public AutoRuProxyController(@NonNull AutoRuHttpClientSessionFactory autoRuHttpClientSessionFactory,
                                 @NonNull AutoRuSessionStorageRepository autoRuSessionStorageRepository) {
        this.autoRuHttpClientSessionFactory = autoRuHttpClientSessionFactory;
        this.autoRuSessionStorageRepository = autoRuSessionStorageRepository;
    }

    @Operation(summary = "List auto ru http proxy client")
    @GetMapping(value = "/list", consumes = MediaType.ALL_VALUE)
    public Collection<Long> listProxy(HttpServletRequest httpServletRequest) throws HttpClientSessionFactory.HttpClientSessionCreateException {

        return autoRuSessionStorageRepository.list();
    }

    @Operation(summary = "Create auto ru http proxy client")
    @PutMapping(value = "/create", consumes = MediaType.ALL_VALUE)
    public CreateResponse createProxy(HttpServletRequest httpServletRequest) throws HttpClientSessionFactory.HttpClientSessionCreateException {

        var client = autoRuHttpClientSessionFactory.create(new HttpClientSessionFactory.BaseHeadersSessionStorage(
                "Accept", httpServletRequest.getHeader("Accept"),
                "User-Agent", httpServletRequest.getHeader("User-Agent"),
                "Accept-Language", httpServletRequest.getHeader("Accept-Language")
        ));

        var storage = new AutoRuSessionStorage();
        client.fillStorage(storage);

        return new CreateResponse(Objects.requireNonNull(autoRuSessionStorageRepository.save(storage).id));
    }

    @Operation(summary = "Make auto ru request")
    @PostMapping(value = {"/post/{id}/{param1}", "/post/{id}/{param1}/{param2}", "/post/{id}/{param1}/{param2}/{param3}", "/post/{id}/{param1}/{param2}/{param3}/{param4}"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @NonNull
    public String makeProxyPostRequest(@PathVariable @NonNull Long id,
                                   @PathVariable(required = false) @Nullable String param1,
                                   @PathVariable(required = false) @Nullable String param2,
                                   @PathVariable(required = false) @Nullable String param3,
                                   @PathVariable(required = false) @Nullable String param4,
                                   @RequestBody @NonNull String request) throws IOException, InterruptedException, HttpClientSessionFactory.HttpClientSessionCreateException {

        var paramList = new ArrayList<String>(3);

        if (param1 != null && !param1.isEmpty()) {
            paramList.add(param1);
        }

        if (param2 != null && !param2.isEmpty()) {
            paramList.add(param2);
        }

        if (param3 != null && !param3.isEmpty()) {
            paramList.add(param3);
        }

        if (param4 != null && !param4.isEmpty()) {
            paramList.add(param4);
        }

        var storage = Objects.requireNonNull(autoRuSessionStorageRepository.get(id));
        var client = autoRuHttpClientSessionFactory.create(storage);

        var autoRuRequest = HttpRequest.newBuilder()
                .uri(URI.create(AutoRuHttpClientSessionFactory.URL + String.join("/", paramList)))
                .headers(client.getBaseHeaders())
                .header("x-csrf-token", client.getCsrfToken())
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();

        var response = client.getHttpClient()
                .send(autoRuRequest, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
