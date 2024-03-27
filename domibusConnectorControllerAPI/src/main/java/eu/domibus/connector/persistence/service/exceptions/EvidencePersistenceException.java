package eu.domibus.connector.persistence.service.exceptions;

public class EvidencePersistenceException extends PersistenceException {
    public EvidencePersistenceException() {
    }

    public EvidencePersistenceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EvidencePersistenceException(String arg0) {
        super(arg0);
    }

    public EvidencePersistenceException(Throwable arg0) {
        super(arg0);
    }
}
