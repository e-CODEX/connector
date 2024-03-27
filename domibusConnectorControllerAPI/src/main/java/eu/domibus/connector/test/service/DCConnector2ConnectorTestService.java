package eu.domibus.connector.test.service;

import eu.domibus.connector.common.DomibusConnectorDefaults;
import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Action;
import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Service;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain.BusinessDomainId;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;


public interface DCConnector2ConnectorTestService {
    /**
     * @param testMessage - a test message
     */
    void submitTestMessage(DomibusConnectorMessageType testMessage);

    /**
     * returns the configured name of the test backend, every message sent to this
     * backend is a connector to connector test message
     *
     * @return the backend name
     */
    default String getTestBackendName() {
        return DomibusConnectorDefaults.DEFAULT_TEST_BACKEND;
    }

    AS4Service getTestService(BusinessDomainId laneId);

    AS4Action getTestAction(BusinessDomainId laneId);
}
