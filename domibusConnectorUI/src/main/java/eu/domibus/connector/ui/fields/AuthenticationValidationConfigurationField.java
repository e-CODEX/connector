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
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The AuthenticationValidationConfigurationField class is a custom field component used for
 * configuring authentication validation settings in a user interface.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuthenticationValidationConfigurationField extends
    CustomField<DCBusinessDocumentValidationConfigurationProperties
        .AuthenticationValidationConfigurationProperties> {
    private final SpringBeanValidationBinderFactory validationBinderFactory;
    private SpringBeanValidationBinder<DCBusinessDocumentValidationConfigurationProperties
        .AuthenticationValidationConfigurationProperties>
        binder;
    private final NativeLabel statusLabel = new NativeLabel();
    private final TextField identityProvider = new TextField();
    private final FormLayout formLayout = new FormLayout();
    private DCBusinessDocumentValidationConfigurationProperties
        .AuthenticationValidationConfigurationProperties
        value = new DCBusinessDocumentValidationConfigurationProperties
        .AuthenticationValidationConfigurationProperties();

    public AuthenticationValidationConfigurationField(
        SpringBeanValidationBinderFactory validationBinderFactory) {
        this.validationBinderFactory = validationBinderFactory;
        initUI();
    }

    private void initUI() {
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep(
                "15cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
            ));

        formLayout.addFormItem(identityProvider, "Authenticated Identity Provider");

        this.add(statusLabel);
        this.add(formLayout);

        binder = validationBinderFactory.create(
            DCBusinessDocumentValidationConfigurationProperties
                .AuthenticationValidationConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {

        var changedValue = new DCBusinessDocumentValidationConfigurationProperties
            .AuthenticationValidationConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected void setPresentationValue(
        DCBusinessDocumentValidationConfigurationProperties
            .AuthenticationValidationConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }

    @Override
    protected DCBusinessDocumentValidationConfigurationProperties
        .AuthenticationValidationConfigurationProperties generateModelValue() {
        return value;
    }
}

