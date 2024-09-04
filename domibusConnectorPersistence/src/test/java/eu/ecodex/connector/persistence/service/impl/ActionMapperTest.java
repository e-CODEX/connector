/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.domain.test.util.DomainEntityCreatorForPersistenceTests;
import eu.ecodex.connector.persistence.model.PDomibusConnectorAction;
import eu.ecodex.connector.persistence.model.test.util.PersistenceEntityCreator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MissingJavadocType")
class ActionMapperTest {
    @Test
    void mapActionToDomain() {
        PDomibusConnectorAction dbAction = PersistenceEntityCreator.createRelayREMMDFailureAction();
        DomibusConnectorAction action = ActionMapper.mapActionToDomain(dbAction);
        assertThat(action.getAction()).isEqualTo("RelayREMMDFailure");
    }

    @Test
    void mapActionToDomain_null_shouldRetNull() {
        assertThat(ActionMapper.mapActionToDomain(null)).isNull();
    }

    @SuppressWarnings("checkstyle:LocalVariableName")
    @Test
    void testMapActionToPersistence() {
        DomibusConnectorAction createActionForm_A =
            DomainEntityCreatorForPersistenceTests.createActionForm_A();
        PDomibusConnectorAction action = ActionMapper.mapActionToPersistence(createActionForm_A);
        assertThat(action.getAction()).as("must match").isEqualTo("Form_A");
    }

    @Test
    void testMapActionToPersistence_null_shouldRetNull() {
        assertThat(ActionMapper.mapActionToPersistence(null)).isNull();
    }
}
