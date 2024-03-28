package eu.domibus.connector.link.api.exception;

import eu.domibus.connector.link.api.PluginFeature;


public class FeatureNotSupportedException extends LinkPluginException {
    private final PluginFeature pluginFeature;

    public FeatureNotSupportedException(PluginFeature pluginFeature) {
        this.pluginFeature = pluginFeature;
    }

    public FeatureNotSupportedException(String message, PluginFeature pluginFeature) {
        super(message);
        this.pluginFeature = pluginFeature;
    }

    public FeatureNotSupportedException(String message, Throwable cause, PluginFeature pluginFeature) {
        super(message, cause);
        this.pluginFeature = pluginFeature;
    }

    public FeatureNotSupportedException(Throwable cause, PluginFeature pluginFeature) {
        super(cause);
        this.pluginFeature = pluginFeature;
    }

    public FeatureNotSupportedException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace,
            PluginFeature pluginFeature) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.pluginFeature = pluginFeature;
    }

    public PluginFeature getPluginFeature() {
        return pluginFeature;
    }
}
