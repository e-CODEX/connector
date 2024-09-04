/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.model.enums;

import lombok.Getter;

/**
 * Enumeration representing different types of evidence.
 */
@Getter
public enum EvidenceType {
    SUBMISSION_ACCEPTANCE(1),
    SUBMISSION_REJECTION(2),
    RELAY_REMMD_ACCEPTANCE(3),
    RELAY_REMMD_REJECTION(5),
    RELAY_REMMD_FAILURE(4),
    DELIVERY(6),
    NON_DELIVERY(7),
    RETRIEVAL(8),
    NON_RETRIEVAL(9);
    private final int priority;

    EvidenceType(int priority) {
        this.priority = priority;
    }
}
