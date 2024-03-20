
package eu.domibus.connector.domain.transformer.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class InputStreamDataSource implements DataSource {

    private InputStream inputStream;

    private boolean available = true;
    
    private String contentType = "application/octet-stream";
    
    public InputStreamDataSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public InputStreamDataSource(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (available) {
            available = false;
            return this.inputStream;
        } else {
            throw new IOException("Input Stream is already consumed");
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return "";
    }
    
    public static InputStreamDataSource InputStreamDataSourceFromByteArray(byte[] bytes) {
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        return new InputStreamDataSource(bytesIn);
    }
    
}