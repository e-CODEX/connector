package eu.domibus.connector.persistence.service.impl.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.persistence.model.enums.PMessageDirection;
import org.junit.jupiter.api.Test;


class PMessageDirectionMapperTest {
    @Test
    void mapFromDomainToPersistence() throws Exception {
        for (DomibusConnectorMessageDirection direction : DomibusConnectorMessageDirection.values()) {
            MessageDirectionMapper.mapFromDomainToPersistence(direction);
        }
    }

    @Test
    void mapFromPersistenceToDomain() throws Exception {
        for (PMessageDirection direction : PMessageDirection.values()) {
            MessageDirectionMapper.mapFromPersistenceToDomain(direction);
        }
    }
}
