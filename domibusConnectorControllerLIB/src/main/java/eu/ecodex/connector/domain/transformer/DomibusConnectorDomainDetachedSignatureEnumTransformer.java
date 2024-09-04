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

import eu.ecodex.connector.domain.model.DetachedSignatureMimeType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
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
