
package eu.domibus.connector.persistence.dao;

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
