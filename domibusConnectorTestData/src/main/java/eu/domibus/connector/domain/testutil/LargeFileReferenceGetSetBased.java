package eu.domibus.connector.domain.testutil;


import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.testutils.LargeFileProviderMemoryImpl;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Base64;
import java.util.UUID;


/**
 * A memory based impl for handling files
 * this impl is intended to be used only within tests!
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class LargeFileReferenceGetSetBased extends LargeFileReference {
    byte[] bytes;
    boolean readable;
    boolean writeable;

    public LargeFileReferenceGetSetBased() {
        this.setStorageProviderName(LargeFileProviderMemoryImpl.PROVIDER_NAME);
        this.setStorageIdReference(UUID.randomUUID().toString());
    }

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
        ByteArrayOutputStream b = new OnClassCallbackByteArrayOutputStream(this);
        return b;
    }

    @Override
    public boolean isReadable() {
        return readable;
    }

    @Override
    public boolean isWriteable() {
        return writeable;
    }

    public void setWriteable(boolean writeable) {
        this.writeable = writeable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
        this.setText(Base64.getEncoder().encodeToString(bytes));
        this.setSize((long) bytes.length);
    }

    private static class OnClassCallbackByteArrayOutputStream extends ByteArrayOutputStream {
        private final LargeFileReferenceGetSetBased ref;

        public OnClassCallbackByteArrayOutputStream(LargeFileReferenceGetSetBased ref) {
            this.ref = ref;
        }

        @Override
        public void close() {
            flush();
        }

        @Override
        public void flush() {
            ref.setBytes(this.toByteArray());
        }
    }
}


