package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorAction;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorActionBuilder {
    private String action;
    // private boolean documentRequired;

    private DomibusConnectorActionBuilder() {
    }

    public static DomibusConnectorActionBuilder createBuilder() {
        return new DomibusConnectorActionBuilder();
    }

    public DomibusConnectorActionBuilder setAction(String action) {
        this.action = action;
        return this;
    }

    // public DomibusConnectorActionBuilder withDocumentRequired(boolean required) {
    // this.documentRequired = required;
    //    return this;
    // }

    public DomibusConnectorAction build() {
        if (action == null) {
            throw new IllegalArgumentException("action is required!");
        }
        return new DomibusConnectorAction(action);
        // return new DomibusConnectorAction(action, documentRequired);
    }

    public DomibusConnectorActionBuilder copyPropertiesFrom(DomibusConnectorAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null here!");
        }
        this.action = action.getAction();
        // this.documentRequired = action.isDocumentRequired();
        return this;
    }
}
