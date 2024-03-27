package eu.domibus.connector.domain.transformer.util;

import eu.domibus.connector.domain.model.LargeFileReference;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class LargeFileHandlerBacked extends LargeFileReference {
    private transient DataHandler dataHandler;

    public LargeFileHandlerBacked() {
    }

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

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
}
