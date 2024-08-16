/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
