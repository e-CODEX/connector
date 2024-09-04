/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.transport;

import eu.ecodex.connector.controller.service.SubmitToLinkService;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorTransportStep;
import eu.ecodex.connector.domain.model.helper.DomainModelHelper;
import eu.ecodex.connector.persistence.service.DCMessagePersistenceService;
import org.springframework.stereotype.Service;

/**
 * Implementation of the DCTransportRetryService interface that handles the retry logic of a
 * transport step.
 */
@Service
public class DCTransportRetryServiceImpl implements DCTransportRetryService {
    private final SubmitToLinkService submitToLinkService;
    private final DCMessagePersistenceService messagePersistenceService;

    public DCTransportRetryServiceImpl(SubmitToLinkService submitToLinkService,
                                       DCMessagePersistenceService messagePersistenceService) {
        this.submitToLinkService = submitToLinkService;
        this.messagePersistenceService = messagePersistenceService;
    }

    @Override
    public void retryTransport(DomibusConnectorTransportStep step) {
        step.getTransportedMessage().ifPresent(submitToLinkService::submitToLink);
    }

    @Override
    public boolean isRetryAble(DomibusConnectorTransportStep step) {
        boolean retryPossible = true;
        if (step.getTransportedMessage().isPresent()) {
            DomibusConnectorMessage msg = step.getTransportedMessage().get();
            if (DomainModelHelper.isBusinessMessage(msg)) {
                retryPossible = !messagePersistenceService.checkMessageConfirmedOrRejected(msg);
            }
        } else {
            retryPossible = false;
        }
        return retryPossible;
    }
}
