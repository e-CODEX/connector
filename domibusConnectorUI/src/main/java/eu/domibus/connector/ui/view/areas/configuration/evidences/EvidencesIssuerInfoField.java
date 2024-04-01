package eu.domibus.connector.ui.view.areas.configuration.evidences;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.evidences.spring.EvidencesIssuerInfo;
import eu.domibus.connector.ui.fields.HomePartyConfigurationPropertiesField;
import eu.domibus.connector.ui.fields.PostalAdressConfigurationPropertiesField;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;


@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class EvidencesIssuerInfoField extends CustomField<EvidencesIssuerInfo> {
    private final SpringBeanValidationBinderFactory validationBinderFactory;
    private final PostalAdressConfigurationPropertiesField postalAddress;
    private final HomePartyConfigurationPropertiesField as4party;
    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();

    private final SpringBeanValidationBinder<EvidencesIssuerInfo> binder;

    EvidencesIssuerInfo value;

    public EvidencesIssuerInfoField(
            SpringBeanValidationBinderFactory validationBinderFactory,
            PostalAdressConfigurationPropertiesField postalAddress,
            HomePartyConfigurationPropertiesField as4party) {
        this.validationBinderFactory = validationBinderFactory;
        this.postalAddress = postalAddress;
        this.as4party = as4party;

        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep(
                "30cm",
                1,
                FormLayout.ResponsiveStep.LabelsPosition.ASIDE
        ));
        formLayout.addFormItem(postalAddress, "postal address");
        formLayout.addFormItem(this.as4party, "AS4 Party Info");

        binder = validationBinderFactory.create(EvidencesIssuerInfo.class);
        binder.bindInstanceFields(this);
        binder.addValueChangeListener(this::valueChanged);

        binder.setStatusLabel(statusLabel);
    }

    @Override
    public void setReadOnly(boolean readOnly) {

        binder.setReadOnly(readOnly);
    }

    private void valueChanged(ValueChangeEvent<?> valueChangeEvent) {
        EvidencesIssuerInfo changedValue = new EvidencesIssuerInfo();
        binder.writeBeanAsDraft(changedValue, true);
        setModelValue(changedValue, valueChangeEvent.isFromClient());
        value = changedValue;
    }

    @Override
    protected EvidencesIssuerInfo generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(EvidencesIssuerInfo newPresentationValue) {
        binder.readBean(newPresentationValue);
        formLayout.setVisible(newPresentationValue != null);
    }
}
