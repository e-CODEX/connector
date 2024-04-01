package eu.domibus.connector.ui.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "connector.ui")
public class ConnectorUiConfigurationProperties {
    /**
     * should the user be automaticall logged in
     * should only be used for development
     */
    boolean autoLoginEnabled = false;

    /**
     * which user should be automatically logged in?
     * note: user must exist in database!
     */
    String autoLoginUser = "";

    String autoLoginPassword = "";

    String defaultRoute = null;

    public boolean isAutoLoginEnabled() {
        return autoLoginEnabled;
    }

    public void setAutoLoginEnabled(boolean autoLoginEnabled) {
        this.autoLoginEnabled = autoLoginEnabled;
    }

    public String getAutoLoginUser() {
        return autoLoginUser;
    }

    public void setAutoLoginUser(String autoLoginUser) {
        this.autoLoginUser = autoLoginUser;
    }

    public String getAutoLoginPassword() {
        return autoLoginPassword;
    }

    public void setAutoLoginPassword(String autoLoginPassword) {
        this.autoLoginPassword = autoLoginPassword;
    }

    public String getDefaultRoute() {
        return defaultRoute;
    }

    public void setDefaultRoute(String defaultRoute) {
        this.defaultRoute = defaultRoute;
    }
}
