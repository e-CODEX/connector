package eu.domibus.connector.domain.model;

import org.springframework.core.style.ToStringCreator;

import javax.validation.constraints.NotNull;


/**
 * Internal part of the {@link DomibusConnectorMessage}. All message related
 * errors raised while processing a message and all message related errors
 * reported by the gateway are stored and added to the message.
 * @author riederb
 * @version 1.0
 */
public class DomibusConnectorMessageError {

	@NotNull
	private final String text;
	@NotNull
	private final String details;
	private final String source;
	private final String step;
	private final String processor;

	public DomibusConnectorMessageError(String text, String details, String source, String step, String processor) {
		this.text = text;
		this.details = details;
		this.source = source;
		this.step = step;
		this.processor = processor;
	}

	@Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("errorText", this.text);
        builder.append("source", this.source);
        builder.append("details", this.details);
        return builder.toString();        
    }

	public String getText() {
		return text;
	}

	public String getDetails() {
		return details;
	}

	public String getSource() {
		return source;
	}

	public String getStep() {
		return step;
	}

	public String getProcessor() {
		return processor;
	}
}