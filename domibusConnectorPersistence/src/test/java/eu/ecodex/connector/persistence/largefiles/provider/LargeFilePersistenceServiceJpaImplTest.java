/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.largefiles.provider;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.model.LargeFileReference;
import eu.ecodex.connector.persistence.dao.CommonPersistenceTest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.MimeTypeUtils;

@CommonPersistenceTest
class LargeFilePersistenceServiceJpaImplTest {
    private static final Logger LOGGER =
        LogManager.getLogger(LargeFilePersistenceServiceJpaImplTest.class);
    @Autowired
    LargeFilePersistenceServiceJpaImpl largeFilePersistenceServiceJpa;
    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    DataSource dataSource;
    private DatabaseDataSourceConnection conn;

    @BeforeEach
    public void setUp() throws SQLException {
        this.conn = new DatabaseDataSourceConnection(dataSource);
    }

    /**
     * Test write a message with attachments / content into database and ensure, that everything is
     * written.
     */
    @Test
    void testPersistMessageWithBigFiles() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            LOGGER.info("run test testPersistMessageWithBigFiles");
            final DomibusConnectorMessageId connectorMessageId =
                new DomibusConnectorMessageId("myid0091");
            final byte[] CONTENT = "content".getBytes(StandardCharsets.UTF_8);

            LargeFileReference fileReference =
                transactionTemplate.execute((TransactionStatus status) -> {
                    LargeFileReference ref =
                        largeFilePersistenceServiceJpa.createDomibusConnectorBigDataReference(
                            connectorMessageId, "name1",
                            MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE
                        );
                    try (java.io.OutputStream os = ref.getOutputStream()) {
                        os.write(CONTENT);
                        return ref;
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });

            // check database....
            ITable bigdata =
                conn.createQueryTable("BIGDATA", "SELECT * FROM DOMIBUS_CONNECTOR_BIGDATA");
            int rowCount = bigdata.getRowCount();

            assertThat(rowCount).isEqualTo(1); //

            byte[] documentContent = (byte[]) bigdata.getValue(0, "content");
            assertThat(documentContent).isNotNull();
            assertThat(documentContent).isEqualTo(CONTENT);

            String connId = (String) bigdata.getValue(0, "CONNECTOR_MESSAGE_ID");
            assertThat(connId).isEqualTo(connectorMessageId.getConnectorMessageId());
        });
    }

    @Test
    void testGetAllReferences() {

        final DomibusConnectorMessageId connectorMessageId =
            new DomibusConnectorMessageId("myid0095");
        final byte[] CONTENT = "content".getBytes(StandardCharsets.UTF_8);

        LargeFileReference fileReference =
            transactionTemplate.execute((TransactionStatus status) -> {
                LargeFileReference ref =
                    largeFilePersistenceServiceJpa.createDomibusConnectorBigDataReference(
                        connectorMessageId, "name1", MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);
                try (java.io.OutputStream os = ref.getOutputStream()) {
                    os.write(CONTENT);
                    return ref;
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });

        Map<DomibusConnectorMessageId, List<LargeFileReference>> allAvailableReferences =
            largeFilePersistenceServiceJpa.getAllAvailableReferences();
        assertThat(allAvailableReferences.get(connectorMessageId))
            .as("For the connector id mus exist one reference")
            .hasSize(1);
    }
}
