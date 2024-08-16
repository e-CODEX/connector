/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.shared.Registration;
import eu.domibus.connector.ui.utils.field.FindFieldService;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import eu.domibus.connector.utils.service.PropertyMapToBeanConverter;
import eu.ecodex.utils.configuration.domain.ConfigurationProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

/**
 * This class represents a custom field for a list of configuration properties. It extends the
 * CustomField class provided by Spring Framework.
 */
@org.springframework.stereotype.Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DCConfigurationPropertiesListField extends CustomField<Map<String, String>> {
    private final BeanToPropertyMapConverter beanToPropertyMapConverter;
    private final PropertyMapToBeanConverter propertyMapToBeanConverter;
    private final FindFieldService findFieldService;
    private final javax.validation.Validator jsrValidator;
    private final VerticalLayout layout = new VerticalLayout();
    private List<Class<?>> configurationClasses = new ArrayList<>();
    private Binder<Map<String, String>> binder = new Binder<>();
    private Map<String, String> value;
    private final Map<Class<?>, Component> fields = new HashMap<>();
    private boolean readOnly;
    private Map<String, String> presentationValue;

    /**
     * Constructor.
     *
     * @param beanToPropertyMapConverter the BeanToPropertyMapConverter used to convert a Bean to a
     *                                   map of String properties
     * @param propertyMapToBeanConverter the PropertyMapToBeanConverter used to convert a map of
     *                                   properties to a Java bean object
     * @param jsrValidator               the JSR Validator used for Bean validation
     * @param findFieldService           the FindFieldService used to find fields
     */
    public DCConfigurationPropertiesListField(
        BeanToPropertyMapConverter beanToPropertyMapConverter,
        PropertyMapToBeanConverter propertyMapToBeanConverter,
        javax.validation.Validator jsrValidator,
        FindFieldService findFieldService) {

        this.jsrValidator = jsrValidator;
        this.beanToPropertyMapConverter = beanToPropertyMapConverter;
        this.propertyMapToBeanConverter = propertyMapToBeanConverter;
        this.findFieldService = findFieldService;

        initUI();
    }

    public void initUI() {
        this.add(layout);
        updateUI();
    }

    @Override
    public Map<String, String> getEmptyValue() {
        return new HashMap<>();
    }

    @Override
    public Registration addValueChangeListener(
        ValueChangeListener<? super ComponentValueChangeEvent<CustomField<Map<String, String>>,
            Map<String, String>>> listener) {
        return super.addValueChangeListener(listener);
    }

    public Collection<ConfigurationProperty> getConfigurationProperties() {
        return new ArrayList<>();
    }

    public void setConfigurationClasses(List<Class<?>> classes) {
        this.configurationClasses = classes;
        updateUI();
    }

    /**
     * Sets the read-only state of the component. When the component is set to read-only, it will
     * not accept user input and its appearance may change to indicate that it is in a read-only
     * state. Additionally, any associated UI updates triggered by user actions may be disabled or
     * restricted.
     *
     * @param readOnly true to set the component to read-only, false otherwise
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        this.readOnly = readOnly;
        updateUI();
    }

    private void updateUI() {
        layout.removeAll();
        fields.clear();
        binder = new Binder<>();

        binder.addValueChangeListener(this::valueChanged);
        // generate fields
        configurationClasses.forEach(this::processConfigCls);

        binder.readBean(presentationValue);

        binder.setReadOnly(readOnly);
    }

    private <T> void processConfigCls(Class<T> cls) {
        CustomField<T> field = findFieldService.findField(cls);
        var statusLabel = new Label();
        fields.put(cls, field);
        layout.add(statusLabel);
        layout.add(field);

        binder.forField(field)
              .withStatusLabel(statusLabel)
              .bind(
                  (ValueProvider<Map<String, String>, T>) stringStringMap ->
                      propertyMapToBeanConverter.loadConfigurationOnlyFromMap(
                          stringStringMap, cls, ""),
                  (Setter<Map<String, String>, T>) (o, o2) -> {
                      Map<String, String> stringStringMap =
                          beanToPropertyMapConverter.readBeanPropertiesToMap(o2, "");
                      o.putAll(stringStringMap);
                  }
              );
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        Map<String, String> changedValue = new HashMap<>();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected Map<String, String> generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(Map<String, String> value) {
        presentationValue = value;
        updateUI();
    }
}


