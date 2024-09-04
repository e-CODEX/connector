
/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.dao;

import static com.github.database.rider.core.api.dataset.SeedStrategy.CLEAN_INSERT;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import eu.ecodex.connector.domain.enums.MessageTargetSource;
import eu.ecodex.connector.persistence.model.PDomibusConnectorMessage;
import eu.ecodex.connector.persistence.model.test.util.PersistenceEntityCreator;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This class represents the Data Access Object (DAO) for the DomibusConnectorMessage entity in the
 * database. It provides methods to perform various CRUD operations on the DomibusConnectorMessage
 * table.
 *
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
    void testFindOneByNationalBackendId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            List<PDomibusConnectorMessage> msg =
                messageDao.findByBackendMessageId("20171103080259732_domibus-blue");
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
            List<PDomibusConnectorMessage> msg =
                messageDao.findByBackendMessageId("20171103080259732_domibus-blue");
            assertThat(msg).hasSize(1);
        });
    }

    @Test
    void testFindOneByEbmsMessageIdOrNationalBackendIdAndDirection_nationalId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            Optional<PDomibusConnectorMessage> msg =
                messageDao.findOneByBackendMessageIdAndDirectionTarget(
                    "20171103080259732_domibus-blue", MessageTargetSource.GATEWAY);
            assertThat(msg).isPresent();
        });
    }

    @Test
    void testFindOneByEbmsMessageIdOrNationalBackendIdAndDirection_ebmsId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            Optional<PDomibusConnectorMessage> msg =
                messageDao.findOneByEbmsMessageIdAndDirectionTarget(
                    "aaa-7b1a-43c7-aefd-2ec855f5c452@domibus.eu", MessageTargetSource.GATEWAY);
            assertThat(msg).isPresent();
        });
    }

    @Test
    void testFindByConversationId() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            List<PDomibusConnectorMessage> messages =
                messageDao.findByConversationId("aa062b42-2d35-440a-9cb6-c6e95a8679a8@domibus.eu");
            assertThat(messages).hasSize(1);
        });
    }

    @Test
    void testFindOutgoingUnconfirmedMessages() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            List<PDomibusConnectorMessage> messages = messageDao.findOutgoingUnconfirmedMessages();
            assertThat(messages).hasSize(3);
        });
    }

    @Test
    void testFindOutgoingMessagesNotRejectedAndWithoutDelivery() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            List<PDomibusConnectorMessage> messages =
                messageDao.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery();
            assertThat(messages).hasSize(2);
        });
    }

    @Test
    void testFindOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            List<PDomibusConnectorMessage> messages =
                messageDao.findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();
            assertThat(messages).hasSize(2);
        });
    }

    @Test
    void testFindIncomingUnconfirmedMessages() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            List<PDomibusConnectorMessage> messages = messageDao.findIncomingUnconfirmedMessages();
            assertThat(messages).hasSize(2);
        });
    }

    @Test
    void testConfirmMessage() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            int upd = txTemplate.execute(t -> messageDao.confirmMessage(74L));

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable(
                "DOMIBUS_CONNECTOR_MESSAGE", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID=74");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");
            Date value = (Date) domibusConnectorTable.getValue(0, "CONFIRMED");
            assertThat(value).isCloseTo(new Date(), 2000);

            assertThat(upd).as("one row must be updated").isEqualTo(1);
            conn.close();
        });
    }

    @Test
    void testRejectMessage() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            int upd = txTemplate.execute(t -> messageDao.rejectMessage(73L));

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);

            dataSet.addTable(
                "DOMIBUS_CONNECTOR_MESSAGE", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID=73");

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

            assertThat(upd).as("there should be no updates!").isZero();
        });
    }

    @Test
    void testSetMessageDeliveredToGateway() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            PDomibusConnectorMessage message = new PDomibusConnectorMessage();
            message.setId(73L);
            int upd = txTemplate.execute(t -> messageDao.setMessageDeliveredToGateway(message));

            assertThat(upd).as("exactly one row should be updated!").isEqualTo(1);

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable(
                "DOMIBUS_CONNECTOR_MESSAGE", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID=73");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");
            Date value = (Date) domibusConnectorTable.getValue(0, "delivered_gw");
            assertThat(value).isCloseTo(new Date(), 2000);

            conn.close();
        });
    }

    @Test
    void testSetMessageDeliveredToBackend() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            PDomibusConnectorMessage message = new PDomibusConnectorMessage();
            message.setId(74L);
            int upd = txTemplate.execute(t -> messageDao.setMessageDeliveredToBackend(message));

            assertThat(upd).as("exactly one row should be updated!").isEqualTo(1);

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable(
                "DOMIBUS_CONNECTOR_MESSAGE", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID=74");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");
            Date value = (Date) domibusConnectorTable.getValue(0, "delivered_backend");
            assertThat(value).isCloseTo(new Date(), 2000);

            conn.close();
        });
    }

    @Test
    void testSave() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            PDomibusConnectorMessage message =
                PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
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
                "DOMIBUS_CONNECTOR_MESSAGE", "SELECT * FROM DOMIBUS_CONNECTOR_MESSAGE WHERE ID="
                    + savedMessage.getId());

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MESSAGE");

            String connectorMessageId =
                (String) domibusConnectorTable.getValue(0, "CONNECTOR_MESSAGE_ID");
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
    //    @Query("SELECT case when (count(m) > 0) then true else false end
    //    FROM PDomibusConnectorMessage m WHERE m.id = ?1 AND m.rejected is not null")
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
    //    @Query("SELECT case when (count(m) > 0)  then true else false end
    //    FROM PDomibusConnectorMessage m WHERE m.id = ?1 AND m.confirmed is not null")
    //    public boolean checkMessageConfirmed(Long messageId);
    public void checkMessageConfirmed_shouldBeFalse() {
        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {
            long id = 59L;
            boolean confirmed = messageDao.checkMessageConfirmed(id);

            assertThat(confirmed).isTrue();
        });
    }
}

