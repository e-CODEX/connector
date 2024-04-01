package eu.domibus.connector.persistence.testutils;

import eu.domibus.connector.common.SpringProfiles;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.domain.testutil.LargeFileReferenceGetSetBased;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@ConditionalOnMissingBean(LargeFileProviderMemoryImpl.class)
@Profile(SpringProfiles.TEST)
public class LargeFileProviderMemoryImpl implements LargeFilePersistenceProvider {
    public static final String PROVIDER_NAME = "MEMORY";

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public LargeFileReference getReadableDataSource(LargeFileReference bigDataReference) {
        LargeFileReferenceGetSetBased bigDataReference2 = new LargeFileReferenceGetSetBased(bigDataReference);
        return bigDataReference2;
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
            InputStream input, String connectorMessageId, String documentName,
            String documentContentType) {
        return createDomibusConnectorBigDataReference(connectorMessageId, documentName, documentContentType);
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
            String connectorMessageId,
            String documentName,
            String documentContentType) {
        LargeFileReferenceGetSetBased dataRef = new LargeFileReferenceGetSetBased();
        //        dataRef.setOutputStream(new MyOutputStream(dataRef));
        dataRef.setWriteable(true);
        dataRef.setReadable(false);
        return dataRef;
    }

    @Override
    public void deleteDomibusConnectorBigDataReference(LargeFileReference ref) {
        // Just do nothing!
    }

    @Override
    public Map<DomibusConnectorMessageId, List<LargeFileReference>> getAllAvailableReferences() {
        // just return empty map
        return new HashMap<>();
    }

    //    @Override
    //    public boolean isStorageProviderAvailable(LargeFileReference toCopy) {
    //        return true;
    //    }
    //
    //    @Override
    //    public LargeFilePersistenceProvider getDefaultProvider() {
    //        return this;
    //    }

    //    public static class MyOutputStream extends ByteArrayOutputStream {
    //
    //        private final LargeFileReferenceGetSetBased reference;
    //
    //        public MyOutputStream(LargeFileReferenceGetSetBased reference) {
    //            this.reference = reference;
    //        }
    //
    //        @Override
    //        public void close() throws IOException {
    //            flush();
    //            reference.setWriteable(false);
    //            reference.setBytes(this.toByteArray());
    ////            reference.setOutputStream(null);
    //            super.close();
    //        }
    //
    //    }
}
