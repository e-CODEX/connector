/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.link;

import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import java.util.Optional;
import lombok.Data;
import lombok.Setter;

/**
 * The abstract class WebLinkItem represents an item used in web links. It provides common
 * functionality and properties for both link partners and link configurations.
 */
@Setter
public abstract class WebLinkItem {
    private LinkPlugin linkPlugin;

    public DomibusConnectorLinkPartner getLinkPartner() {
        return null;
    }

    public DomibusConnectorLinkConfiguration getLinkConfiguration() {
        return null;
    }

    public abstract String getName();

    public abstract ConfigurationSource getConfigurationSource();

    public Boolean isEnabled() {
        return null;
    }

    public Optional<LinkPlugin> getLinkPlugin() {
        return Optional.ofNullable(this.linkPlugin);
    }

    /**
     * The WebLinkConfigurationItem class represents a configuration item for a web link. It extends
     * the WebLinkItem class. It contains a link configuration object and provides methods to access
     * and modify the link configuration.
     *
     * @see WebLinkItem
     */
    @Setter
    public static class WebLinkConfigurationItem extends WebLinkItem {
        private DomibusConnectorLinkConfiguration linkConfiguration;

        @Override
        public DomibusConnectorLinkConfiguration getLinkConfiguration() {
            return linkConfiguration;
        }

        @Override
        public String getName() {
            return linkConfiguration.getConfigName().toString();
        }

        @Override
        public ConfigurationSource getConfigurationSource() {
            return linkConfiguration.getConfigurationSource();
        }

        public void setLinkConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
            this.linkConfiguration = linkConfiguration;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof WebLinkConfigurationItem that)) {
                return false;
            }

            return linkConfiguration != null
                ? linkConfiguration.equals(that.linkConfiguration)
                : that.linkConfiguration == null;
        }

        @Override
        public int hashCode() {
            return linkConfiguration != null ? linkConfiguration.hashCode() : 0;
        }
    }

    /**
     * The {@code WebLinkPartnerItem} class extends the {@link WebLinkItem} class and represents a
     * link partner item used in web links.
     *
     * @see WebLinkItem
     */
    @Data
    public static class WebLinkPartnerItem extends WebLinkItem {
        private DomibusConnectorLinkPartner linkPartner;

        @Override
        public String getName() {
            return linkPartner.getLinkPartnerName().toString();
        }

        @Override
        public ConfigurationSource getConfigurationSource() {
            return linkPartner.getConfigurationSource();
        }

        @Override
        public Boolean isEnabled() {
            return linkPartner.isEnabled();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof WebLinkPartnerItem that)) {
                return false;
            }

            return linkPartner != null
                ? linkPartner.equals(that.linkPartner)
                : that.linkPartner == null;
        }

        @Override
        public int hashCode() {
            return linkPartner != null ? linkPartner.hashCode() : 0;
        }
    }
}
