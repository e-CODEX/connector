/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration;

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
