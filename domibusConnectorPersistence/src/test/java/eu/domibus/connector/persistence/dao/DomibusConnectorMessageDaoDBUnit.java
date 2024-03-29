package eu.domibus.connector.persistence.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.test.util.PersistenceEntityCreator;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.github.database.rider.core.api.dataset.SeedStrategy.CLEAN_INSERT;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@CommonPersistenceTest
@DataSet(value = "/database/testdata/dbunit/DomibusConnectorMessage.xml", strategy = CLEAN_INSERT)
class DomibusConnectorMessageDaoDBUnit {
    @Autowired
    private DomibusConnectorMessageDao messageDao;

    @Autowired
    private DatabaseDataSourceConnection ddsc;

    @Autowired
    private TransactionTemplate txTemplate;

    @Test
    void testFindById() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            PDomibusConnectorMessage msg = messageDao.findById(73L).get();
            assertThat(msg).isNotNull();
            assertThat(msg.getHashValue()).isEqualTo("31fb9a629e9640c4723cbd101adafd32");
        });
    }

    @Test
    void testFindById_doesNotExist_shouldRetNull() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            Optional<PDomibusConnectorMessage> msg = messageDao.findById(7231254123L);
            assertThat(msg).isEmpty();
        });
    }

    @Test
    void testFindByEbmsMessageId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            List<PDomibusConnectorMessage> msg =
                    messageDao.findByEbmsMessageId("c1039627-2db3-489c-af18-92b54e630b36@domibus.eu");
            assertThat(msg).hasSize(1);
        });
    }

    @Test
    void testFindOnebyNationalBackendId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            List<PDomibusConnectorMessage> msg = messageDao.findByBackendMessageId("20171103080259732_domibus-blue");
            assertThat(msg).hasSize(1);
        });
    }

    @Test
    void testFindByEbmsMessageIdOrNationalBackendId_ebmsId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            List<PDomibusConnectorMessage> msg =
                    messageDao.findByEbmsMessageId("c1039627-2db3-489c-af18-92b54e630b36@domibus.eu");
            assertThat(msg).hasSize(1);
        });
    }

    @Test
    void testFindByEbmsMessageIdOrNationalBackendId_nationalId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            List<PDomibusConnectorMessage> msg = messageDao.findByBackendMessageId("20171103080259732_domibus-blue");
            assertThat(msg).hasSize(1);
        });
    }

    @Test
    void testFindOneByEbmsMessageIdOrNationalBackendIdAndDirection_nationalId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            Optional<PDomibusConnectorMessage> msg = messageDao.findOneByBackendMessageIdAndDirectionTarget(
                    "20171103080259732_domibus-blue",
                    MessageTargetSource.GATEWAY
            );
            assertThat(msg).isPresent();
        });
    }

    @Test
    void testFindOneByEbmsMessageIdOrNationalBackendIdAndDirection_ebmsId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            Optional<PDomibusConnectorMessage> msg = messageDao.findOneByEbmsMessageIdAndDirectionTarget(
                    "aaa-7b1a-43c7-aefd-2ec855f5c452@domibus.eu",
                    MessageTargetSource.GATEWAY
            );
            assertThat(msg).isPresent();
        });
    }

    @Test
    void testFindByConversationId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            List<PDomibusConnectorMessage> msgs =
                    messageDao.findByConversationId("aa062b42-2d35-440a-9cb6-c6e95a8679a8@domibus.eu");
            assertThat(msgs).hasSize(1);
            // assertThat(msg.getHashValue()).isEqualTo("31fb9a629e9640c4723cbd101adafd32");
        });
    }

    @Test
    void testFindOutgoingUnconfirmedMessages() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            List<PDomibusConnectorMessage> msgs = messageDao.findOutgoingUnconfirmedMessages();
            assertThat(msgs).hasSize(3);
        });
    }

    @Test
    void testFindOutgoingMessagesNotRejectedAndWithoutDelivery() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            List<PDomibusConnectorMessage> msgs =
                    messageDao.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery();
            assertThat(msgs).hasSize(2);
        });
    }

    @Test
    void testFindOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            List<PDomibusConnectorMessage> msgs =
                    messageDao.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();
            assertThat(msgs).hasSize(2);
        });
    }

    @Test
    void testFindIncomingUnconfirmedMessages() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            List<PDomibusConnectorMessage> msgs = messageDao.findIncomingUnconfirmedMessages();
            assertThat(msgs).hasSize(2);
        });
    }

    @Test
    void testConfirmMessage() throws SQLException, DataSetException {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            int upd = txTemplate.execute(t -> messageDao.confirmMessage(74L));

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable("DOMIBUS_CONNECTOR_MESSAGE", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID=74");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");
            Date value = (Date) domibusConnectorTable.getValue(0, "CONFIRMED");
            assertThat(value).isCloseTo(new Date(), 2000);

            assertThat(upd).as("one row must be updated").isEqualTo(1);
            conn.close();
        });
    }

    @Test
    void testRejectMessage() throws SQLException, DataSetException {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            int upd = txTemplate.execute(t -> messageDao.rejectMessage(73L));

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);

            dataSet.addTable("DOMIBUS_CONNECTOR_MESSAGE", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID=73");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");
            Date value = (Date) domibusConnectorTable.getValue(0, "REJECTED");
            assertThat(value).isCloseTo(new Date(), 2000);

            assertThat(upd).as("one row must be updated!").isEqualTo(1);
            conn.close();
        });
    }

    @Test
    void testRejectedMessage_notExisting() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            int upd = txTemplate.execute(t -> messageDao.rejectMessage(21321315123123L));

            assertThat(upd).as("there should be no updates!").isEqualTo(0);
        });
    }

    @Test
    void testSetMessageDeliveredToGateway() throws SQLException, DataSetException {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            PDomibusConnectorMessage message = new PDomibusConnectorMessage();
            message.setId(73L);
            int upd = txTemplate.execute(t -> messageDao.setMessageDeliveredToGateway(message));

            assertThat(upd).as("exactly one row should be updated!").isEqualTo(1);

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable("DOMIBUS_CONNECTOR_MESSAGE", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID=73");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");
            Date value = (Date) domibusConnectorTable.getValue(0, "delivered_gw");
            assertThat(value).isCloseTo(new Date(), 2000);

            conn.close();
        });
    }

    @Test
    void testSetmessageDeliveredToBackend() throws
            SQLException, DataSetException {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            PDomibusConnectorMessage message = new PDomibusConnectorMessage();
            message.setId(74L);
            int upd = txTemplate.execute(t -> messageDao.setMessageDeliveredToBackend(message));

            assertThat(upd).as("exactly one row should be updated!").isEqualTo(1);

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable("DOMIBUS_CONNECTOR_MESSAGE", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID=74");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");
            Date value = (Date) domibusConnectorTable.getValue(0, "delivered_backend");
            assertThat(value).isCloseTo(new Date(), 2000);

            conn.close();
        });
    }

    @Test
    void testSave() throws SQLException, DataSetException {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            PDomibusConnectorMessage message = PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
            message.setEbmsMessageId("ebms2");
            message.setId(null);
            message.setConnectorMessageId("msg201");

            PDomibusConnectorMessage savedMessage = messageDao.save(message);

            assertThat(savedMessage).isNotNull();
            assertThat(savedMessage.getId()).isNotNull();

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable(
                    "DOMIBUS_CONNECTOR_MESSAGE",
                    "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID=" + savedMessage.getId()
            );

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");

            String connectorMessageId = (String) domibusConnectorTable.getValue(0, "CONNECTOR_MESSAGE_ID");
            assertThat(connectorMessageId).isEqualTo("msg201");

            conn.close();
        });
    }

    @Test
    void testCheckMessageConfirmedOrRejected_shouldBeFalse() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            long id = 655L;
            boolean rejectedOrConfirmed = messageDao.checkMessageConfirmedOrRejected(id);

            assertThat(rejectedOrConfirmed).isFalse();
        });
    }

    @Test
    void testCheckMessageConfirmedOrRejected_shouldBeTrue() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            long id = 65L;
            boolean rejectedOrConfirmed = messageDao.checkMessageConfirmedOrRejected(id);

            assertThat(rejectedOrConfirmed).isTrue();
        });
    }

    //    // if DB field rejected is NOT NULL -> then true
    //    @Query("SELECT case when (count(m) > 0) then true else false end FROM PDomibusConnectorMessage m WHERE m.id
    //    = ?1 AND m.rejected is not null")
    //    public boolean checkMessageRejected(Long messageId);
    @Test
    void checkMessageRejected_shouldBeTrue() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            long id = 65L;
            boolean rejected = messageDao.checkMessageRejected(id);

            assertThat(rejected).isTrue();
        });
    }

    @Test
    void checkMessageRejected_shouldBeFalse() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            long id = 655L;
            boolean rejected = messageDao.checkMessageRejected(id);

            assertThat(rejected).isFalse();
        });
    }

    //    // if DB field confirmend is NOT NULL -> then true
    //    @Query("SELECT case when (count(m) > 0)  then true else false end FROM PDomibusConnectorMessage m WHERE m
    //    .id = ?1 AND m.confirmed is not null")
    //    public boolean checkMessageConfirmed(Long messageId);
    public void checkMessageConfirmed_shouldBeFalse() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            long id = 59L;
            boolean confirmed = messageDao.checkMessageConfirmed(id);

            assertThat(confirmed).isTrue();
        });
    }
}

