/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorAction;

/**
 * The DomibusConnectorActionBuilder class provides a fluent API for constructing instances of
 * DomibusConnectorAction.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorActionBuilder {
    private String action;

    public static DomibusConnectorActionBuilder createBuilder() {
        return new DomibusConnectorActionBuilder();
    }

    private DomibusConnectorActionBuilder() {
    }

    public DomibusConnectorActionBuilder setAction(String action) {
        this.action = action;
        return this;
    }

    /**
     * Builds a {@link DomibusConnectorAction} object.
     *
     * @return a new {@link DomibusConnectorAction} object
     * @throws IllegalArgumentException if the action is null
     */
    public DomibusConnectorAction build() {
        if (action == null) {
            throw new IllegalArgumentException("action is required!");
        }
        return new DomibusConnectorAction(action);
    }

    /**
     * Copies the properties from the given DomibusConnectorAction to the current
     * DomibusConnectorActionBuilder instance.
     *
     * @param action the DomibusConnectorAction to copy properties from
     * @return the current DomibusConnectorActionBuilder instance
     * @throws IllegalArgumentException if the action is null
     */
    public DomibusConnectorActionBuilder copyPropertiesFrom(DomibusConnectorAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null here!");
        }
        this.action = action.getAction();
        return this;
    }
}
