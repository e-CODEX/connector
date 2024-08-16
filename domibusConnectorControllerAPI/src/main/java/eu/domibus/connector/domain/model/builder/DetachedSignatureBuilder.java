/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DetachedSignature;
import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import javax.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

/**
 * Builder for @see eu.domibus.connector.domain.model.DetachedSignatureBuilder
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@NoArgsConstructor
public final class DetachedSignatureBuilder {
    private byte[] detachedSignature;
    private String detachedSignatureName;
    private DetachedSignatureMimeType mimeType;

    public static DetachedSignatureBuilder createBuilder() {
        return new DetachedSignatureBuilder();
    }

    /**
     * The signature @see
     * eu.domibus.connector.domain.model.DetachedSignatureBuilder#detachedSignature
     *
     * @param signature - the signature, must be not null
     * @return the builder
     */
    public DetachedSignatureBuilder setSignature(@NotNull byte[] signature) {
        this.detachedSignature = signature;
        return this;
    }

    /**
     * The name @see
     * eu.domibus.connector.domain.model.DetachedSignatureBuilder#detachedSignatureName
     *
     * @param name - the name
     * @return the builder
     */
    public DetachedSignatureBuilder setName(@NotNull String name) {
        this.detachedSignatureName = name;
        return this;
    }

    /**
     * the mimeType @see eu.domibus.connector.domain.model.DetachedSignatureBuilder#mimeType
     *
     * @param mimeType - mimeType
     * @return the builder
     */
    public DetachedSignatureBuilder setMimeType(@NotNull DetachedSignatureMimeType mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    /**
     * Creates the DetachedSignature based on the provided properties, also checks if the
     * requirements are fulfilled.
     *
     * @return the created DetachedSignature
     * @throws IllegalArgumentException if an argument is missing or illegal
     */
    public DetachedSignature build() {
        if (detachedSignature == null || detachedSignature.length < 1) {
            throw new IllegalArgumentException("");
        }
        if (detachedSignatureName == null) {
            throw new IllegalArgumentException("detachedSignatureName is mandatory!");
        }
        if (mimeType == null) {
            throw new IllegalArgumentException("mimeType is mandatory!");
        }
        return new DetachedSignature(detachedSignature, detachedSignatureName, mimeType);
    }
}
