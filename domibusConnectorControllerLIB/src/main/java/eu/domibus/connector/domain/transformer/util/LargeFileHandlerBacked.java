/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.transformer.util;

import eu.domibus.connector.domain.model.LargeFileReference;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jakarta.activation.DataHandler;
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
