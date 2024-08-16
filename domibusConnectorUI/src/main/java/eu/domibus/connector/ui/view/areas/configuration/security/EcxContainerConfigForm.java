/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.security;

import eu.domibus.connector.ui.fields.SignatureConfigurationField;
import eu.domibus.connector.ui.fields.SignatureValidationConfigurationField;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * EcxContainerConfigForm is a custom form layout component that extends
 * com.vaadin.flow.component.formlayout.FormLayout. It is used to display and edit the configuration
 * options for a container in the ECX system.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EcxContainerConfigForm extends com.vaadin.flow.component.formlayout.FormLayout {
    // is bound by binder
    private final SignatureValidationConfigurationField signatureValidation;
    private final SignatureConfigurationField signature;

    /**
     * Constructor.
     *
     * @param signatureValidation The SignatureValidationConfigurationField object representing the
     *                            signature validation configuration.
     * @param signature           The SignatureConfigurationField object representing the signature
     *                            configuration.
     */
    public EcxContainerConfigForm(
        SignatureValidationConfigurationField signatureValidation,
        SignatureConfigurationField signature) {
        this.signatureValidation = signatureValidation;
        this.signature = signature;

        this.setResponsiveSteps(new ResponsiveStep("15cm", 1, ResponsiveStep.LabelsPosition.ASIDE));
        signatureValidation.setTruststoreReadOnly(true);
        this.signatureValidation.setTruststoreReadOnly(true);
        addFormItem(signatureValidation, "Signature Validation Config");
        addFormItem(signature, "Signature Configuration");
    }
}
