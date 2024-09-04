
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
import java.time.Duration;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This class is a test case for the DomibusConnectorMsgContDaoDBUnit class. It uses DBUnit for
 * testing the database operations of the DomibusConnectorMsgContDao class.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@CommonPersistenceTest
@DataSet(
    value = "/database/testdata/dbunit/DomibusConnectorMsgContent.xml", strategy = CLEAN_INSERT
)
class DomibusConnectorMsgContDaoDBUnit extends CommonPersistenceDBUnitITCase {
    @Autowired
    private DomibusConnectorMsgContDao msgContDao;
    @Autowired
    private DomibusConnectorMessageDao messageDao;
    @Autowired
    private DatabaseDataSourceConnection ddsc;
    @Autowired
    private TransactionTemplate txTemplate;

    @Test
    void testDeleteByMessage() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            txTemplate.executeWithoutResult(t -> msgContDao.deleteByMessage("conn1"));

            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable(
                "DOMIBUS_CONNECTOR_MSG_CONT", "SELECT * FROM DOMIBUS_CONNECTOR_MSG_CONT");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MSG_CONT");

            int rows = domibusConnectorTable.getRowCount();

            assertThat(rows).isEqualTo(3);
        });
    }
}
