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
import eu.ecodex.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import eu.ecodex.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.ecodex.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The KeyAndKeyStoreAndTrustStoreConfigurationField class is a custom field component that
 * represents the configuration properties for a key, key store, and trust store.
 */
@SuppressWarnings("squid:S1135")
@Component(KeyAndKeyStoreAndTrustStoreConfigurationField.BEAN_NAME)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KeyAndKeyStoreAndTrustStoreConfigurationField
    extends CustomField<KeyAndKeyStoreAndTrustStoreConfigurationProperties> {
    public static final String BEAN_NAME = "KeyAndKeyStoreAndTrustStoreConfigurationField";
    private final SpringBeanValidationBinderFactory validationBinderFactory;
    private final StoreConfigurationField trustStore;
    private final StoreConfigurationField keyStore;
    private final KeyConfigurationField privateKey;
    private final NativeLabel statusLabel = new NativeLabel();
    private final FormLayout formLayout = new FormLayout();
    private final SpringBeanValidationBinder<KeyAndKeyStoreAndTrustStoreConfigurationProperties>
        binder;
    KeyAndKeyStoreAndTrustStoreConfigurationProperties value;

    /**
     * Constructor.
     *
     * @param validationBinderFactory the SpringBeanValidationBinderFactory instance used for
     *                                creating SpringBeanValidationBinder objects
     * @param trustStore              the StoreConfigurationField instance for configuring the trust
     *                                store properties
     * @param keyStore                the StoreConfigurationField instance for configuring the key
     *                                store properties
     * @param privateKey              the KeyConfigurationField instance for configuring the private
     *                                key properties
     */
    public KeyAndKeyStoreAndTrustStoreConfigurationField(
        SpringBeanValidationBinderFactory validationBinderFactory,
        StoreConfigurationField trustStore,
        StoreConfigurationField keyStore,
        KeyConfigurationField privateKey) {
        this.validationBinderFactory = validationBinderFactory;
        this.trustStore = trustStore;
        this.keyStore = keyStore;
        this.privateKey = privateKey;

        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
            ));
        formLayout.addFormItem(this.trustStore, "Trust Store");
        formLayout.addFormItem(this.keyStore, "Key Store");
        formLayout.addFormItem(this.privateKey, "Private Key");

        // TODO: add alternative view, when associated with keystore...some kind of select box...

        binder = validationBinderFactory.create(
            KeyAndKeyStoreAndTrustStoreConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new KeyAndKeyStoreAndTrustStoreConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        trustStore.setReadOnly(readOnly);
        keyStore.setReadOnly(readOnly);
        privateKey.setReadOnly(readOnly);
    }

    @Override
    protected KeyAndKeyStoreAndTrustStoreConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(
        KeyAndKeyStoreAndTrustStoreConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }
}
