package eu.domibus.connector.ui.view.areas.configuration.link;

import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;

import java.util.List;


public class LnkConfigItem {

    private LinkType linkType;
    private NewConfig newConfig;
    private LinkPlugin linkPlugin;
    private String linkConfigName = "";
    private DomibusConnectorLinkConfiguration linkConfiguration;
    private List<DomibusConnectorLinkPartner> linkPartnerList;
    private DomibusConnectorLinkPartner linkPartner;

    public DomibusConnectorLinkConfiguration getLinkConfiguration() {
        return linkConfiguration;
    }

    public void setLinkConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        this.linkConfiguration = linkConfiguration;
    }

    public List<DomibusConnectorLinkPartner> getLinkPartnerList() {
        return linkPartnerList;
    }

    public void setLinkPartnerList(List<DomibusConnectorLinkPartner> linkPartnerList) {
        this.linkPartnerList = linkPartnerList;
    }

    public NewConfig getNewConfig() {
        return newConfig;
    }

    public void setNewConfig(NewConfig newConfig) {
        this.newConfig = newConfig;
    }

    public String getLinkConfigName() {
        return linkConfigName;
    }

    public void setLinkConfigName(String linkConfigName) {
        this.linkConfigName = linkConfigName;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public DomibusConnectorLinkPartner getLinkPartner() {
        return linkPartner;
    }

    public void setLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        this.linkPartner = linkPartner;
    }

    public LinkPlugin getLinkPlugin() {
        return linkPlugin;
    }

    public void setLinkPlugin(LinkPlugin linkPlugin) {
        this.linkPlugin = linkPlugin;
    }

    public enum NewConfig {
        NEW_LINK_CONFIG("Create new Link Configuration"),
        EXISTING_LINK_CONFIG("Use existing Link Configuration");

        private final String humanString;

        NewConfig(String s) {
            this.humanString = s;
        }

        public String getHumanString() {
            return humanString;
        }
    }
}
