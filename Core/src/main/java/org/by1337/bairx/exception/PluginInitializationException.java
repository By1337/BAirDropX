package org.by1337.bairx.exception;

public class PluginInitializationException extends RuntimeException {
    public PluginInitializationException() {
    }

    public PluginInitializationException(String message, Object... objects) {
        this(String.format(message, objects));
    }

    public PluginInitializationException(String message) {
        super(message);
    }

    public PluginInitializationException(String message, Throwable cause, Object... objects) {
        this(String.format(message, objects), cause);
    }

    public PluginInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginInitializationException(Throwable cause) {
        super(cause);
    }

    public PluginInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
