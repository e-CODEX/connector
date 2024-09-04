/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.security.util;

import eu.ecodex.connector.domain.model.LargeFileReference;
import eu.ecodex.connector.persistence.service.LargeFilePersistenceService;
import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.model.CommonDocument;
import eu.europa.esig.dss.model.DSSDocument;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents a DSS (Document Storage System) document that is based on large files. It
 * extends the CommonDocument class and implements the DSSDocument interface.
 *
 * <p>The LargeFileBasedDssDocument class encapsulates a large file persistence service and a large
 * file reference. It provides methods to open an input stream to read the contents of the document,
 * get the MIME type of the document, and get the name of the document.
 *
 * <p>The large file persistence service is responsible for managing the storage and retrieval of
 * large files. It provides methods to create and delete large file references, and retrieve a
 * readable data source for a given reference.
 *
 * <p>The large file reference represents a reference to a storage system for big files. It
 * contains
 * information such as the storage provider name, storage ID reference, name, MIME type, size, and
 * creation date of the file. The LargeFileReference class also implements the DataSource interface,
 * providing methods to get the input and output streams for the file.
 *
 * <p>The LargeFileBasedDssDocument class uses the large file persistence service to retrieve a
 * readable data source for the large file reference. It then uses the readable data source to open
 * an input stream to read the contents of the document. The MIME type and name of the document are
 * retrieved from the large file reference.
 */
public class LargeFileBasedDssDocument extends CommonDocument implements DSSDocument {
    private final LargeFilePersistenceService persistenceService;
    private final LargeFileReference reference;

    public LargeFileBasedDssDocument(
        LargeFilePersistenceService persistenceService,
        LargeFileReference reference) {
        this.persistenceService = persistenceService;
        this.reference = reference;
    }

    @Override
    public InputStream openStream() {
        LargeFileReference readableDataSource =
            persistenceService.getReadableDataSource(this.reference);
        try (var is = readableDataSource.getInputStream()) {
            return is;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MimeType getMimeType() {
        return MimeType.fromMimeTypeString(reference.getContentType());
    }

    @Override
    public String getName() {
        return reference.getName();
    }
}
