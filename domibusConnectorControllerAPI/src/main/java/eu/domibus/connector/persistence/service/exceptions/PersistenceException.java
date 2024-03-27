package eu.domibus.connector.persistence.service.exceptions;

public class PersistenceException extends RuntimeException {
    private static final long serialVersionUID = -5879716562932550680L;

    public PersistenceException() {
    }

    public PersistenceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public PersistenceException(String arg0) {
        super(arg0);
    }

    public PersistenceException(Throwable arg0) {
        super(arg0);
    }
}
