package eu.domibus.connector.ui.view.areas.configuration.link.gwwspullplugin;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.ui.fields.CxfTrustKeyStoreConfigurationPropertiesField;
import eu.domibus.connector.ui.fields.SpringResourceField;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.domibus.connectorplugins.link.gwwspullplugin.DCGatewayPullPluginConfigurationProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;


@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DCGatewayPullPluginConfigurationPropertiesField extends CustomField<DCGatewayPullPluginConfigurationProperties> implements AfterNavigationObserver {
    private final SpringBeanValidationBinder<DCGatewayPullPluginConfigurationProperties> binder;

    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();

    private final TextField gwAddress = new TextField();
    private final Checkbox cxfLoggingEnabled = new Checkbox();
    private final CxfTrustKeyStoreConfigurationPropertiesField soap;
    private final SpringResourceField wsPolicy;

    private DCGatewayPullPluginConfigurationProperties value = new DCGatewayPullPluginConfigurationProperties();

    public DCGatewayPullPluginConfigurationPropertiesField(
            SpringBeanValidationBinderFactory validationBinderFactory,
            CxfTrustKeyStoreConfigurationPropertiesField soap,
            SpringResourceField wsPolicy) {
        binder = validationBinderFactory.create(DCGatewayPullPluginConfigurationProperties.class);
        this.soap = soap;
        this.wsPolicy = wsPolicy;
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
        formLayout.addFormItem(gwAddress, "gwAddress");
        formLayout.addFormItem(soap, "Soap");
        formLayout.addFormItem(wsPolicy, "WS Policy");
        formLayout.addFormItem(cxfLoggingEnabled, "cxfLoggingEnabled");

        binder.setStatusLabel(statusLabel);
        binder.bindInstanceFields(this);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        DCGatewayPullPluginConfigurationProperties changedValue = new DCGatewayPullPluginConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    @Override
    protected DCGatewayPullPluginConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(DCGatewayPullPluginConfigurationProperties value) {
        binder.readBean(value);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
    }
}
