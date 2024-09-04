/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.transformer.util;

import eu.ecodex.connector.domain.model.LargeFileReference;
import jakarta.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a data handler backed implementation of {@link LargeFileReference}. It provides
 * read-only access to the large file through a DataHandler object.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Getter
@Setter
@NoArgsConstructor
public class LargeFileHandlerBacked extends LargeFileReference {
    private transient DataHandler dataHandler;

    public LargeFileHandlerBacked(DataHandler dh) {
        this.dataHandler = dh;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (dataHandler == null) {
            throw new IOException("DataHandler Backend is missing");
        }
        return dataHandler.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Read only!");
    }

    @Override
    public boolean isReadable() {
        return this.dataHandler != null;
    }

    @Override
    public boolean isWriteable() {
        return false;
    }
}
