package com.maxzxwd.autoruc.utils;

import org.springframework.lang.NonNull;

import java.util.function.Function;

public final class JavaUtils {
    private JavaUtils() {}

    @FunctionalInterface
    public interface CheckedSupplier<T> {
        T get() throws java.lang.Throwable;
    }

    public static <T> @NonNull T uncheck(@NonNull CheckedSupplier<T> supplier, Function<Throwable,
                RuntimeException> exceptionWrapper) {

        try {
            return supplier.get();
        } catch (Throwable e) {
            throw exceptionWrapper.apply(e);
        }
    }
}
