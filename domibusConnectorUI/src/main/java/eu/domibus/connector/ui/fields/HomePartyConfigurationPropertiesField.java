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

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.evidences.spring.HomePartyConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.context.annotation.Scope;

/**
 * The {@code HomePartyConfigurationPropertiesField} class is a custom field used for configuring a
 * home party in a home party configuration file.
 */
@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class HomePartyConfigurationPropertiesField
    extends CustomField<HomePartyConfigurationProperties> {
    private final SpringBeanValidationBinderFactory validationBinderFactory;
    private final TextField name = new TextField();
    private final TextField endpointAddress = new TextField();
    private final NativeLabel statusLabel = new NativeLabel();
    private final FormLayout formLayout = new FormLayout();
    private final SpringBeanValidationBinder<HomePartyConfigurationProperties> binder;
    private HomePartyConfigurationProperties value;

    /**
     * Constructor.
     *
     * @param validationBinderFactory the SpringBeanValidationBinderFactory used to create the
     *                                validation binder
     */
    public HomePartyConfigurationPropertiesField(
        SpringBeanValidationBinderFactory validationBinderFactory) {
        this.validationBinderFactory = validationBinderFactory;

        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
            ));
        formLayout.addFormItem(name, "Party Name");
        formLayout.addFormItem(endpointAddress, "Party Endpoint Address");

        binder = validationBinderFactory.create(HomePartyConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new HomePartyConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected HomePartyConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(HomePartyConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }
}
