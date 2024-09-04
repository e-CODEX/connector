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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.test.util.DomainEntityCreatorForPersistenceTests;
import eu.ecodex.connector.persistence.dao.DomibusConnectorEvidenceDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.ecodex.connector.persistence.model.PDomibusConnectorEvidence;
import eu.ecodex.connector.persistence.model.PDomibusConnectorMessage;
import eu.ecodex.connector.persistence.model.enums.EvidenceType;
import eu.ecodex.connector.persistence.model.test.util.PersistenceEntityCreator;
import eu.ecodex.connector.persistence.service.DomibusConnectorEvidencePersistenceService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@Disabled
class DomibusConnectorEvidencePersistenceServiceImplTest {
    @Mock
    DomibusConnectorEvidenceDao domibusConnectorEvidenceDao;
    @Mock
    DomibusConnectorMessageDao domibusConnectorMessageDao;
    DomibusConnectorEvidencePersistenceService evidencePersistenceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DomibusConnectorEvidencePersistenceServiceImpl impl =
            new DomibusConnectorEvidencePersistenceServiceImpl();
        impl.setEvidenceDao(domibusConnectorEvidenceDao);
        impl.setMessageDao(domibusConnectorMessageDao);
        evidencePersistenceService = impl;
    }

    @Test
    void testPersistEvidenceForMessageIntoDatabase_evidenceBytesAreNull() {
        DomibusConnectorMessage message =
            DomainEntityCreatorForPersistenceTests.createSimpleTestMessage();

        PDomibusConnectorMessage dbMessage =
            PersistenceEntityCreator.createSimpleDomibusConnectorMessage();

        byte[] evidence = null;

        Mockito.when(this.domibusConnectorMessageDao.findOneByConnectorMessageId(eq("msgid")))
               .thenReturn(Optional.of(dbMessage));

        Mockito.when(this.domibusConnectorEvidenceDao.save(any(PDomibusConnectorEvidence.class)))
               .thenAnswer(new Answer<PDomibusConnectorEvidence>() {
                   @Override
                   public PDomibusConnectorEvidence answer(InvocationOnMock invocation)
                       throws Throwable {
                       PDomibusConnectorEvidence evidence = invocation.getArgument(0);
                       assertThat(evidence.getDeliveredToGateway()).isNull();
                       assertThat(evidence.getDeliveredToBackend()).isNull();
                       assertThat(evidence.getType()).isEqualTo(
                           EvidenceType.DELIVERY);
                       assertThat(evidence.getBusinessMessage()).isNotNull();
                       assertThat(evidence.getEvidence()).isNull();
                       return evidence;
                   }
               });
        Mockito.verify(this.domibusConnectorEvidenceDao, Mockito.times(1))
               .save(any(PDomibusConnectorEvidence.class));
    }

    @Test
    void testPersistEvidenceForMessageIntoDatabase() {
        DomibusConnectorMessage message =
            DomainEntityCreatorForPersistenceTests.createSimpleTestMessage();

        PDomibusConnectorMessage dbMessage =
            PersistenceEntityCreator.createSimpleDomibusConnectorMessage();

        byte[] evidence = "EVIDENCE1".getBytes();

        Mockito.when(this.domibusConnectorMessageDao.findOneByConnectorMessageId(eq("msgid")))
               .thenReturn(Optional.of(dbMessage));

        Mockito.when(this.domibusConnectorEvidenceDao.save(any(PDomibusConnectorEvidence.class)))
               .thenAnswer((Answer<PDomibusConnectorEvidence>) invocation -> {
                   PDomibusConnectorEvidence evidence1 = invocation.getArgument(0);
                   assertThat(evidence1.getDeliveredToGateway()).isNull();
                   assertThat(evidence1.getDeliveredToBackend()).isNull();
                   assertThat(evidence1.getType()).isEqualTo(
                       EvidenceType.DELIVERY);
                   assertThat(evidence1.getBusinessMessage()).isNotNull();
                   assertThat(evidence1.getEvidence()).isEqualTo("EVIDENCE1");
                   return evidence1;
               });
        Mockito.verify(this.domibusConnectorEvidenceDao, Mockito.times(1))
               .save(any(PDomibusConnectorEvidence.class));
    }
}
