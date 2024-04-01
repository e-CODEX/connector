package eu.domibus.connector.ui.view.areas.configuration.link.gwwspushplugin;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.ui.fields.CxfTrustKeyStoreConfigurationPropertiesField;
import eu.domibus.connector.ui.fields.SpringResourceField;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.domibus.connectorplugins.link.gwwspushplugin.WsGatewayPluginConfigurationProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;


@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WsGatewayPluginConfigurationPropertiesField extends CustomField<WsGatewayPluginConfigurationProperties> {
    private final SpringBeanValidationBinder<WsGatewayPluginConfigurationProperties> binder;

    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();

    private final TextField gwDeliveryServicePublishAddress = new TextField();
    private final TextField gwAddress = new TextField();
    private final Checkbox cxfLoggingEnabled = new Checkbox();
    private final CxfTrustKeyStoreConfigurationPropertiesField soap;
    private final SpringResourceField wsPolicy;

    private WsGatewayPluginConfigurationProperties value = new WsGatewayPluginConfigurationProperties();

    public WsGatewayPluginConfigurationPropertiesField(
            SpringBeanValidationBinderFactory validationBinderFactory,
            CxfTrustKeyStoreConfigurationPropertiesField soap,
            SpringResourceField wsPolicy) {
        binder = validationBinderFactory.create(WsGatewayPluginConfigurationProperties.class);

        this.soap = soap;
        this.wsPolicy = wsPolicy;
        initUI();
    }

    private void initUI() {
        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
        ));
        formLayout.addFormItem(gwDeliveryServicePublishAddress, "web service publish address");
        binder.forField(gwDeliveryServicePublishAddress)
              .bind(
                      WsGatewayPluginConfigurationProperties::getGwDeliveryServicePublishAddress,
                      WsGatewayPluginConfigurationProperties::setGwDeliveryServicePublishAddress
              );

        formLayout.addFormItem(gwAddress, "gwAddress");
        binder.forField(gwAddress)
              .bind(
                      WsGatewayPluginConfigurationProperties::getGwAddress,
                      WsGatewayPluginConfigurationProperties::setGwAddress
              );

        formLayout.addFormItem(soap, "Soap");
        binder.forField(soap)
              .bind(WsGatewayPluginConfigurationProperties::getSoap, WsGatewayPluginConfigurationProperties::setSoap);

        formLayout.addFormItem(wsPolicy, "WS Policy");
        binder.forField(wsPolicy)
              .bind(
                      WsGatewayPluginConfigurationProperties::getWsPolicy,
                      WsGatewayPluginConfigurationProperties::setWsPolicy
              );

        formLayout.addFormItem(cxfLoggingEnabled, "cxfLoggingEnabled");
        binder.forField(cxfLoggingEnabled)
              .bind(
                      WsGatewayPluginConfigurationProperties::isCxfLoggingEnabled,
                      WsGatewayPluginConfigurationProperties::setCxfLoggingEnabled
              );

        binder.setStatusLabel(statusLabel);
        binder.addValueChangeListener(this::valueChanged);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        WsGatewayPluginConfigurationProperties changedValue = new WsGatewayPluginConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    @Override
    protected WsGatewayPluginConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(WsGatewayPluginConfigurationProperties value) {
        binder.readBean(value);
    }
}
