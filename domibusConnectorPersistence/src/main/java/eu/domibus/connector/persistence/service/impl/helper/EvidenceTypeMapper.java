package eu.domibus.connector.persistence.service.impl.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.persistence.model.enums.EvidenceType;

import javax.annotation.Nonnull;

public class EvidenceTypeMapper {

    public static EvidenceType mapEvidenceTypeFromDomainToDb(@Nonnull DomibusConnectorEvidenceType evidenceType) {
        return EvidenceType.valueOf(evidenceType.name());
    }

    public static DomibusConnectorEvidenceType mapEvidenceFromDbToDomain(@Nonnull EvidenceType evidenceType) {
        return DomibusConnectorEvidenceType.valueOf(evidenceType.name());
    }

}
