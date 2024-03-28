package eu.domibus.connector.evidences.exception;

public class DomibusConnectorEvidencesToolkitException extends RuntimeException {
    private static final long serialVersionUID = 6341995681547056010L;

    public DomibusConnectorEvidencesToolkitException() {
    }

    public DomibusConnectorEvidencesToolkitException(String message) {
        super(message);
    }

    public DomibusConnectorEvidencesToolkitException(Throwable cause) {
        super(cause);
    }

    public DomibusConnectorEvidencesToolkitException(String message, Throwable cause) {
        super(message, cause);
    }
}
