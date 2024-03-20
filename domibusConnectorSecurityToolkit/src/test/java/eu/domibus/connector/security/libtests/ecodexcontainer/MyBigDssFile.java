
package eu.domibus.connector.security.libtests.ecodexcontainer;

import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class MyBigDssFile implements DSSDocument {

    @Override
    public InputStream openStream() throws DSSException {
        InputStream inputStream = new InputStream() {
            
            private final int length = 4194304; //4 GB 1024byte * 1024 * 4

            private int counter = 0;
            
            @Override
            public int read() throws IOException {
                if (counter > length) {
                    return -1;
                }                
                counter++;
                return (int) (Math.random() * 200);
            }            
        };        
        
        return inputStream;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        throw new UnsupportedOperationException("This document is read only!"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "random.bin";
    }

    @Override
    public void setName(String string) {
        throw new UnsupportedOperationException("This document is read only!");
    }

    @Override
    public MimeType getMimeType() {
        return MimeTypeEnum.BINARY;
    }

    @Override
    public void setMimeType(MimeType mt) {
        throw new UnsupportedOperationException("This document is read only!");
    }

    @Override
    public void save(String string) throws IOException {
        throw new UnsupportedOperationException("This document is read only!");
    }

    @Override
    public String getDigest(DigestAlgorithm da) {
        return DigestAlgorithm.MD5.getName();
    }

}
