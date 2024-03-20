package eu.domibus.connector.domain.transformer.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.activation.DataHandler;
import org.junit.jupiter.api.Test;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class DataHandlerTest {

    
    @Test
    public void testDataHandlerFromDataSource() throws IOException {

        ByteArrayInputStream bin = new ByteArrayInputStream("test".getBytes());        
        InputStreamDataSource ds = new InputStreamDataSource(bin);        
        DataHandler dh = new DataHandler(ds);
        
        dh.getInputStream();
        
        
    }
}
