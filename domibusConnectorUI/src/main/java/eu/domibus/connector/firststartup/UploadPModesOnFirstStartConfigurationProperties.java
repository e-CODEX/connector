/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.firststartup;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.style.ToStringCreator;

/**
 * The UploadPModesOnFirstStartConfigurationProperties class represents the configuration properties
 * for uploading PMode files on first start. It is annotated with @ConfigurationProperties to define
 * a prefix for the properties.
 */
@Data
@ConfigurationProperties(prefix = UploadPModesOnFirstStartConfigurationProperties.PREFIX)
public class UploadPModesOnFirstStartConfigurationProperties {
    public static final String PREFIX = "connector.init.pmode";
    private List<PModeUpload> upload = new ArrayList<>();
    private boolean enabled;

    /**
     * The PModeUpload class represents the configuration properties for uploading a PMode file.
     * It is a nested static class within the UploadPModesOnFirstStartConfigurationProperties class.
     */
    @Data
    @SuppressWarnings({"checkstyle:MemberName", "checkstyle:ParameterName"})
    public static class PModeUpload {
        private String businessDomainName = DomibusConnectorBusinessDomain.DEFAULT_LANE_NAME;
        private StoreConfigurationProperties trustStore;
        private Resource pModeXml;

        public Resource getpModeXml() {
            return pModeXml;
        }

        public void setpModeXml(Resource pModeXml) {
            this.pModeXml = pModeXml;
        }

        @Override
        public String toString() {
            return new ToStringCreator(this)
                .append("bussinessDomain", this.businessDomainName)
                .append("trustStore", this.trustStore)
                .toString();
        }
    }
}
