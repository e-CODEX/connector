/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.evidences;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;

/**
 * Interface to publish methods for creation of eCodex Evidence Messages.
 *
 * @author riederb
 */
public interface DomibusConnectorEvidencesToolkit {
    DomibusConnectorMessageConfirmation createEvidence(
        DomibusConnectorEvidenceType type, DomibusConnectorMessage message,
        DomibusConnectorRejectionReason rejectionReason, String details)
        throws DomibusConnectorEvidencesToolkitException;
}
