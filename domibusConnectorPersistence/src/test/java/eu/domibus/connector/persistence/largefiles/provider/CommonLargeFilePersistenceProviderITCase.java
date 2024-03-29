package eu.domibus.connector.persistence.largefiles.provider;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.MimeTypeUtils;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


public abstract class CommonLargeFilePersistenceProviderITCase {
    @Autowired
    LargeFilePersistenceService largeFilePersistenceService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Test
    public void testPersistLargeFile() {
        final String CONNECTOR_ID = "myid001";

        final byte[] writtenBytes = new byte[20];
        new Random().nextBytes(writtenBytes);
        LargeFileReference document1;

        document1 = transactionTemplate.execute((TransactionStatus status) -> {

            LargeFileReference d = largeFilePersistenceService.createDomibusConnectorBigDataReference(
                    CONNECTOR_ID,
                    "document1",
                    MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE
            );

            try (InputStream is = new ByteArrayInputStream(writtenBytes); OutputStream os = d.getOutputStream()) {
                IOUtils.copy(is, os);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return d;
        });

        assertThat(document1.getStorageProviderName())
                .isEqualTo(getProviderName());

        byte[] readBytes = transactionTemplate.execute((TransactionStatus status) -> {
            LargeFileReference readableDataSource = largeFilePersistenceService.getReadableDataSource(document1);
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

    @Test
    public void testFindReferences() {
        final String CONNECTOR_ID = "myid006";

        final byte[] writtenBytes = new byte[20];
        new Random().nextBytes(writtenBytes);
        LargeFileReference document1;

        document1 = transactionTemplate.execute((TransactionStatus status) -> {

            LargeFileReference d = largeFilePersistenceService
                    .createDomibusConnectorBigDataReference(
                            CONNECTOR_ID,
                            "document1",
                            MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE
                    );

            try (InputStream is = new ByteArrayInputStream(writtenBytes); OutputStream os = d.getOutputStream()) {
                IOUtils.copy(is, os);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return d;
        });

        Map<DomibusConnectorMessageId, List<LargeFileReference>> references =
                transactionTemplate.execute((TransactionStatus status) -> {
                    return largeFilePersistenceService.getAllAvailableReferences();
                });

        assertThat(references.get(new DomibusConnectorMessageId(CONNECTOR_ID)))
                .hasSize(1);
    }

    protected abstract String getProviderName();
}
