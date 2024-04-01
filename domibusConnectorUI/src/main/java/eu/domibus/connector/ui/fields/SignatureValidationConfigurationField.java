package eu.domibus.connector.ui.fields;


import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import eu.domibus.connector.dss.configuration.SignatureValidationConfigurationProperties;
import eu.domibus.connector.dss.service.DSSTrustedListsManager;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SignatureValidationConfigurationField extends CustomField<SignatureValidationConfigurationProperties> implements AfterNavigationObserver {
    private final DSSTrustedListsManager trustedListsManager;
    private final SpringBeanValidationBinderFactory validationBinderFactory;

    private SpringBeanValidationBinder<SignatureValidationConfigurationProperties> binder;

    private final TextField validationConstraintsXml = new TextField();
    private final Checkbox trustStoreEnabled = new Checkbox();
    private Select<String> trustedListSource;
    private final Checkbox ocspEnabled = new Checkbox();
    private final Checkbox crlEnabled = new Checkbox();
    private final Checkbox aiaEnabled = new Checkbox();
    private final StoreConfigurationField trustStore;
    private final StoreConfigurationField ignoreStore;
    private final Checkbox ignoreStoreEnabled = new Checkbox();

    private final FormLayout formLayout = new FormLayout();
    private SignatureValidationConfigurationProperties value = new SignatureValidationConfigurationProperties();

    public SignatureValidationConfigurationField(
            DSSTrustedListsManager trustedListsManager,
            StoreConfigurationField trustStore,
            StoreConfigurationField ignoreStore,
            SpringBeanValidationBinderFactory validationBinderFactory) {
        this.trustedListsManager = trustedListsManager;
        this.validationBinderFactory = validationBinderFactory;
        this.trustStore = trustStore;
        this.ignoreStore = ignoreStore;
        initUI();
    }

    private void initUI() {
        Label statusLabel = new Label();
        statusLabel.getStyle().set("color", "red");

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep(
                "15cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
        ));

        formLayout.addFormItem(validationConstraintsXml, "Location of EtsiValidationPolicyXml");

        // TODO: do not show store, if not enabled
        // trust store
        formLayout.addFormItem(trustStoreEnabled, "Use TrustStore");
        formLayout.addFormItem(trustStore, "Trust Store Configuration");

        // TODO: do not show store, if not enabled
        // ignore Store
        formLayout.addFormItem(ignoreStoreEnabled, "Use Ignore Store");
        formLayout.addFormItem(ignoreStore, "Ignore Store Configuration");

        // Trusted Lists Source
        trustedListSource = new Select<String>();
        trustedListSource.setEmptySelectionAllowed(true);
        trustedListSource.setDataProvider(new CallbackDataProvider<String, String>(
                this::fetchTrustedLists,
                this::countTrustedLists
        ));
        formLayout.addFormItem(trustedListSource, "Set the trusted list source");

        formLayout.addFormItem(ocspEnabled, "Should OCSP be used on certificate verification");
        formLayout.addFormItem(crlEnabled, "Should CRL be used on certificate verification");
        formLayout.addFormItem(aiaEnabled, "Should AIA be used");

        this.add(statusLabel);
        this.add(formLayout);

        binder = validationBinderFactory.create(SignatureValidationConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    public void setTruststoreReadOnly(boolean readOnly) {
        this.trustStore.setReadOnly(readOnly);
        this.trustStoreEnabled.setReadOnly(readOnly);
    }

    private int countTrustedLists(Query<String, String> query) {
        return trustedListsManager.getAllSourceNames().size();
    }

    private Stream<String> fetchTrustedLists(Query<String, String> query) {
        return trustedListsManager.getAllSourceNames().stream();
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        SignatureValidationConfigurationProperties changedValue = new SignatureValidationConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected SignatureValidationConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(SignatureValidationConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        formLayout.setVisible(newPresentationValue != null);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // init trusted list source items on enter event
        // this.trustedListSource.setItems(trustedListsManager.getAllSourceNames());
    }
}

