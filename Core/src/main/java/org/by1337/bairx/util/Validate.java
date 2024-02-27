package org.by1337.bairx.util;


import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.by1337.bairx.exception.PluginInitializationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Validate {

    public static <T, R> R tryMap(T raw, ThrowableFunction<T, R> mapper, String message, Object... objects) throws PluginInitializationException {
        return tryMap(raw, mapper, String.format(message, objects));
    }

    public static <T, R> R tryMap(T raw, ThrowableFunction<T, R> mapper, String message) throws PluginInitializationException {
        try {
            return mapper.apply(raw);
        } catch (Exception e) {
            throw new PluginInitializationException(message, e);
        }
    }

    @CanIgnoreReturnValue
    @NotNull
    public static <T> T notNull(@Nullable T obj, @NotNull String message, @NotNull Object... objects) {
        return notNull(obj, String.format(message, objects));

    }

    @CanIgnoreReturnValue
    @NotNull
    public static <T> T notNull(@Nullable T obj) {
        return notNull(obj, null);
    }

    @CanIgnoreReturnValue
    @NotNull
    @SuppressWarnings("ConstantConditions")
    public static <T> T notNull(@Nullable T obj, @Nullable String message) {
        return test(obj, Objects::isNull, () -> new NullPointerException(message));
    }

    @CanIgnoreReturnValue
    public static <T, X extends Throwable> T test(@Nullable T obj, @NotNull Predicate<T> test, @NotNull Supplier<@NotNull X> supplier) throws X {
        if (test.test(obj)) {
            throw supplier.get();
        }
        return obj;
    }

    @FunctionalInterface
    public interface ThrowableFunction<T, R> {

        /**
         * Applies this function to the given argument.
         *
         * @param t the function argument
         * @return the function result
         */
        R apply(T t) throws Exception;
    }
}
