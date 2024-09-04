/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.enums;

import lombok.Getter;

/**
 * Brings the ETSI-REM evidence model into the connector.
 *
 * <p>A higher priority overrules the meaning of an evidence
 * with lower priority
 *
 * <p>the maxOccurence is telling the connector how often
 * an evidence can occur (-1 means no limit)
 *
 * <p>if positive is true an evidence is confirming a message
 * if negative the message has failed or has been rejected
 */
// @Getter
public enum DomibusConnectorEvidenceType {
    SUBMISSION_ACCEPTANCE(1, true, 1),
    SUBMISSION_REJECTION(2, false, 1),
    RELAY_REMMD_ACCEPTANCE(3, true, -1),
    RELAY_REMMD_REJECTION(5, false, -1),
    RELAY_REMMD_FAILURE(4, false, -1),
    DELIVERY(6, true, 1),
    NON_DELIVERY(7, false, 1),
    RETRIEVAL(8, true, 1),
    NON_RETRIEVAL(9, false, 1);
    private final int priority;
    private final boolean positive;
    private final int maxOccurence;

    DomibusConnectorEvidenceType(int priority, boolean positive, int maxOccurence) {
        this.positive = positive;
        this.maxOccurence = maxOccurence;
        this.priority = priority;
    }

    public boolean isPositive() {
        return positive;
    }

    public int getMaxOccurence() {
        return maxOccurence;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
