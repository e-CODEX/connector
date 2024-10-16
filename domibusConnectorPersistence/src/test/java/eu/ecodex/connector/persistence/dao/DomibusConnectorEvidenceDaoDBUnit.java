
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

import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import eu.ecodex.connector.persistence.model.PDomibusConnectorEvidence;
import eu.ecodex.connector.persistence.model.PDomibusConnectorMessage;
import eu.ecodex.connector.persistence.model.enums.EvidenceType;
import java.time.Duration;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The DomibusConnectorEvidenceDaoDBUnit class represents a test class for the
 * DomibusConnectorEvidenceDao interface. It is responsible for testing the methods of the
 * DomibusConnectorEvidenceDao implementation using DBUnit for data setup.
 */
//@CommonPersistenceTest
@SuppressWarnings("checkstyle:LineLength")
@Disabled("repair tests...")
class DomibusConnectorEvidenceDaoDBUnit {
    @Autowired
    private DomibusConnectorEvidenceDao evidenceDao;
    @Autowired
    private DomibusConnectorMessageDao messageDao;
    @Autowired
    private DatabaseDataSourceConnection ddsc;

    //    @Test
    //    @DataSet(value = "/database/testdata/dbunit/DomibusConnectorEvidence.xml", cleanAfter = true, cleanBefore = true, disableConstraints = true)
    //    public void testFindEvidencesForMessage() {
    //        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
    //
    //            List<PDomibusConnectorEvidence> evidences = evidenceDao.findByMessage_Id(73L);
    //
    //            assertThat(evidences).hasSize(3);
    //        });
    //    }

    //    @Test
    //    @DataSet(value = "/database/testdata/dbunit/DomibusConnectorEvidence.xml", cleanAfter = true, cleanBefore = true, disableConstraints = true)
    //    public void testSetDeliveredToGateway() throws SQLException, AmbiguousTableNameException, DataSetException {
    //        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
    //            int result = evidenceDao.setDeliveredToGateway(82L);
    //            assertThat(result).isEqualTo(1); //check on row updated
    //
    //            //check result in DB
    //            DatabaseDataSourceConnection conn = ddsc;
    //            QueryDataSet dataSet = new QueryDataSet(conn);
    //            dataSet.addTable("DOMIBUS_CONNECTOR_EVIDENCE", "SELECT * FROM DOMIBUS_CONNECTOR_EVIDENCE WHERE ID=82");
    //
    //            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_EVIDENCE");
    //            Date value = (Date) domibusConnectorTable.getValue(0, "DELIVERED_GW");
    //            assertThat(value).isCloseTo(new Date(), 2000);
    //        });
    //    }
    //
    //    @Test
    //    @DataSet(value = "/database/testdata/dbunit/DomibusConnectorEvidence.xml", cleanAfter = true, cleanBefore = true, disableConstraints = true)
    //    public void testSetDeliveredToGateway_updateNonExistant_shouldReturnZero() {
    //        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
    //            int result = evidenceDao.setDeliveredToGateway(882L);
    //            assertThat(result).isEqualTo(0); //check on row updated
    //        });
    //    }

    //    @Test(timeout=20000)
    //    public void testSetDeliveredToGateway_ByMessageIdAndType() throws SQLException, DataSetException {
    //        PDomibusConnectorMessage dbMessage = new PDomibusConnectorMessage();
    //        dbMessage.setId(73L);
    //        int result = evidenceDao.setDeliveredToGateway(dbMessage, EvidenceType.SUBMISSION_REJECTION);
    //
    //        assertThat(result).isEqualTo(1);
    //        //check result in DB
    //        DatabaseDataSourceConnection conn = new DatabaseDataSourceConnection(ds);
    //        QueryDataSet dataSet = new QueryDataSet(conn);
    //        dataSet.addTable("DOMIBUS_CONNECTOR_EVIDENCE", "SELECT * FROM DOMIBUS_CONNECTOR_EVIDENCE WHERE ID=82");
    //
    //        ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_EVIDENCE");
    //        Date value = (Date) domibusConnectorTable.getValue(0, "DELIVERED_GW");
    //        assertThat(value).isNotNull();
    //        assertThat(value).isCloseTo(new Date(), 2000);
    //    }

    //    @Test(timeout=20000)
    //    public void testSetDeliveredToBackend() throws SQLException, AmbiguousTableNameException, DataSetException {
    //        int result = evidenceDao.setDeliveredToBackend(83L);
    //        assertThat(result).isEqualTo(1); //check one row updated
    //
    //         //check result in DB
    //        DatabaseDataSourceConnection conn = new DatabaseDataSourceConnection(ds);
    //        DatabaseConfig config = conn.getConfig();
    //        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
    //
    //        QueryDataSet dataSet = new QueryDataSet(conn);
    //        dataSet.addTable("DOMIBUS_CONNECTOR_EVIDENCE", "SELECT * FROM DOMIBUS_CONNECTOR_EVIDENCE WHERE ID=83");
    //
    //        ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_EVIDENCE");
    //        Date value = (Date) domibusConnectorTable.getValue(0, "DELIVERED_NAT");
    //        assertThat(value).isCloseTo(new Date(), 2000);
    //    }

    //    @Test
    //    @DataSet(value = "/database/testdata/dbunit/DomibusConnectorEvidence.xml", cleanAfter = true, cleanBefore = true, disableConstraints = true)
    //    public void testSetDeliveredToBackend_ByMessageIdAndType() throws SQLException, DataSetException {
    //        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
    //            PDomibusConnectorMessage dbMessage = new PDomibusConnectorMessage();
    //            dbMessage.setId(74L);
    //            int result = evidenceDao.setDeliveredToBackend(dbMessage, EvidenceType.SUBMISSION_ACCEPTANCE);
    //
    //            assertThat(result).isEqualTo(1);
    //            //check result in DB
    //            DatabaseDataSourceConnection conn = ddsc;
    //            QueryDataSet dataSet = new QueryDataSet(conn);
    //            dataSet.addTable("DOMIBUS_CONNECTOR_EVIDENCE", "SELECT * FROM DOMIBUS_CONNECTOR_EVIDENCE WHERE ID=85");
    //
    //            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_EVIDENCE");
    //            Date value = (Date) domibusConnectorTable.getValue(0, "DELIVERED_NAT");
    //            assertThat(value).isNotNull();
    //            assertThat(value).isCloseTo(new Date(), 2000);
    //
    //            conn.close();
    //        });
    //    }

    //    @Test(timeout=20000)
    //    public void testSetDeliveredToBackend_updateNoneExistant_shouldReturnZero() {
    //        int result = evidenceDao.setDeliveredToBackend(83231L);
    //        assertThat(result).isEqualTo(0); //check one row updated
    //    }

    @Test
    @DataSet(
        value = "/database/testdata/dbunit/DomibusConnectorEvidence.xml", cleanAfter = true,
        cleanBefore = true, disableConstraints = true
    )
    void testSaveEvidence() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            PDomibusConnectorEvidence dbEvidence = new PDomibusConnectorEvidence();

            PDomibusConnectorMessage dbMessage = messageDao.findById(75L).get();
            assertThat(dbMessage).isNotNull();

            byte[] evidence = "Hallo Welt".getBytes();

            dbEvidence.setBusinessMessage(dbMessage);
            dbEvidence.setEvidence(new String(evidence));
            dbEvidence.setType(EvidenceType.DELIVERY);
            dbEvidence.setDeliveredToGateway(null);
            dbEvidence.setDeliveredToBackend(null);
            // dbEvidence.setTransportMessageId("testid");

            evidenceDao.save(dbEvidence);
        });
    }
}
