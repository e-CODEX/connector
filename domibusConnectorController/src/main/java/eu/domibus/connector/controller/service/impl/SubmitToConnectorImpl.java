package eu.domibus.connector.controller.service.impl;

import eu.domibus.connector.controller.queues.producer.ToConnectorQueue;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;


@Service
public class SubmitToConnectorImpl implements SubmitToConnector {
    private final ToConnectorQueue toConnectorQueue;

    // TODO Adding TransactionTemplate manually fixes a bug where the @Transactional Annotation was ignored for some
    //  yet unknown reason
    // TODO This happened when sending a message from connector client, maybe because it came from different
    //  Application Context
    // TODO investigate and fix
    private final TransactionTemplate txTemplate;

    public SubmitToConnectorImpl(ToConnectorQueue toConnectorQueue, TransactionTemplate txTemplate) {
        this.toConnectorQueue = toConnectorQueue;
        this.txTemplate = txTemplate;
    }

    @Override
    public void submitToConnector(
            DomibusConnectorMessage message,
            DomibusConnectorLinkPartner.LinkPartnerName linkPartner,
            LinkType linkType) {
        txTemplate.execute((t) -> {
            if (linkType == LinkType.GATEWAY) {
                message.getMessageDetails().setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
                message.getMessageDetails().setGatewayName(linkPartner.getLinkName());
                toConnectorQueue.putOnQueue(message);
            } else if (linkType == LinkType.BACKEND) {
                message.getMessageDetails().setDirection(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
                message.getMessageDetails().setConnectorBackendClientName(linkPartner.getLinkName());
                toConnectorQueue.putOnQueue(message);
            } else {
                throw new RuntimeException("linkType not known!");
            }
            return null;
        });
    }
}
