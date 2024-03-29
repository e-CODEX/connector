package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.test.util.DomainEntityCreatorForPersistenceTests;
import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import eu.domibus.connector.persistence.model.test.util.PersistenceEntityCreator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ActionMapperTest {
    @Test
    void mapActionToDomain() throws Exception {
        PDomibusConnectorAction dbAction = PersistenceEntityCreator.createRelayREMMDFailureAction();
        DomibusConnectorAction action = ActionMapper.mapActionToDomain(dbAction);
        assertThat(action.getAction()).isEqualTo("RelayREMMDFailure");
    }

    @Test
    void mapActionToDomain_null_shouldRetNull() throws Exception {
        assertThat(ActionMapper.mapActionToDomain(null)).isNull();
    }

    @Test
    void testMapActionToPersistence() {
        DomibusConnectorAction createActionForm_A = DomainEntityCreatorForPersistenceTests.createActionForm_A();
        PDomibusConnectorAction action = ActionMapper.mapActionToPersistence(createActionForm_A);
        //        assertThat(action.isDocumentRequired()).as("pdf is required so must be true").isTrue();
        assertThat(action.getAction()).as("must match").isEqualTo("Form_A");
    }

    @Test
    void testMapActionToPersistence_null_shouldRetNull() {
        assertThat(ActionMapper.mapActionToPersistence(null)).isNull();
    }
}
