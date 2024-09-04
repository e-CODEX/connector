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

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageError;
import eu.ecodex.connector.domain.test.util.DomainEntityCreatorForPersistenceTests;
import eu.ecodex.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorMessageErrorDao;
import eu.ecodex.connector.persistence.model.PDomibusConnectorMessage;
import eu.ecodex.connector.persistence.model.PDomibusConnectorMessageError;
import eu.ecodex.connector.persistence.model.test.util.PersistenceEntityCreator;
import eu.ecodex.connector.persistence.service.DomibusConnectorMessageErrorPersistenceService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * This class contains unit tests for the DomibusConnectorMessageErrorPersistenceServiceImpl class.
 */
@SuppressWarnings("checkstyle:LineLength")
class DomibusConnectorMessageErrorPersistenceServiceImplTest {
    @Mock
    DomibusConnectorMessageDao messageDao;
    @Mock
    DomibusConnectorMessageErrorDao errorDao;
    DomibusConnectorMessageErrorPersistenceService messageErrorPersistenceService;

    /**
     * Initializes the test environment before each test case.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DomibusConnectorMessageErrorPersistenceServiceImpl impl =
            new DomibusConnectorMessageErrorPersistenceServiceImpl();
        impl.setMessageDao(messageDao);
        impl.setMessageErrorDao(errorDao);
        messageErrorPersistenceService = impl;
    }

    /**
     * Message Error related.
     */
    @Test
    void testPersistMessageError() {
        DomibusConnectorMessageError messageError =
            DomainEntityCreatorForPersistenceTests.createMessageError();

        PDomibusConnectorMessage dbMessage =
            PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
        Mockito.when(this.messageDao.findOneByConnectorMessageId("msg72"))
               .thenReturn(Optional.of(dbMessage));

        Mockito.when(this.errorDao.save(any(PDomibusConnectorMessageError.class)))
               .thenAnswer(new Answer<PDomibusConnectorMessageError>() {
                   @Override
                   public PDomibusConnectorMessageError answer(InvocationOnMock invocation)
                       throws Throwable {
                       PDomibusConnectorMessageError msgError = invocation.getArgument(0);
                       assertThat(msgError.getDetailedText()).isEqualTo("error detail message");
                       assertThat(msgError.getErrorSource()).isEqualTo("error source");
                       assertThat(msgError.getErrorMessage()).isEqualTo("error message");
                       return msgError;
                   }
               });

        messageErrorPersistenceService.persistMessageError("msg72", messageError);

        Mockito.verify(this.errorDao, Mockito.times(1))
               .save(any(PDomibusConnectorMessageError.class));
    }

    /**
     * Message Error related.
     */
    @Test
    void testPersistMessageError_messageDoesNotExistInDb_shouldNotPersist() {
        DomibusConnectorMessageError messageError =
            DomainEntityCreatorForPersistenceTests.createMessageError();

        PDomibusConnectorMessage dbMessage =
            PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
        Mockito.when(this.messageDao.findById(47L)).thenReturn(Optional.empty());

        messageErrorPersistenceService.persistMessageError("msg72", messageError);

        Mockito.verify(this.errorDao, Mockito.times(0))
               .save(any(PDomibusConnectorMessageError.class));
    }

    //    @Test
    //    public void testPersistMessageErrorFromException() {
    //        eu.ecodex.connector.domain.model.DomibusConnectorMessage message =
    //        DomainEntityCreatorForPersistenceTests.createSimpleTestMessage();
    //        //message.setDbMessageId(47L);
    //        Exception ex = new RuntimeException("hallo welt!");
    //        Class source = Integer.class;
    //
    //        PDomibusConnectorMessage dbMessage = PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
    //        Mockito.when(this.messageDao.findOneByConnectorMessageId(eq("msgid"))).thenReturn(dbMessage);
    //        Mockito.when(this.messageDao.findById(eq(47L))).thenReturn(Optional.of(dbMessage));
    //
    //        messageErrorPersistenceService.persistMessageErrorFromException(message, ex, source);
    //
    //        Mockito.verify(this.errorDao, Mockito.times(1)).save(any(PDomibusConnectorMessageError.class));
    //    }

    //    @Test(expected=RuntimeException.class)
    //    public void testPersistMessageErrorFromException_messageHasNoId_shouldThrowException() {
    //        eu.ecodex.connector.domain.model.DomibusConnectorMessage message =
    //        DomainEntityCreatorForPersistenceTests.createSimpleTestMessage();
    //        //message.setDbMessageId(null);
    //        Exception ex = new RuntimeException("hallo welt!");
    //        Class source = Integer.class;
    //
    //        PDomibusConnectorMessage dbMessage =
    //        PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
    //        Mockito.when(this.messageDao.findById(eq(47L))).thenReturn(Optional.of(dbMessage));
    //
    //        messageErrorPersistenceService.persistMessageErrorFromException(message, ex, source);
    //
    //        Mockito.verify(this.errorDao, Mockito.times(1))
    //        .save(any(PDomibusConnectorMessageError.class));
    //    }

    @Test
    void testGetMessageErrors() throws Exception {
        DomibusConnectorMessage message =
            DomainEntityCreatorForPersistenceTests.createSimpleTestMessage();

        List<PDomibusConnectorMessageError> errorList =
            Arrays.asList(
                PersistenceEntityCreator.createMessageError(),
                PersistenceEntityCreator.createMessageError()
            );

        PDomibusConnectorMessage dbMessage =
            PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
        Mockito.when(messageDao.findOneByConnectorMessageId("msgid"))
               .thenReturn(Optional.of(dbMessage));

        Mockito.when(this.errorDao.findByMessageId(47L)).thenReturn(errorList);

        List<DomibusConnectorMessageError> messageErrors =
            messageErrorPersistenceService.getMessageErrors(message);

        assertThat(messageErrors).hasSize(2);
    }

    @Test
    void testGetMessageErrors_noResult_shouldReturnEmptyList() throws Exception {
        DomibusConnectorMessage message =
            DomainEntityCreatorForPersistenceTests.createSimpleTestMessage();

        List<DomibusConnectorMessageError> messageErrors =
            messageErrorPersistenceService.getMessageErrors(message);

        assertThat(messageErrors).isEmpty();
    }
}
