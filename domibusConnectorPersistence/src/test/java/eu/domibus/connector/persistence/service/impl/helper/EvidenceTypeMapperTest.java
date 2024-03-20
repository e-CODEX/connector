package eu.domibus.connector.persistence.service.impl.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import org.junit.jupiter.api.Test;

/*
test that all declared enum types can be mapped in both directions
 */
public class EvidenceTypeMapperTest {


    @Test
    public void testMapsAllFromDomainToDb() {
        for (DomibusConnectorEvidenceType t : DomibusConnectorEvidenceType.values()) {
            EvidenceTypeMapper.mapEvidenceTypeFromDomainToDb(t);
        }
    }

    @Test
    public void testMapsAllFromDbToDomain() {
        for (EvidenceType t : EvidenceType.values()) {
            EvidenceTypeMapper.mapEvidenceFromDbToDomain(t);
        }
    }

}