package eu.domibus.connector.controller.spring;

//import eu.domibus.connector.configuration.annotation.ConfigurationLabel;
import eu.domibus.connector.lib.spring.DomibusConnectorDuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

@Component(ContentDeletionConfigurationProperties.BEAN_NAME)
@ConfigurationProperties(prefix = ContentDeletionConfigurationProperties.PREFIX)
@Validated
public class ContentDeletionConfigurationProperties {

    public static final String BEAN_NAME = "ContentDeletionConfigurationProperties";

    public static final String PREFIX = "connector.controller.content";

    private boolean checkTimeoutEnabled = true;

    /**
     *
     */
    private DomibusConnectorDuration checkTimeout;

    public DomibusConnectorDuration getCheckTimeout() {
        return checkTimeout;
    }

    public void setCheckTimeout(DomibusConnectorDuration checkTimeout) {
        this.checkTimeout = checkTimeout;
    }

    public boolean isCheckTimeoutEnabled() {
        return checkTimeoutEnabled;
    }

    public void setCheckTimeoutEnabled(boolean checkTimeoutEnabled) {
        this.checkTimeoutEnabled = checkTimeoutEnabled;
    }

    @PostConstruct
    public void validate() {
        if (checkTimeoutEnabled && checkTimeout == null) {
            String error = String.format("If %s.check-timeout-enabled is true, then %s.check-timeout must be set to a duration (eg. 24h)", PREFIX, PREFIX);
            throw new IllegalArgumentException(error);
        }
    }

}
