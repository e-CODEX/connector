package eu.domibus.connector.persistence.service;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.domain.test.util.DomainEntityCreatorForPersistenceTests;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.domain.transformer.util.LargeFileReferenceMemoryBacked;
import eu.domibus.connector.persistence.dao.CommonPersistenceTest;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import java.sql.SQLException;
import java.time.Duration;
import javax.sql.DataSource;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Integration Test for testing persistence service.
 *
 * <p>Creates an embedded h2 database for tests this tests are _WRITING_ to the database, there is
 * no rollback
 *
 * <p>Then liquibase is used to initialize tables and basic testdata in the testing database
 *
 * <p>Additional testdata is loaded by dbunit in setUp method
 *
 * <p>Database settings are configured in {@literal application-<profile>.properties}
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at>}
 */
@SuppressWarnings({"squid:S1135", "checkstyle:LineLength"})
@CommonPersistenceTest
class MessagePersistenceServiceITCase {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DCMessagePersistenceService messagePersistenceService;
    @Autowired
    private TransactionTemplate txTemplate;

    /**
     * Test if the test EpoMessage can be persisted into database.
     */
    @Test
    void testPersistEpoMessageIntoDatabase()
        throws PersistenceException, SQLException, DataSetException {
        String connectorMessageId = "msgid8972_epo";
        DomibusConnectorMessage epoMessage = DomainEntityCreator.createEpoMessage();
        epoMessage.setConnectorMessageId(connectorMessageId);

        epoMessage.getMessageDetails().setEbmsMessageId("ebms9000");
        epoMessage.getMessageDetails().setConversationId("conversation4000");
        epoMessage.getMessageDetails()
                  .setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);

        txTemplate.executeWithoutResult(
            t -> messagePersistenceService.persistBusinessMessageIntoDatabase(epoMessage));

        assertThat(epoMessage).isNotNull();

        // check result in DB
        DatabaseDataSourceConnection conn = new DatabaseDataSourceConnection(dataSource);
        QueryDataSet dataSet = new QueryDataSet(conn);
        dataSet.addTable(
            "DOMIBUS_CONNECTOR_MESSAGE",
            String.format(
                "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE CONNECTOR_MESSAGE_ID='%s'",
                connectorMessageId
            )
        );

        ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");

        String ebmsId = (String) domibusConnectorTable.getValue(0, "ebms_message_id");
        assertThat(ebmsId).isEqualTo("ebms9000");

