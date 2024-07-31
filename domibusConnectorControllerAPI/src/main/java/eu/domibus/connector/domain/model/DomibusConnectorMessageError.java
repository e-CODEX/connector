/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;

/**
 * Internal part of the {@link DomibusConnectorMessage}. All message related errors raised while
 * processing a message and all message related errors reported by the gateway are stored and added
 * to the message.
 *
 * @author riederb
 * @version 1.0
 */
@Data
public class DomibusConnectorMessageError {
    @NotNull
    private final String text;
    @NotNull
    private final String details;
    private final String source;
    private final String step;
    private final String processor;

    /**
     * Represents an error related to a {@link DomibusConnectorMessage}. This class is used to store
     * and add message-related errors raised during processing a message or reported by the
     * gateway.
     */
    public DomibusConnectorMessageError(String text, String details, String source, String step,
                                        String processor) {
        this.text = text;
        this.details = details;
        this.source = source;
        this.step = step;
        this.processor = processor;
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("errorText", this.text);
        builder.append("source", this.source);
        builder.append("details", this.details);
        return builder.toString();
    }
}
