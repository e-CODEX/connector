package eu.domibus.connector.persistence.service.testutil;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.domain.testutil.LargeFileReferenceGetSetBased;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class LargeFilePersistenceServicePassthroughImpl implements LargeFilePersistenceService,
        LargeFilePersistenceProvider {
    @Override
    public String getProviderName() {
        return "passthrough";
    }

    @Override
    public LargeFileReference getReadableDataSource(LargeFileReference bigDataReference) {
        LargeFileReferenceGetSetBased bigDataReference2 = (LargeFileReferenceGetSetBased) bigDataReference;
        try {
            if (bigDataReference2.getInputStream() == null) {
                ByteArrayOutputStream out = (ByteArrayOutputStream) bigDataReference2.getOutputStream();
                //				bigDataReference2.setInputStream(new ByteArrayInputStream(out.toByteArray()));
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
        LargeFileReferenceGetSetBased dataRef = new LargeFileReferenceGetSetBased();

        return dataRef;
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
            DomibusConnectorMessageId connectorMessageId,
            String documentName,
            String documentContentType) {
        LargeFileReferenceGetSetBased dataRef = new LargeFileReferenceGetSetBased();
        return dataRef;
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
            String connectorMessageId,
            String documentName,
            String documentContentType) {
        LargeFileReferenceGetSetBased dataRef = new LargeFileReferenceGetSetBased();
        return dataRef;
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
