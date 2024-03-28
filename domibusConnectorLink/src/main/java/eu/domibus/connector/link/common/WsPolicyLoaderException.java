package eu.domibus.connector.link.common;

public class WsPolicyLoaderException extends RuntimeException {
    public WsPolicyLoaderException() {
    }

    public WsPolicyLoaderException(String message) {
        super(message);
    }

    public WsPolicyLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public WsPolicyLoaderException(Throwable cause) {
        super(cause);
    }

    public WsPolicyLoaderException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
