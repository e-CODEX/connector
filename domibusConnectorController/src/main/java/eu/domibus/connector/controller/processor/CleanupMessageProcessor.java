package eu.domibus.connector.controller.processor;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.persistence.service.DCMessageContentManager;
import org.springframework.stereotype.Service;

/**
 * This processor is called after a business message
 * has been rejected or confirmed
 * <p>
 * delegates deletion of message content
 */
@Service
public class CleanupMessageProcessor implements DomibusConnectorMessageProcessor {
    private final DCMessageContentManager dcMessageContentManager;

    public CleanupMessageProcessor(DCMessageContentManager dcMessageContentManager) {
        this.dcMessageContentManager = dcMessageContentManager;
    }

    @Override
    public void processMessage(DomibusConnectorMessage message) {
        dcMessageContentManager.cleanForMessage(message);
    }
}
