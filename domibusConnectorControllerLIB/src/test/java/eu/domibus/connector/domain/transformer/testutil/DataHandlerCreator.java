/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.transformer.testutil;

import eu.domibus.connector.domain.transformer.util.InputStreamDataSource;
import jakarta.activation.DataHandler;

/**
 * A utility class that provides a method to create a DataHandler object from a string input.
 */
public class DataHandlerCreator {
    /**
     * Creates a DataHandler object from a string input.
     *
     * @param input The string input to be converted to a DataHandler object.
     * @return The DataHandler object created from the string input.
     */
    public static DataHandler createDataHandlerFromString(String input) {
        var ds = InputStreamDataSource.inputStreamDataSourceFromByteArray(input.getBytes());
        return new DataHandler(ds);
    }
}
