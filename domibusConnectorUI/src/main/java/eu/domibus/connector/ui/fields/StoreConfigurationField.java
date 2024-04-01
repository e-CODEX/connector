package eu.domibus.connector.ui.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class StoreConfigurationField extends CustomField<StoreConfigurationProperties> {
    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();

    private final TextField path = new TextField();
    private final PasswordField password = new PasswordField();
    private final Select<String> type = new Select();

    private final Binder<StoreConfigurationProperties> binder;
    private StoreConfigurationProperties value;

    public StoreConfigurationField(SpringBeanValidationBinderFactory validationBinderFactory) {

        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
        ));

        // TODO: add button to show content of key/truststore
        formLayout.addFormItem(path, "Store Location");
        formLayout.addFormItem(password, "Store password");
        formLayout.addFormItem(type, "Store Type");
        type.setItems("JKS", "JCEKS", "PKCS12");

        binder = validationBinderFactory.create(StoreConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);
        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        path.setReadOnly(readOnly);
        password.setReadOnly(readOnly);
        type.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        StoreConfigurationProperties changedValue = new StoreConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected StoreConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(StoreConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        formLayout.setVisible(newPresentationValue != null);
    }
}
