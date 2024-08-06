/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.link.service;

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.persistence.service.DCLinkPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * Provides a health indicator for the connector link plugins the health goes to down if a plugin
 * which should be UP is actually not running.
 */
//@Component
//@Profile(LINK_PLUGIN_PROFILE_NAME)
public class DCActiveLinkHealthIndicator extends AbstractHealthIndicator {
    @Autowired
    DCActiveLinkManagerService dcActiveLinkManagerService;
    @Autowired
    DCLinkPersistenceService dcLinkPersistenceService;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.up();
        dcLinkPersistenceService.getAllEnabledLinks()
                                .forEach(enabledLink -> this.checkLink(builder, enabledLink));
    }

    @SuppressWarnings("squid:S1135")
    private void checkLink(Health.Builder builder, DomibusConnectorLinkPartner enabledLink) {
        var linkPartnerName = enabledLink.getLinkPartnerName();

        // Object activeLinkPartner = dcActiveLinkManagerService
        // .getActiveLinkPartner(linkPartnerName);
        // if (activeLinkPartnerOptional.isPresent()) {
        //     ActiveLinkPartnerManager activeLinkPartner = activeLinkPartnerOptional.get();
        //     ActiveLinkManager activeLink = activeLinkPartner.getActiveLink();
        //     if (activeLink.getLinkState()) {
        //         builder.withDetail("linkpartner_" + linkPartnerName, Status.UP);
        //     } else {
        //         builder.withDetail("linkpartner_" + linkPartnerName, Status.DOWN);
        //     }
        // } else {
        //     builder.down();
        //     builder.withDetail("linkpartner_" + linkPartnerName, Status.DOWN);
        // }
    }
}
