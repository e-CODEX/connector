package eu.domibus.connector.domain.transformer.util;

import jakarta.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class DataHandlerTest {
    @Test
    void testDataHandlerFromDataSource() throws IOException {

        ByteArrayInputStream bin = new ByteArrayInputStream("test".getBytes());
        InputStreamDataSource ds = new InputStreamDataSource(bin);
        DataHandler dh = new DataHandler(ds);

        dh.getInputStream();
    }
}
