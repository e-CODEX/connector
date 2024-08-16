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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.select.Select;
import eu.domibus.connector.dss.configuration.SignatureConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The {@code SignatureConfigurationField} class is a custom field component that represents a
 * signature configuration.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("squid:S1135")
public class SignatureConfigurationField extends CustomField<SignatureConfigurationProperties> {
    private final SpringBeanValidationBinderFactory validationBinderFactory;
    private final KeyConfigurationField privateKey;
    private final Select<EncryptionAlgorithm> encryptionAlgorithm;
    private final Select<DigestAlgorithm> digestAlgorithm;
    private final StoreConfigurationField keyStore;
    private final SpringBeanValidationBinder<SignatureConfigurationProperties> binder;
    private final Label statusLabel = new Label("");
    private final FormLayout formLayout = new FormLayout();
    private SignatureConfigurationProperties value;

    /**
     * Constructor.
     *
     * @param validationBinderFactory The factory for creating instances of
     *                                {@link SpringBeanValidationBinder} used for data binding and
     *                                validation.
     * @param keyConfigurationField   The configuration field for key properties.
     * @param keyStore                The configuration field for store properties.
     */
    public SignatureConfigurationField(
        SpringBeanValidationBinderFactory validationBinderFactory,
        KeyConfigurationField keyConfigurationField,
        StoreConfigurationField keyStore) {
        this.validationBinderFactory = validationBinderFactory;
        this.keyStore = keyStore;
        this.privateKey = keyConfigurationField;

        add(statusLabel);
        add(formLayout);

        encryptionAlgorithm = new Select<>(EncryptionAlgorithm.values());
        digestAlgorithm = new Select<>(DigestAlgorithm.values());

        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
            ));
        formLayout.addFormItem(encryptionAlgorithm, "Encryption Algorithm");
        formLayout.addFormItem(digestAlgorithm, "Digest Algorithm");
        formLayout.addFormItem(keyStore, "Key Store Configuration");
        formLayout.addFormItem(privateKey, "Private Key Configuration");
        // TODO: set keystore on keyfield, so keyfield can be a chooser

        binder = validationBinderFactory.create(SignatureConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        encryptionAlgorithm.setReadOnly(readOnly);
        digestAlgorithm.setReadOnly(readOnly);
        keyStore.setReadOnly(readOnly);
        privateKey.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new SignatureConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected SignatureConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(SignatureConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }
}
