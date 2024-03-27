package eu.domibus.connector.controller.exception;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;


public class DomibusConnectorMessageException extends RuntimeException {
    private static final long serialVersionUID = 2899706995862182574L;
    private final DomibusConnectorMessage domibusConnectorMessage;
    private final Class<?> source;

    public DomibusConnectorMessageException() {
        this.source = null;
        this.domibusConnectorMessage = null;
    }

    public DomibusConnectorMessageException(DomibusConnectorMessage message, Class<?> source) {
        super();
        this.domibusConnectorMessage = message;
        this.source = source;
    }

    public DomibusConnectorMessageException(DomibusConnectorMessage message, Class<?> source, Throwable cause) {
        super(cause);
        this.domibusConnectorMessage = message;
        this.source = source;
        this.setStackTrace(cause.getStackTrace());
    }

    public DomibusConnectorMessageException(DomibusConnectorMessage message, Class<?> source, String text) {
        super(text);
        this.domibusConnectorMessage = message;
        this.source = source;
    }

    public DomibusConnectorMessageException(
            DomibusConnectorMessage message,
            Class<?> source,
            Throwable cause,
            String text) {
        super(text, cause);
        this.domibusConnectorMessage = message;
        this.source = source;
        this.setStackTrace(cause.getStackTrace());
    }

    public DomibusConnectorMessage getDomibusConnectorMessage() {
        return domibusConnectorMessage;
    }

    public Class<?> getSource() {
        return source;
    }
}
