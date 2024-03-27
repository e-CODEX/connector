package eu.domibus.connector.domain.transformer.util;

import eu.domibus.connector.domain.model.LargeFileReference;

import java.io.*;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
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
