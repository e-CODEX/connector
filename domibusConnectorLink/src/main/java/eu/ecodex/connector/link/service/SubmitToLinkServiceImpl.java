/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.service;

import eu.ecodex.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.ecodex.connector.controller.service.SubmitToLinkService;
import eu.ecodex.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.ecodex.connector.domain.enums.MessageTargetSource;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.lib.logging.MDC;
import eu.ecodex.connector.tools.LoggingMDCPropertyNames;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implementation of the SubmitToLinkService interface.
 * This class is responsible for submitting a message to a link partner.
 */
@Service
public class SubmitToLinkServiceImpl implements SubmitToLinkService {
    public static final String SUBMIT_TO_LINK_SERVICE = "SubmitToLinkPartnerService";
    private final DCActiveLinkManagerService dcActiveLinkManagerService;

    public SubmitToLinkServiceImpl(DCActiveLinkManagerService activeLinkManagerService) {
        this.dcActiveLinkManagerService = activeLinkManagerService;
    }

    @Override
    @MDC(
        name = LoggingMDCPropertyNames.MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME,
        value = SUBMIT_TO_LINK_SERVICE
    )
    public void submitToLink(DomibusConnectorMessage message)
        throws DomibusConnectorSubmitToLinkException {
        DomibusConnectorMessageDirection direction = message.getMessageDetails().getDirection();
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName;
        if (direction.getTarget() == MessageTargetSource.BACKEND) {
            if (!StringUtils.hasLength(
                message.getMessageDetails().getConnectorBackendClientName())) {
                throw new DomibusConnectorSubmitToLinkException(
                    message, "The backendClientName is empty!");
            }
            linkPartnerName = new DomibusConnectorLinkPartner.LinkPartnerName(
                message.getMessageDetails().getConnectorBackendClientName());
        } else if (direction.getTarget() == MessageTargetSource.GATEWAY) {
            if (!StringUtils.hasLength(message.getMessageDetails().getGatewayName())) {
                throw new DomibusConnectorSubmitToLinkException(
                    message, "The gatewayName is empty!");
            }
            linkPartnerName = new DomibusConnectorLinkPartner.LinkPartnerName(
                message.getMessageDetails().getGatewayName());
        } else {
            throw new IllegalArgumentException("MessageTarget not valid!");
        }

        Optional<SubmitToLinkPartner> submitToLinkPartner =
            dcActiveLinkManagerService.getSubmitToLinkPartner(linkPartnerName);
        if (submitToLinkPartner.isPresent()) {
            submitToLinkPartner.ifPresent(s -> s.submitToLink(message, linkPartnerName));
        } else {
            var errorMessage =
                String.format(
                    "The LinkPartner with name [%s] could not be found/is not active!",
                    linkPartnerName
                );
            throw new DomibusConnectorSubmitToLinkException(message, errorMessage);
        }
    }
}
