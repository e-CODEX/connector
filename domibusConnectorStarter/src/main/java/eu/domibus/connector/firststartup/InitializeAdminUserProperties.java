package eu.domibus.connector.firststartup;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.UUID;


@ConfigurationProperties(prefix = InitializeAdminUserProperties.PREFIX)
public class InitializeAdminUserProperties {
    public static final String PREFIX = "connector.init.user";

    private boolean enabled;
    private boolean initialChangeRequired = true;
    private String initialUserName = "admin";
    private boolean logInitialToConsole = true;
    private String initialUserPassword = UUID.randomUUID().toString();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInitialChangeRequired() {
        return initialChangeRequired;
    }

    public void setInitialChangeRequired(boolean initialChangeRequired) {
        this.initialChangeRequired = initialChangeRequired;
    }

    public boolean isLogInitialToConsole() {
        return logInitialToConsole;
    }

    public void setLogInitialToConsole(boolean logInitialToConsole) {
        this.logInitialToConsole = logInitialToConsole;
    }

    public String getInitialUserName() {
        return initialUserName;
    }

    public void setInitialUserName(String initialUserName) {
        this.initialUserName = initialUserName;
    }

    public String getInitialUserPassword() {
        return initialUserPassword;
    }

    public void setInitialUserPassword(String initialUserPassword) {
        this.initialUserPassword = initialUserPassword;
    }
}
