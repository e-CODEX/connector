/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.link.wsbackendplugin;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.domibus.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginLinkPartnerConfigurationProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * The {@code WsBackendLinkConfigurationField} class represents a custom field used for configuring
 * link partners in the WS backend plugin.
 */
@SuppressWarnings("squid:S1135")
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WsBackendLinkConfigurationField
    extends CustomField<WsBackendPluginLinkPartnerConfigurationProperties>
    implements AfterNavigationObserver {
    private final SpringBeanValidationBinder<WsBackendPluginLinkPartnerConfigurationProperties>
        binder;
    private final TextField pushAddress = new TextField();
    private final TextField encryptionAlias = new TextField();
    private final TextField certificateDn = new TextField();
    private WsBackendPluginLinkPartnerConfigurationProperties value =
        new WsBackendPluginLinkPartnerConfigurationProperties();
    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();

    /**
     * Constructor.
     *
     * @param validationBinderFactory the factory used to create {@link SpringBeanValidationBinder}
     *                                instances
     * @throws NullPointerException if {@code validationBinderFactory} is {@code null}
     */
    public WsBackendLinkConfigurationField(
        SpringBeanValidationBinderFactory validationBinderFactory) {
        binder =
            validationBinderFactory.create(WsBackendPluginLinkPartnerConfigurationProperties.class);
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
        formLayout.addFormItem(pushAddress, "push address");
        // TODO: replace with chooser from truststore
        formLayout.addFormItem(encryptionAlias, "encryption alias (public key in trust store)");
        // TODO: replace with chooser from truststore
        formLayout.addFormItem(certificateDn, "complete certificate dn");

        binder.bindInstanceFields(this);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        var changedValue = new WsBackendPluginLinkPartnerConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    @Override
    protected WsBackendPluginLinkPartnerConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(WsBackendPluginLinkPartnerConfigurationProperties value) {
        binder.readBean(value);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        // TODO see why this method body is empty
    }
}
