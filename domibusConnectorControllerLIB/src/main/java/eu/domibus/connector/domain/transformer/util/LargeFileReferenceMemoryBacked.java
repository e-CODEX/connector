/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.transformer.util;

import eu.domibus.connector.domain.model.LargeFileReference;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a memory-backed implementation of a LargeFileReference.
 * This class provides methods to store and retrieve file content in memory.
 */
public class LargeFileReferenceMemoryBacked extends LargeFileReference {
    private transient byte[] bytes;
    private boolean read = false;
    private boolean write = false;

    public LargeFileReferenceMemoryBacked(byte[] bytes) {
        this.read = true;
        this.bytes = bytes;
    }

    public LargeFileReferenceMemoryBacked() {
        this.write = true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new ByteArrayOutputStream();
    }

    @Override
    public boolean isReadable() {
        return this.read;
    }

    @Override
    public boolean isWriteable() {
        return this.write;
    }
}
