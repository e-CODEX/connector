/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The {@code KeyConfigurationField} class is a custom field component that represents a key
 * configuration.
 *
 * @see CustomField
 * @see KeyConfigurationProperties
 * @see SpringBeanValidationBinder
 * @see SpringBeanValidationBinderFactory
 */
@SuppressWarnings("squid:S1135")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KeyConfigurationField extends CustomField<KeyConfigurationProperties> {
    private final SpringBeanValidationBinderFactory validationBinderFactory;
    private final TextField alias = new TextField();
    private final PasswordField password = new PasswordField();
    private final NativeLabel statusLabel = new NativeLabel();
    private final FormLayout formLayout = new FormLayout();
    private final SpringBeanValidationBinder<KeyConfigurationProperties> binder;
    KeyConfigurationProperties value;

    /**
     * Constructor.
     *
     * @see CustomField
     * @see KeyConfigurationProperties
     * @see SpringBeanValidationBinder
     * @see SpringBeanValidationBinderFactory
     */
    public KeyConfigurationField(SpringBeanValidationBinderFactory validationBinderFactory) {
        this.validationBinderFactory = validationBinderFactory;

        this.add(statusLabel);
        this.add(formLayout);

        formLayout.addFormItem(alias, "Key Alias");
        formLayout.addFormItem(password, "Key Password");
        // TODO: add alternative view, when associated with keystore...some kind of select box...

        binder = validationBinderFactory.create(KeyConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        alias.setReadOnly(readOnly);
        password.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new KeyConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected KeyConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(KeyConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }
}
