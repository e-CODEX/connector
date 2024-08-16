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
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The CxfTrustKeyStoreConfigurationPropertiesField class represents a custom field in a form that
 * is used to configure trust key store properties for the CXF framework.
 */
@Component(CxfTrustKeyStoreConfigurationPropertiesField.BEAN_NAME)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("squid:S1135")
public class CxfTrustKeyStoreConfigurationPropertiesField
    extends CustomField<CxfTrustKeyStoreConfigurationProperties> {
    public static final String BEAN_NAME = "CxfTrustKeyStoreConfigurationPropertiesField";
    private final TextField encryptAlias = new TextField();
    private final StoreConfigurationField trustStore;
    private final StoreConfigurationField keyStore;
    private final KeyConfigurationField privateKey;
    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();
    private final SpringBeanValidationBinder<CxfTrustKeyStoreConfigurationProperties> binder;
    CxfTrustKeyStoreConfigurationProperties value;

    /**
     * Constructor.
     *
     * @param validationBinderFactory the SpringBeanValidationBinderFactory instance used for
     *                                validation and binding of form fields
     * @param trustStore              the StoreConfigurationField instance representing the trust
     *                                store
     * @param keyStore                the StoreConfigurationField instance representing the key
     *                                store
     * @param privateKey              the KeyConfigurationField instance representing the private
     *                                key
     */
    public CxfTrustKeyStoreConfigurationPropertiesField(
        SpringBeanValidationBinderFactory validationBinderFactory,
        StoreConfigurationField trustStore,
        StoreConfigurationField keyStore,
        KeyConfigurationField privateKey) {
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
        formLayout.addFormItem(this.encryptAlias, "encrypt alias");
        formLayout.addFormItem(this.trustStore, "Trust Store");
        formLayout.addFormItem(this.keyStore, "Key Store");
        formLayout.addFormItem(this.privateKey, "Private Key");
        // TODO: add alternative view, when associated with keystore...some kind of select box...

        binder = validationBinderFactory.create(CxfTrustKeyStoreConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        encryptAlias.setReadOnly(readOnly);
        trustStore.setReadOnly(readOnly);
        keyStore.setReadOnly(readOnly);
        privateKey.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new CxfTrustKeyStoreConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected CxfTrustKeyStoreConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(
        CxfTrustKeyStoreConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }
}
