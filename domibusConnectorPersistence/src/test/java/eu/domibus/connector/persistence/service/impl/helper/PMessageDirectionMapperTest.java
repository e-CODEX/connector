package eu.domibus.connector.persistence.service.impl.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.persistence.model.enums.PMessageDirection;
import org.junit.jupiter.api.Test;

/**
 * This class is a JUnit test class for {@link MessageDirectionMapper} class. It tests the mapping
 * from domain layer to persistence layer and vice versa.
 */
public class PMessageDirectionMapperTest {
    @Test
    void mapFromDomainToPersistence() {
        for (DomibusConnectorMessageDirection direction :
            DomibusConnectorMessageDirection.values()) {
            MessageDirectionMapper.mapFromDomainToPersistence(direction);
        }
    }

    @Test
    void mapFromPersistenceToDomain() {
        for (PMessageDirection direction : PMessageDirection.values()) {
            MessageDirectionMapper.mapFromPersistenceToDomain(direction);
        }
    }
}
