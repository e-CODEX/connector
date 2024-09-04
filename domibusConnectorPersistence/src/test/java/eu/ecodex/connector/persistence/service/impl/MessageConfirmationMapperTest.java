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

import eu.ecodex.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.persistence.model.PDomibusConnectorEvidence;
import eu.ecodex.connector.persistence.model.enums.EvidenceType;
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
