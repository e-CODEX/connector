package eu.domibus.connector.link.api.exception;

public class LinkPluginException extends RuntimeException {
    public LinkPluginException() {
    }

    public LinkPluginException(String message) {
        super(message);
    }

    public LinkPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinkPluginException(Throwable cause) {
        super(cause);
    }

    public LinkPluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
