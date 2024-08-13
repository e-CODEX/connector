/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.testutil;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.domain.testutil.LargeFileReferenceGetSetBased;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is an implementation of the LargeFilePersistenceService and
 * LargeFilePersistenceProvider interfaces. It provides a passthrough behavior for accessing large
 * files.
 *
 * <p>This class implements the LargeFilePersistenceService and LargeFilePersistenceProvider
 * interfaces to allow transparent access to large files. The passthrough behavior means that this
 * implementation does not actually store or persist the large files, but instead provides direct
 * access to the underlying input and output streams of the provided LargeFileReference object.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class LargeFilePersistenceServicePassthroughImpl
    implements LargeFilePersistenceService, LargeFilePersistenceProvider {
    @Override
    public String getProviderName() {
        return "passthrough";
    }

    @Override
    public LargeFileReference getReadableDataSource(LargeFileReference bigDataReference) {
        LargeFileReferenceGetSetBased bigDataReference2 =
            (LargeFileReferenceGetSetBased) bigDataReference;
        try {
            if (bigDataReference2.getInputStream() == null) {
                bigDataReference2.getOutputStream();
                return bigDataReference2;
            }
            return bigDataReference2;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
        InputStream input, String connectorMessageId, String documentName,
        String documentContentType) {

        return new LargeFileReferenceGetSetBased();
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
        DomibusConnectorMessageId connectorMessageId, String documentName,
        String documentContentType) {
        return new LargeFileReferenceGetSetBased();
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
        String connectorMessageId, String documentName, String documentContentType) {
        return new LargeFileReferenceGetSetBased();
    }

    @Override
    public void deleteDomibusConnectorBigDataReference(LargeFileReference ref) {
        // just do nothing
    }

    @Override
    public Map<DomibusConnectorMessageId, List<LargeFileReference>> getAllAvailableReferences() {
        // just return empty map
        return new HashMap<>();
    }

    @Override
    public boolean isStorageProviderAvailable(LargeFileReference toCopy) {
        return true;
    }

    @Override
    public LargeFilePersistenceProvider getDefaultProvider() {
        return this;
    }
}
