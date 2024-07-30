/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.processor.confirmation;

import eu.domibus.connector.controller.exception.DomibusConnectorControllerException;

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
