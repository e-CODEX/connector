/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.security;

import eu.ecodex.connector.ui.fields.SignatureConfigurationField;
import eu.ecodex.connector.ui.fields.SignatureValidationConfigurationField;
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
