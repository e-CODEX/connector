/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.api.exception;

import eu.ecodex.connector.link.api.PluginFeature;
import lombok.Getter;

/**
 * Exception thrown when a feature is not supported by a plugin.
 */
@Getter
public class FeatureNotSupportedException extends LinkPluginException {
    private final PluginFeature pluginFeature;

    public FeatureNotSupportedException(PluginFeature pluginFeature) {
        this.pluginFeature = pluginFeature;
    }

    public FeatureNotSupportedException(String message, PluginFeature pluginFeature) {
        super(message);
        this.pluginFeature = pluginFeature;
    }

    public FeatureNotSupportedException(
        String message, Throwable cause, PluginFeature pluginFeature) {
        super(message, cause);
        this.pluginFeature = pluginFeature;
    }

    public FeatureNotSupportedException(Throwable cause, PluginFeature pluginFeature) {
        super(cause);
        this.pluginFeature = pluginFeature;
    }

    public FeatureNotSupportedException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
        PluginFeature pluginFeature) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.pluginFeature = pluginFeature;
    }
}