        String conversationId = (String) domibusConnectorTable.getValue(0, "conversation_id");
        assertThat(conversationId).isEqualTo("conversation4000");
    }

    /*
     * test that, the persist throws an exception if an service is used which is not
     * configured in database!
     *
     * TODO: improve specification to throw more specific exception!
     *
     */
    @Test
    void testPersistMessageIntoDatabase_serviceNotInDatabase_shouldThrowException()
        throws PersistenceException {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            Assertions.assertThrows(PersistenceException.class, () -> {
                String connectorMessageId = "msg0021";

                DomibusConnectorMessage message =
                    DomainEntityCreatorForPersistenceTests.createMessage(connectorMessageId);
                DomibusConnectorMessageDetails messageDetails = message.getMessageDetails();

                messageDetails.setConversationId("conversation421");
                messageDetails.setEbmsMessageId("ebms421");
                messageDetails.setBackendMessageId("backend421");
                messageDetails.setDirection(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);

                DomibusConnectorService serviceUnknown =
                    DomainEntityCreatorForPersistenceTests.createServiceUnknown();
                messageDetails.setService(serviceUnknown); // set Unknown service

                // should throw exception, because UnknownService is not configured in DB!
                messagePersistenceService.persistBusinessMessageIntoDatabase(message);
            });
        });
    }

    /**
     * Tests complete message, if a message can be stored to DB and also loaded again from DB.
     *
     * <p>Test restore evidenceMessage!
     */
    @Test
    void testPersistMessageIntoDatabase_testContentPersist()
        throws PersistenceException {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            String ebmsId = "aztebamHUGO1";
            DomibusConnectorMessageDirection messageDirection =
                DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND;
            DomibusConnectorMessage message =
                DomainEntityCreatorForPersistenceTests.createMessage("superid23");
            message.getMessageDetails().setEbmsMessageId(ebmsId);
            message.getMessageDetails().setBackendMessageId("fklwefa");
            message.getMessageDetails().setDirection(messageDirection);

            LargeFileReferenceMemoryBacked attachRef =
                new LargeFileReferenceMemoryBacked("hallo welt".getBytes());
            DomibusConnectorMessageAttachment attach1 =
                new DomibusConnectorMessageAttachment(attachRef, "idf");
            message.addAttachment(attach1);

            DomibusConnectorMessageConfirmation confirmation =
                new DomibusConnectorMessageConfirmation(
                    DomibusConnectorEvidenceType.DELIVERY,
                    "hallowelt".getBytes()
                );
            message.addTransportedMessageConfirmation(confirmation);

            messagePersistenceService.persistMessageIntoDatabase(message, messageDirection);

            // load persisted message again from db and run checks
            DomibusConnectorMessage messageToCheck =
                messagePersistenceService.findMessageByEbmsIdAndDirection(ebmsId, messageDirection)
                                         .orElse(null);
            assertThat(messageToCheck).as("message must exist").isNotNull();
            assertThat(messageToCheck.getMessageContent()).as("message must have content!")
                                                          .isNotNull();

            assertThat(messageToCheck.getMessageAttachments()).as("should contain two attachments!")
                                                              .hasSize(2);
            DomibusConnectorMessageAttachment messageAttachment = messageToCheck
                .getMessageAttachments()
                .stream()
                .filter(a -> "idf".equals(a.getIdentifier()))
                .findFirst()
                .get();
        });
    }

    //    @Test
    //    public void testMergeMessageWithDatabase() throws PersistenceException, SQLException, AmbiguousTableNameException, DataSetException {
    //        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
    //            DomibusConnectorMessage message = DomainEntityCreatorForPersistenceTests.createMessage("superid");
    //            message.getMessageDetails().setEbmsMessageId("ebamdsafae3");
    //            message.getMessageDetails().setBackendMessageId("adfsöljabafklwefa");
    //
    //            DomibusConnectorMessageDirection messageDirection = DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND;
    //            messagePersistenceService.persistMessageIntoDatabase(message, messageDirection);
    //
    //            //TODO: make changes to message
    //
    //            message = messagePersistenceService.mergeMessageWithDatabase(message);
    //
    //
    //            //message.getMessageDetails()
    //        });
    //    }

    //    @Test
    //    public void testMergeMessageWithDatabase_doesNotExistInDatabase() throws PersistenceException {
    //        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
    //            Assertions.assertThrows(PersistenceException.class, () -> {
    //                DomibusConnectorMessage message = DomainEntityCreatorForPersistenceTests.createMessage();
    //                messagePersistenceService.mergeMessageWithDatabase(message);
    //            });
    //        });
    //    }

    @Test
    void testFindMessageByNationalId_doesNotExist_shouldBeNull() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            String nationalIdString = "TEST1";
            DomibusConnectorMessage findMessageByNationalId = messagePersistenceService
                .findMessageByNationalIdAndDirection(
                    nationalIdString, DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY)
                .orElse(null);

            assertThat(findMessageByNationalId).isNull();
        });
    }

    // TODO: test find message & check fromParty, toParty, service, action

    //    @Test
    //    public void findMessageBy() {
    //        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
    //            String ebmsId = "ebamHUGO1";
    //            DomibusConnectorMessageDirection messageDirection = DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND;
    //            DomibusConnectorMessage message = DomainEntityCreatorForPersistenceTests.createMessage("msg665");
    //            message.getMessageDetails().setEbmsMessageId(ebmsId);
    //            message.getMessageDetails().setBackendMessageId("fklwefa");
    //            message.getMessageDetails().setDirection(messageDirection);
    //
    //            LargeFileReferenceMemoryBacked attachRef =
    //                    new LargeFileReferenceMemoryBacked("hallo welt".getBytes());
    //            DomibusConnectorMessageAttachment attach1 =
    //                    new DomibusConnectorMessageAttachment(attachRef, "idf");
    //            message.addAttachment(attach1);
    //            messagePersistenceService.persistMessageIntoDatabase(message, DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
    //
    //            DomibusConnectorMessage msg1 = messagePersistenceService.findMessageByConnectorMessageId("msg665");
    //            assertThat(msg1).as("message cannot be null!").isNotNull();
    //        });
    //    }

    // TODO: test other methods/use cases
    /*
     void persistMessageIntoDatabase(Message message, PMessageDirection direction) throws PersistenceException;

     void mergeMessageWithDatabase(Message message);

     void persistEvidenceForMessageIntoDatabase(Message message, byte[] evidence, EvidenceType evidenceType);

     Message findMessageByNationalId(String nationalMessageId);

     Message findMessageByEbmsId(String ebmsMessageId);

     void setEvidenceDeliveredToGateway(Message message, EvidenceType evidenceType);

     void setEvidenceDeliveredToNationalSystem(Message message, EvidenceType evidenceType);

     void setDeliveredToGateway(Message message);

     void setMessageDeliveredToNationalSystem(Message message);

     List<Message> findOutgoingUnconfirmedMessages();

     List<Message> findIncomingUnconfirmedMessages();

     void confirmMessage(Message message);

     void rejectMessage(Message message);

     DomibusConnectorAction getAction(String action);

     DomibusConnectorAction getRelayREMMDAcceptanceRejectionAction();

     DomibusConnectorAction getRelayREMMDFailure();

     DomibusConnectorAction getDeliveryNonDeliveryToRecipientAction();

     DomibusConnectorAction getRetrievalNonRetrievalToRecipientAction();

     DomibusConnectorService getService(String service);

     DomibusConnectorParty getParty(String partyId, String role);

     DomibusConnectorParty getPartyByPartyId(String partyId);

     List<Message> findMessagesByConversationId(String conversationId);

     void persistMessageError(MessageError messageError);

     List<MessageError> getMessageErrors(Message message) throws Exception;

     void persistMessageErrorFromException(Message message, Throwable ex, Class<?> source) throws PersistenceException;

     List<Message> findOutgoingMessagesNotRejectedAndWithoutDelivery();

     List<Message> findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();
     */
}
