
/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.transformer;

import eu.ecodex.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.ecodex.connector.domain.model.DetachedSignatureMimeType;
import eu.ecodex.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import org.junit.jupiter.api.Test;

/**
 * This test ensures that the used enums can be converted in both directions.
 *
 * <p>If one of the enums are extended and the other part not this tests should fail
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
class EnumTransformationTest {
    @Test
    void testDetachedSignatureMimeType_transformDomainToTransition() {
        for (DetachedSignatureMimeType domainMimeType : DetachedSignatureMimeType.values()) {
            DomibusConnectorDomainDetachedSignatureEnumTransformer
                .transformDetachedSignatureMimeTypeDomainToTransition(domainMimeType);
        }
    }

    @Test
    void testDetachedSignatureMimeType_transformTransitionToDomain() {
        for (DomibusConnectorDetachedSignatureMimeType transitionMimeType
            : DomibusConnectorDetachedSignatureMimeType.values()) {
            DomibusConnectorDomainDetachedSignatureEnumTransformer
                .transformDetachedSignatureMimeTypeTransitionToDomain(transitionMimeType);
        }
    }

    @Test
    void testEvidenceType_transformDomainToTransition() {
        for (DomibusConnectorEvidenceType domainEvidenceType
            : DomibusConnectorEvidenceType.values()) {
            DomibusConnectorConfirmationType.valueOf(domainEvidenceType.name());
        }
    }

    @Test
    void testEvidenceType_transformTransitionToDomain() {
        for (DomibusConnectorConfirmationType transitionEvidenceType
            : DomibusConnectorConfirmationType.values()) {
            DomibusConnectorEvidenceType.valueOf(transitionEvidenceType.name());
        }
    }
}
