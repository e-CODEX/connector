package eu.domibus.connector.domain.enums;

/**
 * Brings the ETSI-REM evidence model
 * into the connector
 * A higher priority overrules the meaning of a evidence
 * with lower priority
 * the maxOccurence is telling the connector how often
 * an evidence can occur (-1 means no limit)
 * if positive is true a evidence is confirming a message
 * if negative the message has failed or has been rejected
 */
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

    public String toString() {
        return this.name();
    }
}
