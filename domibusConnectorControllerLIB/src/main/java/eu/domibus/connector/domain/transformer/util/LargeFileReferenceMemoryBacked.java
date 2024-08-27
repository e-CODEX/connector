/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
