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

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.ecodex.connector.evidences.spring.PostalAdressConfigurationProperties;
import eu.ecodex.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.ecodex.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.context.annotation.Scope;

/**
 * The PostalAdressConfigurationPropertiesField class is a custom field used to display and edit
 * {@link PostalAdressConfigurationProperties} objects.
 */
@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class PostalAdressConfigurationPropertiesField
    extends CustomField<PostalAdressConfigurationProperties> {
    private final SpringBeanValidationBinderFactory validationBinderFactory;
    private final TextField street = new TextField();
    private final TextField locality = new TextField();
    private final TextField zipCode = new TextField();
    private final TextField country = new TextField();
    private final NativeLabel statusLabel = new NativeLabel();
    private final FormLayout formLayout = new FormLayout();
    private final SpringBeanValidationBinder<PostalAdressConfigurationProperties> binder;
    private PostalAdressConfigurationProperties value;

    /**
     * Constructor.
     *
     * @param validationBinderFactory the factory for creating instances of
     *                                SpringBeanValidationBinder
     */
    public PostalAdressConfigurationPropertiesField(
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
        formLayout.addFormItem(street, "Street");
        formLayout.addFormItem(locality, "Locality");
        formLayout.addFormItem(zipCode, "ZipCode");
        formLayout.addFormItem(country, "Country (2 Letter Code)");

        binder = validationBinderFactory.create(PostalAdressConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        street.setReadOnly(readOnly);
        locality.setReadOnly(readOnly);
        zipCode.setReadOnly(readOnly);
        country.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new PostalAdressConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected PostalAdressConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(PostalAdressConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }
}
