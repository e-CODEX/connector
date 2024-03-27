package eu.domibus.connector.controller.exception;

public class ErrorCode {
    public static final ErrorCode EVIDENCE_IGNORED_MESSAGE_ALREADY_REJECTED = new ErrorCode(
            "E101",
            "The processed evidence is ignored, because the business message is already in rejected state"
    );
    public static final ErrorCode EVIDENCE_IGNORED_DUE_DUPLICATE = new ErrorCode(
            "E102",
            "The processed evidence is ignored, because max occurrence number of evidence type exceeded"
    );
    public static final ErrorCode EVIDENCE_IGNORED_DUE_HIGHER_PRIORITY = new ErrorCode(
            "E103",
            "The processed evidence is not relevant due another evidence with higher priority"
    );

    public static final ErrorCode LINK_PARTNER_NOT_FOUND = new ErrorCode(
            "L104",
            "The requested LinkPartner is not configured"
    );
    public static final ErrorCode LINK_PARTNER_NOT_ACTIVE = new ErrorCode(
            "L101",
            "The requested LinkPartner is not active"
    );

    private final String errorCode;
    private final String description;

    public ErrorCode(String errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return this.errorCode + ": " + this.description;
    }
}
