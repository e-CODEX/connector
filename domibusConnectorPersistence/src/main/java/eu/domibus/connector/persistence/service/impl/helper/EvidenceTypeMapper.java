/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.service.impl.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import jakarta.annotation.Nonnull;
import lombok.experimental.UtilityClass;

/**
 * This class provides mapping functionality to convert between different types of evidence.
 */
@UtilityClass
public class EvidenceTypeMapper {
    public static EvidenceType mapEvidenceTypeFromDomainToDb(
        @Nonnull DomibusConnectorEvidenceType evidenceType) {
        return EvidenceType.valueOf(evidenceType.name());
    }

    public static DomibusConnectorEvidenceType mapEvidenceFromDbToDomain(
        @Nonnull EvidenceType evidenceType) {
        return DomibusConnectorEvidenceType.valueOf(evidenceType.name());
    }
}
