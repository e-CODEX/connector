/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.link;

import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import java.util.List;
import lombok.Data;
import lombok.Getter;

/**
 * The LnkConfigItem class represents a configuration item for a link connector in Domibus.
 */
@Data
public class LnkConfigItem {
    /**
     * The NewConfig enum represents the two options for configuring a link in Domibus. It has two
     * enum constants: - NEW_LINK_CONFIG: Indicates that a new Link Configuration should be created.
     * - EXISTING_LINK_CONFIG: Indicates that an existing Link Configuration should be used.
     */
    @Getter
    public enum NewConfig {
        NEW_LINK_CONFIG("Create new Link Configuration"),
        EXISTING_LINK_CONFIG("Use existing Link Configuration");
        private final String humanString;

        NewConfig(String s) {
            this.humanString = s;
        }
    }

    private LinkType linkType;
    private NewConfig newConfig;
    private LinkPlugin linkPlugin;
    private String linkConfigName = "";
    private DomibusConnectorLinkConfiguration linkConfiguration;
    private List<DomibusConnectorLinkPartner> linkPartnerList;
    private DomibusConnectorLinkPartner linkPartner;
}
