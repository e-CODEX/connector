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
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuthenticationValidationConfigurationField extends CustomField<DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties>
 {

    private final SpringBeanValidationBinderFactory validationBinderFactory;

    private SpringBeanValidationBinder<DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties> binder;

    private Label statusLabel = new Label();

    private TextField identityProvider = new TextField();

    private FormLayout formLayout = new FormLayout();
    private DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties value =
            new DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties();

    public AuthenticationValidationConfigurationField(SpringBeanValidationBinderFactory validationBinderFactory) {
        this.validationBinderFactory = validationBinderFactory;
        initUI();
    }

    private void initUI() {
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("15cm", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        formLayout.addFormItem(identityProvider, "Authenticated Identity Provider");

        this.add(statusLabel);
        this.add(formLayout);

        binder = validationBinderFactory.create(DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }


    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {

        DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties changedValue
                = new DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;

    }


    @Override
    protected void setPresentationValue(DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }


    @Override
    protected DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties generateModelValue() {
        return value;
    }


}

