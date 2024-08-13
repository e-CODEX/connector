/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.testutils;

import eu.domibus.connector.common.SpringProfiles;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.domain.testutil.LargeFileReferenceGetSetBased;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * The LargeFileProviderMemoryImpl class is an implementation of the LargeFilePersistenceProvider
 * interface. It provides functionality to store and retrieve large file references in memory.
 *
 * <p>This class is annotated with the @Component annotation, indicating that it is a candidate for
 * auto-detection when using annotation-based configuration and classpath scanning.
 * The @ConditionalOnMissingBean annotation ensures that this class is only instantiated if
 * there is no other bean of the same type present in the application context.
 * The @Profile(SpringProfiles.TEST) annotation indicates that this class should only be used
 * in the "test" profile.
 */
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
        return new LargeFileReferenceGetSetBased(bigDataReference);
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
        InputStream input, String connectorMessageId, String documentName,
        String documentContentType) {
        return createDomibusConnectorBigDataReference(
            connectorMessageId, documentName, documentContentType);
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
        String connectorMessageId, String documentName, String documentContentType) {
        var dataRef = new LargeFileReferenceGetSetBased();
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
    //
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
    // //            reference.setOutputStream(null);
    //            super.close();
    //        }
    //
    //    }
}
