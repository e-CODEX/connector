/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.security;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import eu.domibus.connector.ui.fields.AuthenticationValidationConfigurationField;
import eu.domibus.connector.ui.fields.SignatureValidationConfigurationField;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A form for configuring business document validation.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BusinessDocumentValidationConfigForm extends FormLayout {
    private final TextField country = new TextField();
    private final TextField serviceProvider = new TextField();
    private final Select<AdvancedElectronicSystemType> defaultAdvancedSystemType = new Select<>();
    private final Checkbox allowSystemTypeOverrideByClient = new Checkbox();
    private final MultiSelectListBox<AdvancedElectronicSystemType> allowedAdvancedSystemTypes;
    // field must be a member to use bind instance fields!
    @SuppressWarnings("FieldCanBeLocal")
    private final SignatureValidationConfigurationField signatureValidation;
    private final AuthenticationValidationConfigurationField authenticationValidation;

    /**
     * Constructor.
     *
     * @param signatureValidation      The SignatureValidationConfigurationField instance to be used
     *                                 for signature validation configuration.
     * @param authenticationValidation The AuthenticationValidationConfigurationField instance to be
     *                                 used for authentication validation configuration.
     */
    public BusinessDocumentValidationConfigForm(
        SignatureValidationConfigurationField signatureValidation,
        AuthenticationValidationConfigurationField authenticationValidation) {
        allowedAdvancedSystemTypes = new MultiSelectListBox<>();
        allowedAdvancedSystemTypes.setItems(
            Stream.of(AdvancedElectronicSystemType.values()).collect(Collectors.toSet()));
        allowedAdvancedSystemTypes.setWidth("100%");

        defaultAdvancedSystemType.setItems(AdvancedElectronicSystemType.values());

        this.authenticationValidation = authenticationValidation;
        this.signatureValidation = signatureValidation;
        this.setResponsiveSteps(new ResponsiveStep("30cm", 1, ResponsiveStep.LabelsPosition.ASIDE));

        addFormItem(country, "Country");
        addFormItem(serviceProvider, "Service Provider");
        addFormItem(allowedAdvancedSystemTypes, "Allowed AdvancedSystemTypes");
        addFormItem(defaultAdvancedSystemType, "Default AdvancedSystemType");
        addFormItem(
            allowSystemTypeOverrideByClient, "Allow client to set AdvancedSystemType on message");
        addFormItem(this.authenticationValidation, "Authentication Based Config");
        addFormItem(this.signatureValidation, "Signature Validation Config");
    }
}
