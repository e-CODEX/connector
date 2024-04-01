package eu.domibus.connector.ui.view.areas.configuration;

import com.vaadin.flow.data.binder.Binder;


public interface BindInstanceFields<T> {
    void bindInstanceFields(Binder<T> bean);
}
