package com.maxzxwd.autoruc.dto.autoru;

public enum AutoRuGeoId {

    YEKATERINBURG(54),

    MOSCOW_AND_MOSCOW_REGION(1),

    MOSCOW(213),

    ST_PETERSBURG(2),

    ST_PETERSBURG_AND_LENINGRAD_REGION(10174),

    SVERDLOVSK_REGION(11162);

    private final int code;

    AutoRuGeoId(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
