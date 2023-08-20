package com.maxzxwd.autoruc.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;

public class RedirectData {

    @NonNull
    public final String host;

    @NonNull
    public final String retPath;

    @JsonCreator
    public RedirectData(@NonNull @JsonProperty("host") String host,
                        @NonNull @JsonProperty("retpath") String retPath) {
        this.host = host;
        this.retPath = retPath;
    }

    @Override
    public String toString() {
        return "RedirectData{" +
                "host='" + host + '\'' +
                ", retPath='" + retPath + '\'' +
                '}';
    }
}
