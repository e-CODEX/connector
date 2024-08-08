package eu.domibus.connector.persistence.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.test.util.DomainEntityCreatorForPersistenceTests;
import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import eu.domibus.connector.persistence.model.test.util.PersistenceEntityCreator;
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
