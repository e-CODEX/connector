package eu.domibus.connector.link.service;

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

import java.util.Optional;


public class DCLinkPullJob implements Job {
    public static final String LINK_PARTNER_NAME_PROPERTY_NAME = "linkPartnerName";
    private static final Logger LOGGER = LogManager.getLogger(DCLinkPullJob.class);
    private final DCActiveLinkManagerService dcActiveLinkManagerService;

    public DCLinkPullJob(DCActiveLinkManagerService dcActiveLinkManagerService) {
        this.dcActiveLinkManagerService = dcActiveLinkManagerService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try (
                MDC.MDCCloseable mdcCloseable = MDC.putCloseable(
                        LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME,
                        DCLinkPullJob.class.getSimpleName()
                )
        ) {
            String linkPartnerName = context.getMergedJobDataMap().getString(LINK_PARTNER_NAME_PROPERTY_NAME);
            LOGGER.debug("Running pull messages job for linkPartner [{}]", linkPartnerName);

            Optional<PullFromLinkPartner> pullFromLinkPartner =
                    dcActiveLinkManagerService.getPullFromLinkPartner(linkPartnerName);

            pullFromLinkPartner.ifPresent((p) -> p.pullMessagesFrom(new DomibusConnectorLinkPartner.LinkPartnerName(
                    linkPartnerName)));
        }
        // TODO: handle the case:
        //-) pull job still exists in db, but Link was removed from configuration offline
        // job tries to pull from nonexistant linkpartner
    }
}
