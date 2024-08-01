/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.transformer;

import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import lombok.experimental.UtilityClass;

/**
 * Transforms DetachedSignature enums between domain and transition model is necessary because PKCS7
 * has a different name in transition model (PKCS_7).
 */
@UtilityClass
public class DomibusConnectorDomainDetachedSignatureEnumTransformer {
    /**
     * Transforms a DetachedSignatureMimeType from the domain model to the transition model.
     *
     * @param domainMimeType The DetachedSignatureMimeType in the domain model.
     * @return The corresponding DetachedSignatureMimeType in the transition model.
     */
    public static DomibusConnectorDetachedSignatureMimeType
    transformDetachedSignatureMimeTypeDomainToTransition(
        DetachedSignatureMimeType domainMimeType) {
        if (DetachedSignatureMimeType.PKCS7 == domainMimeType) {
            return DomibusConnectorDetachedSignatureMimeType.PKCS_7;
        }
        return DomibusConnectorDetachedSignatureMimeType.valueOf(domainMimeType.name());
    }

    /**
     * Transforms a DetachedSignatureMimeType from the transition model to the domain model.
     *
     * @param mimeTypeTO The DetachedSignatureMimeType in the transition model.
     * @return The corresponding DetachedSignatureMimeType in the domain model.
     */
    public static DetachedSignatureMimeType transformDetachedSignatureMimeTypeTransitionToDomain(
        DomibusConnectorDetachedSignatureMimeType mimeTypeTO) {
        if (DomibusConnectorDetachedSignatureMimeType.PKCS_7 == mimeTypeTO) {
            return DetachedSignatureMimeType.PKCS7;
        }
        return DetachedSignatureMimeType.valueOf(mimeTypeTO.name());
    }
}
