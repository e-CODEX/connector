package eu.domibus.connector.firststartup;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.style.ToStringCreator;

import java.util.ArrayList;
import java.util.List;


@ConfigurationProperties(prefix = UploadPModesOnFirstStartConfigurationProperties.PREFIX)
public class UploadPModesOnFirstStartConfigurationProperties {
    public static final String PREFIX = "connector.init.pmode";

    private List<PModeUpload> upload = new ArrayList<>();
    private boolean enabled;

    public List<PModeUpload> getUpload() {
        return upload;
    }

    public void setUpload(List<PModeUpload> upload) {
        this.upload = upload;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static class PModeUpload {
        private String businessDomainName = DomibusConnectorBusinessDomain.DEFAULT_LANE_NAME;
        private StoreConfigurationProperties trustStore;
        private Resource pModeXml;

        public String getBusinessDomainName() {
            return businessDomainName;
        }

        public void setBusinessDomainName(String businessDomainName) {
            this.businessDomainName = businessDomainName;
        }

        public StoreConfigurationProperties getTrustStore() {
            return trustStore;
        }

        public void setTrustStore(StoreConfigurationProperties trustStore) {
            this.trustStore = trustStore;
        }

        public Resource getpModeXml() {
            return pModeXml;
        }

        public void setpModeXml(Resource pModeXml) {
            this.pModeXml = pModeXml;
        }

        public String toString() {
            return new ToStringCreator(this)
                    .append("bussinessDomain", this.businessDomainName)
                    .append("trustStore", this.trustStore)
                    .toString();
        }
    }
}
