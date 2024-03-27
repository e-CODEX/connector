package eu.domibus.connector.domain.transformer;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import org.junit.jupiter.api.Test;


/**
 * This test ensures that the used enums can be converted
 * in both directions
 * <p>
 * If one of the enums are extended and the other part not this tests
 * should fail
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
class EnumTransformationTest {
    @Test
    void testDetachedSignatureMimeType_transformDomainToTransition() {
        for (DetachedSignatureMimeType domainMimeType : DetachedSignatureMimeType.values()) {
            DomibusConnectorDomainDetachedSignatureEnumTransformer.transformDetachedSignatureMimeTypeDomainToTransition(
                    domainMimeType);
        }
    }

    @Test
    void testDetachedSignatureMimeType_transformTransitionToDomain() {
        for (DomibusConnectorDetachedSignatureMimeType transitionMimeType :
                DomibusConnectorDetachedSignatureMimeType.values()) {
            DomibusConnectorDomainDetachedSignatureEnumTransformer.transformDetachedSignatureMimeTypeTransitionToDomain(
                    transitionMimeType);
        }
    }

    @Test
    void testEvidenceType_transformDomainToTransition() {
        for (DomibusConnectorEvidenceType domainEvidenceType : DomibusConnectorEvidenceType.values()) {
            DomibusConnectorConfirmationType.valueOf(domainEvidenceType.name());
        }
    }

    @Test
    void testEvidenceType_transformTransitionToDomain() {
        for (DomibusConnectorConfirmationType transitionEvidenceType : DomibusConnectorConfirmationType.values()) {
            DomibusConnectorEvidenceType.valueOf(transitionEvidenceType.name());
        }
    }
}
