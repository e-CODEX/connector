package eu.domibus.connector.persistence.largefiles.provider;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.service.exceptions.LargeFileDeletionException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


public interface LargeFilePersistenceProvider {
    /**
     * Should return the provider name,
     * the provider name will be stored into the database to make it possible
     * to find the provider again
     * <p>
     * decouples the class name of the implementation from the written value
     * into the database
     *
     * @return - the unique name of the LargeFileStorageProvider
     */
    String getProviderName();

    /**
     * returns a instance of DomibusConnectorBigDataReference,
     * where the getInputStream method will return a valid inputStream
     *
     * @param bigDataReference the DomibusConnectorBigDataReference
     * @return the DomibusConnectorBigDataReference with initialized inputStream
     */
    LargeFileReference getReadableDataSource(LargeFileReference bigDataReference);

    /**
     * will create a new instance of DomibusConnectorBigDataReference
     *
     * @param input               the inputstream holding the content
     * @param connectorMessageId  the unique id of this message
     * @param documentName        the name of this document
     * @param documentContentType the type of this document
     * @return the created DomibusConnectorBigDataReference with CLOSED OutputStream
     */
    @Deprecated
    LargeFileReference createDomibusConnectorBigDataReference(
            InputStream input,
            String connectorMessageId,
            String documentName,
            String documentContentType
    );

    /**
     * will create a new instance of DomibusConnectorBigDataReference
     *
     * @param connectorMessageId  the unique id of this message
     * @param documentName        the name of this document
     * @param documentContentType the type of this document
     * @return the created DomibusConnectorBigDataReference with OPEN OutputStream ready for WRITE,
     * but closed INPUT stream
     */
    default LargeFileReference createDomibusConnectorBigDataReference(
            DomibusConnectorMessageId connectorMessageId,
            String documentName, String documentContentType) {
        if (connectorMessageId == null) {
            throw new IllegalArgumentException("ConnectorMessageId is not allowed to be null!");
        }
        return createDomibusConnectorBigDataReference(connectorMessageId.getConnectorMessageId(),
                                                      documentName, documentContentType
        );
    }

    /**
     * Deprecated use {@link #createDomibusConnectorBigDataReference(DomibusConnectorMessageId, String, String)}
     * instead!
     */
    @Deprecated
    LargeFileReference createDomibusConnectorBigDataReference(
            String connectorMessageId,
            String documentName,
            String documentContentType
    );

    /**
     * will delete the provided bigDataReference
     *
     * @param bigDataReference the reference
     * @throws LargeFileDeletionException - in case of anything fails to delete the file
     */
    void deleteDomibusConnectorBigDataReference(LargeFileReference bigDataReference) throws LargeFileDeletionException;

    /**
     * Returns a map of ALL currently not deleted bigDataReferences
     */
    Map<DomibusConnectorMessageId, List<LargeFileReference>> getAllAvailableReferences();
}
