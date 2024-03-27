package eu.domibus.connector.domain.transformer.util;

import org.junit.jupiter.api.Test;

import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
class DataHandlerTest {
    @Test
    void testDataHandlerFromDataSource() throws IOException {
        ByteArrayInputStream bin = new ByteArrayInputStream("test".getBytes());
        InputStreamDataSource ds = new InputStreamDataSource(bin);
        DataHandler dh = new DataHandler(ds);

        dh.getInputStream();
    }
}
