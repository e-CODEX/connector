/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connectorplugins.link.testbackend;

import eu.domibus.connector.c2ctests.config.ConnectorTestConfigurationProperties;
import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Action;
import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Service;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain.BusinessDomainId;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.test.service.DCConnector2ConnectorTestService;
import org.springframework.stereotype.Service;

/**
 * The DCConnector2ConnectorTestServiceImpl class implements the DCConnector2ConnectorTestService
 * interface. It provides methods to interact with the Domibus Connector for submitting test
 * messages and retrieving test backend information.
 */
@Service
public class DCConnector2ConnectorTestServiceImpl implements DCConnector2ConnectorTestService {
    private final ConfigurationPropertyManagerService configurationPropertyLoaderService;
    private final DomibusConnectorDomainMessageTransformerService transformerService;
    private final DomibusConnectorMessageIdGenerator messageIdGenerator;
    private final SubmitToConnector submitToConnector;

    /**
     * The DCConnector2ConnectorTestServiceImpl class implements the
     * DCConnector2ConnectorTestService interface. It provides methods to interact with the Domibus
     * Connector for submitting test messages and retrieving test backend information.
     */
    public DCConnector2ConnectorTestServiceImpl(
        ConfigurationPropertyManagerService configurationPropertyLoaderService,
        DomibusConnectorDomainMessageTransformerService transformerService,
        DomibusConnectorMessageIdGenerator messageIdGenerator,
        SubmitToConnector submitToConnector) {
        this.configurationPropertyLoaderService = configurationPropertyLoaderService;
        this.transformerService = transformerService;
        this.messageIdGenerator = messageIdGenerator;
        this.submitToConnector = submitToConnector;
    }

    @Override
    public void submitTestMessage(DomibusConnectorMessageType testMessage) {
        var domibusConnectorMessage = transformerService.transformTransitionToDomain(
            testMessage,
            messageIdGenerator.generateDomibusConnectorMessageId()
        );
        submitToConnector.submitToConnector(
            domibusConnectorMessage,
            new DomibusConnectorLinkPartner.LinkPartnerName(getTestBackendName()), LinkType.BACKEND
        );
    }

    @Override
    public AS4Service getTestService(BusinessDomainId laneId) {
        ConnectorTestConfigurationProperties c2cTestProperties =
            configurationPropertyLoaderService.loadConfiguration(
                laneId,
                ConnectorTestConfigurationProperties.class
            );
        return c2cTestProperties.getService();
    }

    @Override
    public AS4Action getTestAction(BusinessDomainId laneId) {
        ConnectorTestConfigurationProperties c2cTestProperties =
            configurationPropertyLoaderService.loadConfiguration(
                laneId,
                ConnectorTestConfigurationProperties.class
            );
        return c2cTestProperties.getAction();
    }
}
