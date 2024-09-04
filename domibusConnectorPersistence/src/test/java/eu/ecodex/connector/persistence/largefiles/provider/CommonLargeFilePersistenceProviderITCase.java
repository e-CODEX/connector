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
import eu.ecodex.connector.persistence.service.LargeFilePersistenceService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.MimeTypeUtils;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

/**
 * Represents a test case for the CommonLargeFilePersistenceProvider class.
 */
public abstract class CommonLargeFilePersistenceProviderITCase {
    @Autowired
    LargeFilePersistenceService largeFilePersistenceService;
    @Autowired
    TransactionTemplate transactionTemplate;

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testPersistLargeFile() {
        final String CONNECTOR_ID = "myid001";

        final byte[] writtenBytes = new byte[20];
        new Random().nextBytes(writtenBytes);
        LargeFileReference document1;

        document1 = transactionTemplate.execute((TransactionStatus status) -> {

            LargeFileReference d =
                largeFilePersistenceService.createDomibusConnectorBigDataReference(
                    CONNECTOR_ID,
                    "document1",
                    MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE
                );

            try (InputStream is = new ByteArrayInputStream(writtenBytes);
                 OutputStream os = d.getOutputStream()) {
                IOUtils.copy(is, os);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return d;
        });

        assertThat(document1.getStorageProviderName())
            .isEqualTo(getProviderName());

        byte[] readBytes = transactionTemplate.execute((TransactionStatus status) -> {
            LargeFileReference readableDataSource =
                largeFilePersistenceService.getReadableDataSource(document1);
            try (InputStream is = readableDataSource.getInputStream()) {
                return IOUtils.toByteArray(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assertThat(writtenBytes)
            .as("Written bytes and read bytes must be equal")
            .isEqualTo(readBytes);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testFindReferences() {
        final String CONNECTOR_ID = "myid006";

        final byte[] writtenBytes = new byte[20];
        new Random().nextBytes(writtenBytes);
        LargeFileReference document1 = transactionTemplate.execute((TransactionStatus status) -> {
            LargeFileReference d =
                largeFilePersistenceService
                    .createDomibusConnectorBigDataReference(
                        CONNECTOR_ID,
                        "document1",
                        MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE
                    );

            try (InputStream is = new ByteArrayInputStream(writtenBytes);
                 OutputStream os = d.getOutputStream()) {
                IOUtils.copy(is, os);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return d;
        });

        Map<DomibusConnectorMessageId, List<LargeFileReference>> references =
            transactionTemplate.execute((TransactionStatus status) -> largeFilePersistenceService
                .getAllAvailableReferences());

        assertThat(references.get(new DomibusConnectorMessageId(CONNECTOR_ID)))
            .hasSize(1);
    }

    protected abstract String getProviderName();
}
