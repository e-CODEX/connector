/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import eu.domibus.connector.persistence.service.exceptions.LargeFileDeletionException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This interface describes a service for storing large amount of data The by this service returned
 * DomibusConnectorBigDataReference contains a OutputStream and a InputStream if these streams are
 * available depends on the state of the DomibusConnectorBigDataReference also be aware that some
 * implementations can only read and write to these streams during an active transaction (as an
 * example the default database based implementation).
 *
 * <p>The implementation should at least manage 4GB
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface LargeFilePersistenceService {
    /**
     * Returns an instance of DomibusConnectorBigDataReference, where the getInputStream method will
     * return a valid inputStream.
     *
     * @param bigDataReference the DomibusConnectorBigDataReference
     * @return the DomibusConnectorBigDataReference with initialized inputStream
     */
    LargeFileReference getReadableDataSource(LargeFileReference bigDataReference);

    /**
     * Will create a new instance of DomibusConnectorBigDataReference.
     *
     * @param input               the inputstream holding the content
     * @param connectorMessageId  the unique id of this message
     * @param documentName        the name of this document
     * @param documentContentType the type of this document
     * @return the created DomibusConnectorBigDataReference with CLOSED OutputStream
     * @deprecated This method is deprecated. Use the alternative method
     *      createDomibusConnectorBigDataReference(DomibusConnectorMessageId connectorMessageId,
     *      String documentName, String documentContentType) instead.
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
    LargeFileReference createDomibusConnectorBigDataReference(
        DomibusConnectorMessageId connectorMessageId, String documentName,
        String documentContentType);

    /**
     * Creates a new instance of DomibusConnectorBigDataReference using the deprecated method
     * createDomibusConnectorBigDataReference.
     *
     * @param connectorMessageId  the unique id of this message
     * @param documentName        the name of this document
     * @param documentContentType the type of this document
     * @return the created DomibusConnectorBigDataReference
     * @deprecated This method is deprecated. Use the alternative method
     *      createDomibusConnectorBigDataReference(DomibusConnectorMessageId connectorMessageId,
     *      String documentName, String documentContentType) instead.
     */
    @Deprecated
    default LargeFileReference createDomibusConnectorBigDataReference(String connectorMessageId,
                                                                      String documentName,
                                                                      String documentContentType) {
        return createDomibusConnectorBigDataReference(
            new DomibusConnectorMessageId(connectorMessageId), documentName, documentContentType);
    }

    /**
     * Will delete the provided bigDataReference.
     *
     * @param bigDataReference the reference
     * @throws LargeFileDeletionException - in case of anything fails to delete the file
     */
    void deleteDomibusConnectorBigDataReference(LargeFileReference bigDataReference)
        throws LargeFileDeletionException;

    /**
     * Returns a map of ALL currently not deleted bigDataReferences.
     */
    Map<DomibusConnectorMessageId, List<LargeFileReference>> getAllAvailableReferences();

    boolean isStorageProviderAvailable(LargeFileReference toCopy);

    LargeFilePersistenceProvider getDefaultProvider();
}
