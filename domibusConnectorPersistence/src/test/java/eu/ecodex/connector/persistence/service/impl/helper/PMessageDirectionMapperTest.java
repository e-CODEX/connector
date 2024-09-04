/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl.helper;

import eu.ecodex.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.ecodex.connector.persistence.model.enums.PMessageDirection;
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
