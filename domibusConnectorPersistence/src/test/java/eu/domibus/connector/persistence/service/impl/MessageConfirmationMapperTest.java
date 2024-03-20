package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MessageConfirmationMapperTest {





    @Test
    public void mapFromDbToDomain() throws Exception {
        PDomibusConnectorEvidence evidence = new PDomibusConnectorEvidence();
        evidence.setEvidence("test");
        evidence.setType(EvidenceType.DELIVERY);


        DomibusConnectorMessageConfirmation confirmation = MessageConfirmationMapper.mapFromDbToDomain(evidence);

        assertThat(confirmation.getEvidenceType()).isEqualTo(DomibusConnectorEvidenceType.DELIVERY);
        assertThat(confirmation.getEvidence()).isEqualTo("test".getBytes());
    }

    @Test
    public void mapFromDbToDomain_evidenceIsNull() throws Exception {
        PDomibusConnectorEvidence evidence = new PDomibusConnectorEvidence();
        evidence.setEvidence(null);
        evidence.setType(EvidenceType.DELIVERY);

        DomibusConnectorMessageConfirmation confirmation = MessageConfirmationMapper.mapFromDbToDomain(evidence);

        assertThat(confirmation.getEvidenceType()).isEqualTo(DomibusConnectorEvidenceType.DELIVERY);
        assertThat(confirmation.getEvidence()).isNull();
    }

    @Test
    @Disabled
    public void mapFromDomainIntoDb() throws Exception {
    }

    @Test
    @Disabled
    public void mapFromDomainToDb() throws Exception {
    }

}