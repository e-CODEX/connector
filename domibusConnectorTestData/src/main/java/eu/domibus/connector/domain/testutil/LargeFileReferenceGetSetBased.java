/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.testutil;

import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.testutils.LargeFileProviderMemoryImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.UUID;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * A memory based impl for handling files this impl is intended to be used only within tests.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Data
public class LargeFileReferenceGetSetBased extends LargeFileReference {
    byte[] bytes;
    boolean readable;
    boolean writeable;

    public LargeFileReferenceGetSetBased() {
        this.setStorageProviderName(LargeFileProviderMemoryImpl.PROVIDER_NAME);
        this.setStorageIdReference(UUID.randomUUID().toString());
    }

    /**
     * Creates a new instance of the {@code LargeFileReferenceGetSetBased} class, initializing it
     * with the given {@code LargeFileReference} object.
     *
     * @param ref The reference to copy fields from. Must not be {@code null}.
     * @throws IllegalArgumentException if the {@code ref} parameter is {@code null}.
     */
    public LargeFileReferenceGetSetBased(LargeFileReference ref) {
        super(ref);
        this.setStorageProviderName(LargeFileProviderMemoryImpl.PROVIDER_NAME);
        this.bytes = Base64.getDecoder().decode(ref.getText());
        if (!StringUtils.hasText(this.getStorageIdReference())) {
            this.setStorageIdReference(UUID.randomUUID().toString());
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new OnClassCallbackByteArrayOutputStream(this);
    }

    @Override
    public boolean isReadable() {
        return readable;
    }

    @Override
    public boolean isWriteable() {
        return writeable;
    }

    /**
     * Sets the byte array of the LargeFileReference. This method updates the bytes, text, and size
     * properties of the object.
     *
     * @param bytes The byte array to set as the new value for the bytes' property.
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
        this.setText(Base64.getEncoder().encodeToString(bytes));
        this.setSize((long) bytes.length);
    }

    private static class OnClassCallbackByteArrayOutputStream extends ByteArrayOutputStream {
        private final LargeFileReferenceGetSetBased largeFileReferenceGetSetBased;

        public OnClassCallbackByteArrayOutputStream(LargeFileReferenceGetSetBased ref) {
            this.largeFileReferenceGetSetBased = ref;
        }

        @Override
        public void close() {
            flush();
        }

        @Override
        public void flush() {
            largeFileReferenceGetSetBased.setBytes(this.toByteArray());
        }
    }
}


