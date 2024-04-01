package eu.domibus.connector.ui.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component(KeyAndKeyStoreAndTrustStoreConfigurationField.BEAN_NAME)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KeyAndKeyStoreAndTrustStoreConfigurationField extends CustomField<KeyAndKeyStoreAndTrustStoreConfigurationProperties> {
    public static final String BEAN_NAME = "KeyAndKeyStoreAndTrustStoreConfigurationField";

    private final SpringBeanValidationBinderFactory validationBinderFactory;
    private final StoreConfigurationField trustStore;
    private final StoreConfigurationField keyStore;
    private final KeyConfigurationField privateKey;
    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();
    private final SpringBeanValidationBinder<KeyAndKeyStoreAndTrustStoreConfigurationProperties> binder;
    KeyAndKeyStoreAndTrustStoreConfigurationProperties value;

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

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
        ));
        formLayout.addFormItem(this.trustStore, "Trust Store");
        formLayout.addFormItem(this.keyStore, "Key Store");
        formLayout.addFormItem(this.privateKey, "Private Key");

        //        formLayout.addFormItem(password, "Key Password");
        // TODO: add alternative view, when associated with keystore...some kind of select box...

        binder = validationBinderFactory.create(KeyAndKeyStoreAndTrustStoreConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        KeyAndKeyStoreAndTrustStoreConfigurationProperties changedValue =
                new KeyAndKeyStoreAndTrustStoreConfigurationProperties();
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
    protected void setPresentationValue(KeyAndKeyStoreAndTrustStoreConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        formLayout.setVisible(newPresentationValue != null);
    }
}
