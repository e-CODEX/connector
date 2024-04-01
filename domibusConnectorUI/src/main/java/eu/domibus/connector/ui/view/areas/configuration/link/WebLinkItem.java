package eu.domibus.connector.ui.view.areas.configuration.link;

import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;

import java.util.Objects;
import java.util.Optional;


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

    public void setLinkPlugin(LinkPlugin linkPlugin) {
        this.linkPlugin = linkPlugin;
    }

    public static class WebLinkConfigurationItem extends WebLinkItem {
        private DomibusConnectorLinkConfiguration linkConfiguration;

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
        public int hashCode() {
            return linkConfiguration != null ? linkConfiguration.hashCode() : 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WebLinkConfigurationItem)) return false;

            WebLinkConfigurationItem that = (WebLinkConfigurationItem) o;

            return Objects.equals(linkConfiguration, that.linkConfiguration);
        }
    }

    public static class WebLinkPartnerItem extends WebLinkItem {
        private DomibusConnectorLinkPartner linkPartner;

        public DomibusConnectorLinkPartner getLinkPartner() {
            return linkPartner;
        }

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

        public void setLinkPartner(DomibusConnectorLinkPartner linkPartner) {
            this.linkPartner = linkPartner;
        }

        @Override
        public int hashCode() {
            return linkPartner != null ? linkPartner.hashCode() : 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WebLinkPartnerItem)) return false;

            WebLinkPartnerItem that = (WebLinkPartnerItem) o;

            return Objects.equals(linkPartner, that.linkPartner);
        }
    }
}
