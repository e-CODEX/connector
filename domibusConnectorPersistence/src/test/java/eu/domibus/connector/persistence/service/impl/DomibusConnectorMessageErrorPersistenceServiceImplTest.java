package eu.domibus.connector.persistence.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.test.util.DomainEntityCreatorForPersistenceTests;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageErrorDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageError;
import eu.domibus.connector.persistence.model.test.util.PersistenceEntityCreator;
import eu.domibus.connector.persistence.service.DomibusConnectorMessageErrorPersistenceService;
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
    //        eu.domibus.connector.domain.model.DomibusConnectorMessage message =
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
    //        eu.domibus.connector.domain.model.DomibusConnectorMessage message =
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
        eu.domibus.connector.domain.model.DomibusConnectorMessage message =
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

        Mockito.when(this.errorDao.findByMessage(47L)).thenReturn(errorList);

        List<eu.domibus.connector.domain.model.DomibusConnectorMessageError> messageErrors =
            messageErrorPersistenceService.getMessageErrors(message);

        assertThat(messageErrors).hasSize(2);
    }

    @Test
    void testGetMessageErrors_noResult_shouldReturnEmptyList() throws Exception {
        eu.domibus.connector.domain.model.DomibusConnectorMessage message =
            DomainEntityCreatorForPersistenceTests.createSimpleTestMessage();

        List<eu.domibus.connector.domain.model.DomibusConnectorMessageError> messageErrors =
            messageErrorPersistenceService.getMessageErrors(message);

        assertThat(messageErrors).isEmpty();
    }
}
