package eu.domibus.connector.domain.transformer.testutil;

import eu.domibus.connector.domain.transformer.util.InputStreamDataSource;

import javax.activation.DataHandler;

public class DataHandlerCreator {

    public static DataHandler createDataHandlerFromString(String input) {
        InputStreamDataSource ds = InputStreamDataSource.InputStreamDataSourceFromByteArray(input.getBytes());
        DataHandler dh = new DataHandler(ds);
        return dh;
    }
}
