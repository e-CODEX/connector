/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.processor.confirmation;

import eu.ecodex.connector.controller.exception.DomibusConnectorControllerException;

/**
 * The CheckEvidencesTimeoutProcessor interface represents a processor that checks the timeout
 * for evidences.
 * It defines a single method checkEvidencesTimeout() that
 * throws a DomibusConnectorControllerException.
 * Implementing classes should provide an implementation for this method.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface CheckEvidencesTimeoutProcessor {
    void checkEvidencesTimeout() throws DomibusConnectorControllerException;
}
