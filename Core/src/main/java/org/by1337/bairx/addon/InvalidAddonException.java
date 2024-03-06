package org.by1337.bairx.addon;

public class InvalidAddonException extends Exception {
    public InvalidAddonException() {
    }

    public InvalidAddonException(String message) {
        super(message);
    }

    public InvalidAddonException(String message, Object... objects) {
        super(String.format(message, objects));
    }

    public InvalidAddonException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAddonException(String message, Throwable cause, Object... objects) {
        super(String.format(message, objects), cause);
    }


    public InvalidAddonException(Throwable cause) {
        super(cause);
    }

}
