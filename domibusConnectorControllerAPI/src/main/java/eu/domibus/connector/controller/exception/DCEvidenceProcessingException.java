package eu.domibus.connector.controller.exception;

public class DCEvidenceProcessingException extends RuntimeException {
    private final ErrorCode errorCode;

    public DCEvidenceProcessingException(ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    public DCEvidenceProcessingException(ErrorCode errorCode, Throwable e) {
        super(errorCode.toString(), e);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
