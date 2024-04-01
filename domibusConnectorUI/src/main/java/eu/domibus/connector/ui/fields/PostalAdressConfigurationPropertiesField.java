package eu.domibus.connector.ui.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.evidences.spring.PostalAdressConfigurationProperties;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;


@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class PostalAdressConfigurationPropertiesField extends CustomField<PostalAdressConfigurationProperties> {
    private final SpringBeanValidationBinderFactory validationBinderFactory;
    private final TextField street = new TextField();
    private final TextField locality = new TextField();
    private final TextField zipCode = new TextField();
    private final TextField country = new TextField();

    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();

    private final SpringBeanValidationBinder<PostalAdressConfigurationProperties> binder;
    private PostalAdressConfigurationProperties value;

    public PostalAdressConfigurationPropertiesField(SpringBeanValidationBinderFactory validationBinderFactory) {
        this.validationBinderFactory = validationBinderFactory;

        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep(
                "5cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
        ));
        formLayout.addFormItem(street, "Street");
        formLayout.addFormItem(locality, "Locality");
        formLayout.addFormItem(zipCode, "ZipCode");
        formLayout.addFormItem(country, "Country (2 Letter Code)");

        binder = validationBinderFactory.create(PostalAdressConfigurationProperties.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        street.setReadOnly(readOnly);
        locality.setReadOnly(readOnly);
        zipCode.setReadOnly(readOnly);
        country.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        PostalAdressConfigurationProperties changedValue = new PostalAdressConfigurationProperties();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected PostalAdressConfigurationProperties generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(PostalAdressConfigurationProperties newPresentationValue) {
        binder.readBean(newPresentationValue);
        formLayout.setVisible(newPresentationValue != null);
    }
}
