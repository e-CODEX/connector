/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.transformer.testutil;

import eu.ecodex.connector.domain.transformer.util.InputStreamDataSource;
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
