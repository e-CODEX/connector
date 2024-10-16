/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.processor.steps;

import eu.ecodex.connector.common.service.ConfigurationPropertyManagerService;
import eu.ecodex.connector.controller.exception.DomibusConnectorMessageException;
import eu.ecodex.connector.controller.processor.util.ConfirmationCreatorService;
import eu.ecodex.connector.domain.configuration.EvidenceActionServiceConfigurationProperties;
import eu.ecodex.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Does some validation if the Action of this evidence message is correct in respect
 * of the transported evidence type.
 */
@Component
public class ValidateMessageConfirmationStep implements MessageProcessStep {
    private static final Logger LOGGER =
        LogManager.getLogger(ValidateMessageConfirmationStep.class);
    private final ConfirmationCreatorService confirmationCreatorService;
    private final ConfigurationPropertyManagerService configurationPropertyLoaderService;

    public ValidateMessageConfirmationStep(
        ConfirmationCreatorService confirmationCreatorService,
        ConfigurationPropertyManagerService configurationPropertyLoaderService) {
        this.confirmationCreatorService = confirmationCreatorService;
        this.configurationPropertyLoaderService = configurationPropertyLoaderService;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_STEP_PROCESSOR_PROPERTY_NAME,
        value = "ValidateMessageConfirmationStep"
    )
    public boolean executeStep(final DomibusConnectorMessage domibusConnectorMessage) {
        domibusConnectorMessage
            .getTransportedMessageConfirmations()
            .forEach(c -> this.validateConfirmation(domibusConnectorMessage, c));

        if (domibusConnectorMessage.getTransportedMessageConfirmations().size() == 1) {
            validateActionService(domibusConnectorMessage);
        }
        return true;
    }

    private void validateActionService(DomibusConnectorMessage domibusConnectorMessage) {
        EvidenceActionServiceConfigurationProperties evidenceActionServiceConfigurationProperties =
            configurationPropertyLoaderService.loadConfiguration(
                domibusConnectorMessage.getMessageLaneId(),
                EvidenceActionServiceConfigurationProperties.class
            );
        boolean enforcing =
            evidenceActionServiceConfigurationProperties.isEnforceServiceActionNames();

        DomibusConnectorMessageConfirmation confirmation =
            domibusConnectorMessage.getTransportedMessageConfirmations().getFirst();
        DomibusConnectorEvidenceType evidenceType = confirmation.getEvidenceType();
        DomibusConnectorAction requiredEvidenceAction =
            confirmationCreatorService.createEvidenceAction(evidenceType);

        DomibusConnectorAction action = domibusConnectorMessage.getMessageDetails().getAction();
        if (!requiredEvidenceAction.equals(action)) {
            var error = String.format(
                "Enforcing the AS4 action is [%s] and the action [%s] is illegal for this "
                    + "type [%s] of evidence",
                enforcing, action, evidenceType
            );
            if (enforcing) {
                throwError(domibusConnectorMessage, error);
            } else {
                LOGGER.warn(error);
            }
        }
    }

    private void validateConfirmation(DomibusConnectorMessage msg,
                                      DomibusConnectorMessageConfirmation confirmation) {
        if (confirmation.getEvidenceType() == null) {
            throwError(msg, "The evidence type is null!");
        }
        if (confirmation.getEvidence() == null) {
            throwError(msg, "The evidence itself is null!");
        }
    }

    private void throwError(DomibusConnectorMessage msg, String text) {
        throw new DomibusConnectorMessageException(
            msg, ValidateMessageConfirmationStep.class, text);
    }
}
