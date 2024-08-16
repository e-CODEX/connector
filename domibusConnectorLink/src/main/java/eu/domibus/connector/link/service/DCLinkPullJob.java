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
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

/**
 * This class represents a job for pulling messages from a designated link partner.
 */
public class DCLinkPullJob implements Job {
    private static final Logger LOGGER = LogManager.getLogger(DCLinkPullJob.class);
    public static final String LINK_PARTNER_NAME_PROPERTY_NAME = "linkPartnerName";
    private final DCActiveLinkManagerService dcActiveLinkManagerService;

    public DCLinkPullJob(DCActiveLinkManagerService dcActiveLinkManagerService) {
        this.dcActiveLinkManagerService = dcActiveLinkManagerService;
    }

    @SuppressWarnings("squid:S1135")
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try (var mdcCloseable = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME,
            DCLinkPullJob.class.getSimpleName()
        )) {
            var linkPartnerName = context
                .getMergedJobDataMap()
                .getString(LINK_PARTNER_NAME_PROPERTY_NAME);
            LOGGER.debug("Running pull messages job for linkPartner [{}]", linkPartnerName);

            Optional<PullFromLinkPartner> pullFromLinkPartner =
                dcActiveLinkManagerService.getPullFromLinkPartner(linkPartnerName);

            pullFromLinkPartner.ifPresent(p -> p.pullMessagesFrom(
                new DomibusConnectorLinkPartner.LinkPartnerName(linkPartnerName)));
        }
        // TODO: handle the case:
        //-) pull job still exists in db, but Link was removed from configuration offline
        // job tries to pull from non existant linkpartner
    }
}
