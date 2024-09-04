/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import eu.ecodex.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The StoreConfigurationField class is a custom field component used for configuring the properties
 * of a keystore or truststore.
 */
@SuppressWarnings("squid:S1135")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class StoreConfigurationField extends CustomField<StoreConfigurationProperties> {
    private final NativeLabel statusLabel = new NativeLabel();
    private final FormLayout formLayout = new FormLayout();
    private final TextField path = new TextField();
    private final PasswordField password = new PasswordField();
    private final Select<String> type = new Select();
    private final Binder<StoreConfigurationProperties> binder;
    private StoreConfigurationProperties value;

    /**
     * Constructor.
     *
     * @param validationBinderFactory an instance of the SpringBeanValidationBinderFactory class
     *                                used for creating instances of SpringBeanValidationBinder to
     *                                bind and validate form fields
     */
    public StoreConfigurationField(SpringBeanValidationBinderFactory validationBinderFactory) {
        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
            ));

        // TODO: add button to show content of key/truststore
        formLayout.addFormItem(path, "Store Location");
        formLayout.addFormItem(password, "Store password");
        formLayout.addFormItem(type, "Store Type");
        type.setItems("JKS", "JCEKS", "PKCS12");

        binder = validationBinderFactory.create(StoreConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);
        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        path.setReadOnly(readOnly);
        password.setReadOnly(readOnly);
        type.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new StoreConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected StoreConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(StoreConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }
}
