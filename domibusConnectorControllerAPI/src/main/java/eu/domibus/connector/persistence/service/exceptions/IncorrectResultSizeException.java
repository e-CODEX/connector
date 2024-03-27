package eu.domibus.connector.persistence.service.exceptions;

public class IncorrectResultSizeException extends PersistenceException {
    public IncorrectResultSizeException() {
    }

    public IncorrectResultSizeException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public IncorrectResultSizeException(String arg0) {
        super(arg0);
    }

    public IncorrectResultSizeException(Throwable arg0) {
        super(arg0);
    }
}
