/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration;

import com.vaadin.flow.data.binder.Binder;

/**
 * The {@code BindInstanceFields} interface represents a contract for binding instance fields to a
 * bean using a {@link Binder}.
 *
 * @param <T> the type of the bean to bind the instance fields to
 */
public interface BindInstanceFields<T> {
    void bindInstanceFields(Binder<T> bean);
}
