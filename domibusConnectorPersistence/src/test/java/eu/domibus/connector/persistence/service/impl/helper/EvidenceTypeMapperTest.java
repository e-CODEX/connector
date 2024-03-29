package eu.domibus.connector.persistence.service.impl.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import org.junit.jupiter.api.Test;


/*
test that all declared enum types can be mapped in both directions
 */
class EvidenceTypeMapperTest {
    @Test
    void testMapsAllFromDomainToDb() {
        for (DomibusConnectorEvidenceType t : DomibusConnectorEvidenceType.values()) {
            EvidenceTypeMapper.mapEvidenceTypeFromDomainToDb(t);
        }
    }

    @Test
    void testMapsAllFromDbToDomain() {
        for (EvidenceType t : EvidenceType.values()) {
            EvidenceTypeMapper.mapEvidenceFromDbToDomain(t);
        }
    }
}
