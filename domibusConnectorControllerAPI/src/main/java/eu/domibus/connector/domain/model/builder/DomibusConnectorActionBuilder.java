/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
