/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.test.service;

import eu.ecodex.connector.common.DomibusConnectorDefaults;
import eu.ecodex.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Action;
import eu.ecodex.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Service;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain.BusinessDomainId;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;

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
