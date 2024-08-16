/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.security;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.security.exception.DomibusConnectorSecurityException;

/**
 * Interface with methods to invoke WP4 functionality.
 *
 * @author riederb
 */
public interface DomibusConnectorSecurityToolkit {
    DomibusConnectorMessage validateContainer(DomibusConnectorMessage message)
        throws DomibusConnectorSecurityException;

    DomibusConnectorMessage buildContainer(DomibusConnectorMessage message)
        throws DomibusConnectorSecurityException;
}
