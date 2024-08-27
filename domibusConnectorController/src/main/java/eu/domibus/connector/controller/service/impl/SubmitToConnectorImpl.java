/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.service.impl;

import eu.domibus.connector.controller.queues.producer.ToConnectorQueue;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The SubmitToConnectorImpl class is an implementation of the SubmitToConnector interface.
 * It provides functionality to submit a message to the connector based on the link type.
 *
 * <p>This class uses a ToConnectorQueue object to put the message on the queue for
 * further processing.
 * It also has a TransactionTemplate object that fixes a bug where the @Transactional annotations
 * were ignored.
 *
 * <p>Example usage:
 * <pre>{@code
 * DomibusConnectorMessage message = new DomibusConnectorMessage();
 * SubmitToConnectorImpl submitToConnector = new SubmitToConnectorImpl(
 * new ToConnectorQueue(), new TransactionTemplate()
 * );
 * submitToConnector.submitToConnector(
 * message, DomibusConnectorLinkPartner.LinkPartnerName.PARTNER_NAME, LinkType.GATEWAY
 * );
 * }</pre>
 *
 * @see SubmitToConnector
 * @see ToConnectorQueue
 */
@Service
public class SubmitToConnectorImpl implements SubmitToConnector {
    private final ToConnectorQueue toConnectorQueue;
    @SuppressWarnings("squid:S1135")
    /*
    TODO Adding TransactionTemplate manually fixes a bug where the @Transactional Annotation
        was ignored for some yet unknown reason
    TODO This happened when sending a message from connector client, maybe because it came
        from different Application Context
    TODO investigate and fix
     */
    private final TransactionTemplate txTemplate;

    /**
     * Constructs a new SubmitToConnectorImpl.
     *
     * @param toConnectorQueue the ToConnectorQueue object to put the message on the queue for
     *                         further processing
     * @param txTemplate       the TransactionTemplate object to fix a bug where the @Transactional
     *                         annotations were ignored
     */
    public SubmitToConnectorImpl(ToConnectorQueue toConnectorQueue,
                                 TransactionTemplate txTemplate) {
        this.toConnectorQueue = toConnectorQueue;

        this.txTemplate = txTemplate;
    }

    @Override
    public void submitToConnector(DomibusConnectorMessage message,
                                  DomibusConnectorLinkPartner.LinkPartnerName linkPartner,
                                  LinkType linkType) {
        txTemplate.execute(t -> {
            if (linkType == LinkType.GATEWAY) {
                message.getMessageDetails()
                    .setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
                message.getMessageDetails().setGatewayName(linkPartner.getLinkName());
                toConnectorQueue.putOnQueue(message);
            } else if (linkType == LinkType.BACKEND) {
                message.getMessageDetails()
                    .setDirection(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
                message.getMessageDetails()
                    .setConnectorBackendClientName(linkPartner.getLinkName());
                toConnectorQueue.putOnQueue(message);
            } else {
                throw new RuntimeException("linkType not known!");
            }
            return null;
        });
    }
}
