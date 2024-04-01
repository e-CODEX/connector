package eu.domibus.connector.ui.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.evidences.spring.HomePartyConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;


@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class HomePartyConfigurationPropertiesField extends CustomField<HomePartyConfigurationProperties> {
    private final SpringBeanValidationBinderFactory validationBinderFactory;

    private final TextField name = new TextField();
    private final TextField endpointAddress = new TextField();

    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();

    private final SpringBeanValidationBinder<HomePartyConfigurationProperties> binder;
    private HomePartyConfigurationProperties value;

    public HomePartyConfigurationPropertiesField(SpringBeanValidationBinderFactory validationBinderFactory) {
        this.validationBinderFactory = validationBinderFactory;

        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
        ));
        formLayout.addFormItem(name, "Party Name");
        formLayout.addFormItem(endpointAddress, "Party Endpoint Address");

        binder = validationBinderFactory.create(HomePartyConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        HomePartyConfigurationProperties changedValue = new HomePartyConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected HomePartyConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(HomePartyConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        formLayout.setVisible(newPresentationValue != null);
    }
}
