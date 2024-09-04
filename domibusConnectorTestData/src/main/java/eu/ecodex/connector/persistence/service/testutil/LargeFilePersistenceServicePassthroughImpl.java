/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.testutil;

import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.model.LargeFileReference;
import eu.ecodex.connector.domain.testutil.LargeFileReferenceGetSetBased;
import eu.ecodex.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import eu.ecodex.connector.persistence.service.LargeFilePersistenceService;
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
