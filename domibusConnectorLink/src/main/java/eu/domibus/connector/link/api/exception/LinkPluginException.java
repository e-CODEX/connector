/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.link.api.exception;

/**
 * LinkPluginException is an exception that is thrown when a feature is not supported by a plugin.
 */
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

    public LinkPluginException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
