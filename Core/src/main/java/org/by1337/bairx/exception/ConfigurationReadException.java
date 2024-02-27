package org.by1337.bairx.exception;

public class ConfigurationReadException  extends RuntimeException {
    public ConfigurationReadException() {
    }

    public ConfigurationReadException(String message, Object... objects) {
        this(String.format(message, objects));
    }

    public ConfigurationReadException(String message) {
        super(message);
    }

    public ConfigurationReadException(String message, Throwable cause, Object... objects) {
        this(String.format(message, objects), cause);
    }

    public ConfigurationReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationReadException(Throwable cause) {
        super(cause);
    }

    public ConfigurationReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
