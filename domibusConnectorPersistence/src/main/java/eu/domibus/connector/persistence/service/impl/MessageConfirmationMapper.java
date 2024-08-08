/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.service.impl.helper.EvidenceTypeMapper;
import eu.domibus.connector.persistence.service.impl.helper.MapperHelper;
import lombok.experimental.UtilityClass;

/**
 * This class provides mapping functionality for converting between different types of objects
 * related to message confirmation.
 */
@UtilityClass
public class MessageConfirmationMapper {
    /**
     * Maps a PDomibusConnectorEvidence object from the database to a
     * DomibusConnectorMessageConfirmation object in the domain.
     *
     * @param e The PDomibusConnectorEvidence object retrieved from the database. Must not be null.
     * @return The mapped DomibusConnectorMessageConfirmation object.
     */
    public static DomibusConnectorMessageConfirmation mapFromDbToDomain(
        PDomibusConnectorEvidence e) {
        var confirmation = new DomibusConnectorMessageConfirmation();
        if (e.getEvidence() != null) {
            confirmation.setEvidence(e.getEvidence().getBytes());
        }
        confirmation.setEvidenceType(EvidenceTypeMapper.mapEvidenceFromDbToDomain(e.getType()));
        return confirmation;
    }

    /**
     * Maps a DomibusConnectorMessageConfirmation object from the domain to a
     * PDomibusConnectorEvidence object in the database.
     *
     * @param evidence     The PDomibusConnectorEvidence object to be mapped. Must not be null.
     * @param confirmation The DomibusConnectorMessageConfirmation object containing the data to be
     *                     mapped. Must not be null.
     * @return The mapped PDomibusConnectorEvidence object.
     */
    public static PDomibusConnectorEvidence mapFromDomainIntoDb(
        PDomibusConnectorEvidence evidence, DomibusConnectorMessageConfirmation confirmation) {
        evidence.setType(
            EvidenceTypeMapper.mapEvidenceTypeFromDomainToDb(confirmation.getEvidenceType()));
        if (confirmation.getEvidence() != null) {
            evidence.setEvidence(MapperHelper.convertByteArrayToString(confirmation.getEvidence()));
        }
        return evidence;
    }

    public static PDomibusConnectorEvidence mapFromDomainToDb(
        DomibusConnectorMessageConfirmation confirmation) {
        var evidence = new PDomibusConnectorEvidence();
        return mapFromDomainIntoDb(evidence, confirmation);
    }
}
