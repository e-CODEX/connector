package eu.domibus.connector.ui.view.areas.configuration.link.wsbackendplugin;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
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


@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WsBackendConfigurationField extends CustomField<WsBackendPluginConfigurationProperties> implements AfterNavigationObserver {
    private final SpringBeanValidationBinder<WsBackendPluginConfigurationProperties> binder;

    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();

    private final TextField backendPublishAddress = new TextField();
    private final Checkbox cxfLoggingEnabled = new Checkbox();
    private final KeyAndKeyStoreAndTrustStoreConfigurationField soap;
    private final SpringResourceField wsPolicy;

    private WsBackendPluginConfigurationProperties value = new WsBackendPluginConfigurationProperties();

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

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep(
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
        WsBackendPluginConfigurationProperties changedValue = new WsBackendPluginConfigurationProperties();
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
    }
}
