/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
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
    private final Label statusLabel = new Label();
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

