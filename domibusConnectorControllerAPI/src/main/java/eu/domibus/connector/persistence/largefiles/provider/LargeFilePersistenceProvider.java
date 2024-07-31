/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.largefiles.provider;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.service.exceptions.LargeFileDeletionException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * The LargeFilePersistenceProvider interface represents a provider for storing and retrieving large
 * file references. Implementations of this interface should provide functionality to store,
 * retrieve, delete, and list large file references.
 */
public interface LargeFilePersistenceProvider {
    /**
     * Should return the provider name, the provider name will be stored into the database to make
     * it possible to find the provider again.
     *
     * <p>Decouples the class name of the implementation from the written value into the database
     *
     * @return - the unique name of the LargeFileStorageProvider
     */
    String getProviderName();

    /**
     * returns an instance of DomibusConnectorBigDataReference, where the getInputStream method will
     * return a valid inputStream.
     *
     * @param bigDataReference the DomibusConnectorBigDataReference
     * @return the DomibusConnectorBigDataReference with initialized inputStream
     */
    LargeFileReference getReadableDataSource(LargeFileReference bigDataReference);

    /**
     * Creates a DomibusConnectorBigDataReference object for storing large files in the storage
     * system.
     *
     * @param input               the InputStream containing the data to be stored
     * @param connectorMessageId  the unique ID for the message related to the file
     * @param documentName        the name of the document
     * @param documentContentType the content type of the document
     * @return the created DomibusConnectorBigDataReference object
     * @deprecated This method is deprecated. Use the createDomibusConnectorBigDataReference method
     *      with DomibusConnectorMessageId parameter instead.
     */
    @Deprecated
    LargeFileReference createDomibusConnectorBigDataReference(InputStream input,
                                                              String connectorMessageId,
                                                              String documentName,
                                                              String documentContentType);

    /**
     * Will create a new instance of DomibusConnectorBigDataReference.
     *
     * @param connectorMessageId  the unique id of this message
     * @param documentName        the name of this document
     * @param documentContentType the type of this document
     * @return the created DomibusConnectorBigDataReference with OPEN OutputStream ready for WRITE,
     *      but closed INPUT stream
     */
    default LargeFileReference createDomibusConnectorBigDataReference(
        DomibusConnectorMessageId connectorMessageId, String documentName,
        String documentContentType) {
        if (connectorMessageId == null) {
            throw new IllegalArgumentException("ConnectorMessageId is not allowed to be null!");
        }
        return createDomibusConnectorBigDataReference(connectorMessageId.getConnectorMessageId(),
                                                      documentName, documentContentType
        );
    }

    /**
     * Deprecated: Use the createDomibusConnectorBigDataReference method with
     * DomibusConnectorMessageId parameter instead.
     *
     * <p>Creates a DomibusConnectorBigDataReference object for storing large files in the storage
     * system.
     *
     * @param connectorMessageId  the unique ID for the message related to the file
     * @param documentName        the name of the document
     * @param documentContentType the content type of the document
     * @return the created DomibusConnectorBigDataReference object
     * @deprecated This method is deprecated. Use the createDomibusConnectorBigDataReference method
     *      with DomibusConnectorMessageId parameter instead.
     */
    @Deprecated
    LargeFileReference createDomibusConnectorBigDataReference(String connectorMessageId,
                                                              String documentName,
                                                              String documentContentType);

    /**
     * Deletes the provided DomibusConnectorBigDataReference from the storage system.
     *
     * @param bigDataReference the DomibusConnectorBigDataReference to delete
     * @throws LargeFileDeletionException if there is an error during deletion
     */
    void deleteDomibusConnectorBigDataReference(LargeFileReference bigDataReference)
        throws LargeFileDeletionException;

    /**
     * Retrieves all available references to large files from the storage system.
     *
     * @return A map containing the connector message ID as the key and a list of LargeFileReference
     *      objects as the value.
     */
    Map<DomibusConnectorMessageId, List<LargeFileReference>> getAllAvailableReferences();
}
