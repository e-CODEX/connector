package eu.domibus.connector.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import eu.domibus.connector.persistence.model.PDomibusConnectorBigData;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import javax.persistence.EntityManager;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.ITable;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@CommonPersistenceTest
@DataSet(value = "/database/testdata/dbunit/DomibusConnectorBigDataContent.xml", cleanBefore = true)
class PDomibusConnectorBigDataDBUnit {
    @Autowired
    private DomibusConnectorBigDataDao bigDataDao;
    @Autowired
    private DomibusConnectorMessageDao messageDao;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private TransactionTemplate txTemplate;
    @Autowired
    private DatabaseDataSourceConnection conn;

    @Test
    void testSave() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            String msgId = "72";

            PDomibusConnectorBigData bigData = new PDomibusConnectorBigData();
            bigData.setConnectorMessageId(msgId);

            txTemplate.execute(status -> {
                Session hibernateSession = entityManager.unwrap(Session.class);
                bigData.setContent("hallo welt".getBytes(StandardCharsets.UTF_8));
                bigData.setMimeType("application/octet-stream");
                bigData.setName("name");

                bigDataDao.save(bigData);
                return null;
            });

            // check database
            ITable dataTable = this.conn.createQueryTable(
                "DATARES",
                "SELECT * FROM DOMIBUS_CONNECTOR_BIGDATA WHERE MESSAGE_ID = " + msgId
            );
            int rowCount = dataTable.getRowCount();

            assertThat(rowCount).isZero();
        });
    }
}
