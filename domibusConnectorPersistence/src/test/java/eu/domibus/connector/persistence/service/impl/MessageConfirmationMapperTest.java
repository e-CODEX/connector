package eu.domibus.connector.persistence.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("squid:S1135")
class MessageConfirmationMapperTest {
    @Test
    void mapFromDbToDomain() {
        PDomibusConnectorEvidence evidence = new PDomibusConnectorEvidence();
        evidence.setEvidence("test");
        evidence.setType(EvidenceType.DELIVERY);

        DomibusConnectorMessageConfirmation confirmation =
            MessageConfirmationMapper.mapFromDbToDomain(evidence);

        assertThat(confirmation.getEvidenceType()).isEqualTo(DomibusConnectorEvidenceType.DELIVERY);
        assertThat(confirmation.getEvidence()).isEqualTo("test".getBytes());
    }

    @Test
    void mapFromDbToDomain_evidenceIsNull() {
        PDomibusConnectorEvidence evidence = new PDomibusConnectorEvidence();
        evidence.setEvidence(null);
        evidence.setType(EvidenceType.DELIVERY);

        DomibusConnectorMessageConfirmation confirmation =
            MessageConfirmationMapper.mapFromDbToDomain(evidence);

        assertThat(confirmation.getEvidenceType()).isEqualTo(DomibusConnectorEvidenceType.DELIVERY);
        assertThat(confirmation.getEvidence()).isNull();
    }

    @Test
    @Disabled
    void mapFromDomainIntoDb() {
        // TODO see why this test is empty
    }

    @Test
    @Disabled
    void mapFromDomainToDb() {
        // TODO see why this test is empty
    }
}
