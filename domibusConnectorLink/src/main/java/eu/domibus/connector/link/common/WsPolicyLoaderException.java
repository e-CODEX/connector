/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.link.common;

import lombok.NoArgsConstructor;

/**
 * Exception thrown by the WsPolicyLoader class when there is an error loading or handling web
 * service policies.
 */
@NoArgsConstructor
public class WsPolicyLoaderException extends RuntimeException {
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
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
