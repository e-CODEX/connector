/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.transformer.util;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

/**
 * The InputStreamDataHandler class is a subclass of the DataHandler class. It provides a way to
 * handle data from an input stream. This class is used to encapsulate the data and provide access
 * to it.
 *
 * <p>The constructor of this class takes a DataSource object as a parameter. The DataSource object
 * represents the source of the data, which can be an input stream. The InputStreamDataHandler class
 * then uses this source to read and handle the data.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class InputStreamDataHandler extends DataHandler {
    public InputStreamDataHandler(DataSource ds) {
        super(ds);
    }
}
