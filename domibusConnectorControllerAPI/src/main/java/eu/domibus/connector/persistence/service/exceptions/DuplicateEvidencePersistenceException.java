package eu.domibus.connector.persistence.service.exceptions;

public class DuplicateEvidencePersistenceException extends EvidencePersistenceException {
    public DuplicateEvidencePersistenceException() {
    }

    public DuplicateEvidencePersistenceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DuplicateEvidencePersistenceException(String arg0) {
        super(arg0);
    }

    public DuplicateEvidencePersistenceException(Throwable arg0) {
        super(arg0);
    }
}
