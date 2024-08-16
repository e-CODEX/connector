/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.evidences;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

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

/**
 * The {@code EvidencesIssuerInfoField} class is a custom Vaadin field component that allows editing
 * and displaying information about an issuer of evidences.
 *
 * @see CustomField
 * @see EvidencesIssuerInfo
 * @see SpringBeanValidationBinderFactory
 * @see PostalAdressConfigurationPropertiesField
 * @see HomePartyConfigurationPropertiesField
 */
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

    /**
     * Constructor.
     *
     * @param validationBinderFactory The validation binder factory used for creating validation
     *                                binders.
     * @param postalAddress           The postal address configuration properties field.
     * @param as4party                The AS4 party configuration properties field.
     */
    public EvidencesIssuerInfoField(
        SpringBeanValidationBinderFactory validationBinderFactory,
        PostalAdressConfigurationPropertiesField postalAddress,
        HomePartyConfigurationPropertiesField as4party) {
        this.validationBinderFactory = validationBinderFactory;
        this.postalAddress = postalAddress;
        this.as4party = as4party;

        this.add(statusLabel);
        this.add(formLayout);

        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep(
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
        var changedValue = new EvidencesIssuerInfo();
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
        if (newPresentationValue == null) {
            formLayout.setVisible(false);
        } else {
            formLayout.setVisible(true);
        }
    }
}
