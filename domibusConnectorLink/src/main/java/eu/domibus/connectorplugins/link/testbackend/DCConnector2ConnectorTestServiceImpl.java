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
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.test.service.DCConnector2ConnectorTestService;
import org.springframework.stereotype.Service;


@Service
public class DCConnector2ConnectorTestServiceImpl implements DCConnector2ConnectorTestService {
    private final ConfigurationPropertyManagerService configurationPropertyLoaderService;
    private final DomibusConnectorDomainMessageTransformerService transformerService;
    private final DomibusConnectorMessageIdGenerator messageIdGenerator;
    private final SubmitToConnector submitToConnector;

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
        DomibusConnectorMessage domibusConnectorMessage =
                transformerService.transformTransitionToDomain(
                        testMessage,
                        messageIdGenerator.generateDomibusConnectorMessageId()
                );
        submitToConnector.submitToConnector(
                domibusConnectorMessage,
                new DomibusConnectorLinkPartner.LinkPartnerName(getTestBackendName()),
                LinkType.BACKEND
        );
    }

    @Override
    public AS4Service getTestService(BusinessDomainId laneId) {
        ConnectorTestConfigurationProperties c2cTestProperties = configurationPropertyLoaderService.loadConfiguration(
                laneId,
                ConnectorTestConfigurationProperties.class
        );
        return c2cTestProperties.getService();
    }

    @Override
    public AS4Action getTestAction(BusinessDomainId laneId) {
        ConnectorTestConfigurationProperties c2cTestProperties = configurationPropertyLoaderService.loadConfiguration(
                laneId,
                ConnectorTestConfigurationProperties.class
        );
        return c2cTestProperties.getAction();
    }
}
