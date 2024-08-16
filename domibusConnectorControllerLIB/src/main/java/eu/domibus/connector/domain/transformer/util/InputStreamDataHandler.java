/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.transformer.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;

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
