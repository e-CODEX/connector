/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.link.wsbackendplugin;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.ui.fields.KeyAndKeyStoreAndTrustStoreConfigurationField;
import eu.domibus.connector.ui.fields.SpringResourceField;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.domibus.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginConfigurationProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * The {@code WsBackendConfigurationField} class represents a custom field component used for
 * configuring the WS backend plugin.
 *
 * @see CustomField
 * @see AfterNavigationObserver
 * @see SpringComponent
 * @see ConfigurableBeanFactory
 * @see SpringBeanValidationBinder
 * @see KeyAndKeyStoreAndTrustStoreConfigurationField
 * @see SpringResourceField
 * @see WsBackendPluginConfigurationProperties
 */
@SuppressWarnings("squid:S1135")
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WsBackendConfigurationField extends CustomField<WsBackendPluginConfigurationProperties>
    implements AfterNavigationObserver {
    private final SpringBeanValidationBinder<WsBackendPluginConfigurationProperties> binder;
    private final NativeLabel statusLabel = new NativeLabel();
    private final FormLayout formLayout = new FormLayout();
    private final TextField backendPublishAddress = new TextField();
    private final Checkbox cxfLoggingEnabled = new Checkbox();
    private final KeyAndKeyStoreAndTrustStoreConfigurationField soap;
    private final SpringResourceField wsPolicy;
    private WsBackendPluginConfigurationProperties value =
        new WsBackendPluginConfigurationProperties();

    /**
     * Constructor.
     *
     * @param validationBinderFactory The validation binder factory used to create instances of
     *                                SpringBeanValidationBinder.
     * @param soap                    The configuration field for SOAP.
     * @param wsPolicyField           The configuration field for the WS policy.
     */
    public WsBackendConfigurationField(
        SpringBeanValidationBinderFactory validationBinderFactory,
        KeyAndKeyStoreAndTrustStoreConfigurationField soap,
        SpringResourceField wsPolicyField) {
        binder = validationBinderFactory.create(WsBackendPluginConfigurationProperties.class);
        this.soap = soap;
        this.wsPolicy = wsPolicyField;
        initUI();
    }

    private void initUI() {
        binder.addValueChangeListener(this::valueChanged);
        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
            ));
        formLayout.addFormItem(backendPublishAddress, "backendPublishAddress");
        formLayout.addFormItem(soap, "Soap");
        formLayout.addFormItem(cxfLoggingEnabled, "cxfLoggingEnabled");
        formLayout.addFormItem(wsPolicy, "WS Security Policy");

        binder.setStatusLabel(statusLabel);
        binder.bindInstanceFields(this);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new WsBackendPluginConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    @Override
    protected WsBackendPluginConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(WsBackendPluginConfigurationProperties value) {
        binder.readBean(value);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        // TODO see why this method body is empty
    }
}
