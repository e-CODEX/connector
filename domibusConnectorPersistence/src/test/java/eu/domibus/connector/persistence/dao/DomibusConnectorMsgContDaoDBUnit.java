package eu.domibus.connector.persistence.dao;

import com.github.database.rider.core.api.dataset.DataSet;
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

import static com.github.database.rider.core.api.dataset.SeedStrategy.CLEAN_INSERT;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@CommonPersistenceTest
@DataSet(value = "/database/testdata/dbunit/DomibusConnectorMsgContent.xml", strategy = CLEAN_INSERT)
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
    void testDeleteByMessage() throws SQLException, DataSetException {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            //            Optional<PDomibusConnectorMessage> message = messageDao.findOneByConnectorMessageId("conn1");
            txTemplate.executeWithoutResult(t -> msgContDao.deleteByMessage("conn1"));
            // check result in DB
            DatabaseDataSourceConnection conn = ddsc;
            QueryDataSet dataSet = new QueryDataSet(conn);
            dataSet.addTable("DOMIBUS_CONNECTOR_MSG_CONT", "SELECT * FROM DOMIBUS_CONNECTOR_MSG_CONT");

            ITable domibusConnectorTable = dataSet.getTable("DOMIBUS_CONNECTOR_MSG_CONT");

            int rows = domibusConnectorTable.getRowCount();

            assertThat(rows).isEqualTo(3);
        });
    }
}
