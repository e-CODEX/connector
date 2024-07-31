/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.test.service;

import eu.domibus.connector.common.DomibusConnectorDefaults;
import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Action;
import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Service;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain.BusinessDomainId;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * The DCConnector2ConnectorTestService interface provides methods to interact with the Domibus
 * Connector for submitting test messages and retrieving test backend information.
 */
public interface DCConnector2ConnectorTestService {
    /**
     * Submits a test message to the Domibus Connector.
     *
     * @param testMessage the test message to be submitted
     */
    void submitTestMessage(DomibusConnectorMessageType testMessage);

    /**
     * Returns the configured name of the test backend, every message sent to this backend is a
     * connector to connector test message.
     *
     * @return the backend name
     */
    default String getTestBackendName() {
        return DomibusConnectorDefaults.DEFAULT_TEST_BACKEND;
    }

    AS4Service getTestService(BusinessDomainId laneId);

    AS4Action getTestAction(BusinessDomainId laneId);
}
