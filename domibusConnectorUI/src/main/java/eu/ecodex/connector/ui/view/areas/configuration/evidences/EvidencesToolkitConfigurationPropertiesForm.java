/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.evidences;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.fields.SignatureConfigurationField;

/**
 * This class represents a form used for configuring Evidences Toolkit properties.
 *
 * @see FormLayout
 * @see SignatureConfigurationField
 * @see EvidencesIssuerInfoField
 */
@SpringComponent
@UIScope
public class EvidencesToolkitConfigurationPropertiesForm extends FormLayout {
    private final SignatureConfigurationField signature;
    private final EvidencesIssuerInfoField issuerInfo;

    /**
     * Constructor.
     *
     * @see FormLayout
     * @see SignatureConfigurationField
     * @see EvidencesIssuerInfoField
     */
    public EvidencesToolkitConfigurationPropertiesForm(
        SignatureConfigurationField signature, EvidencesIssuerInfoField issuerInfo) {
        this.signature = signature;
        this.issuerInfo = issuerInfo;

        this.setResponsiveSteps(new ResponsiveStep("30cm", 1, ResponsiveStep.LabelsPosition.ASIDE));
        this.addFormItem(signature, "Signature");
        this.addFormItem(this.issuerInfo, "Issuer");
    }
}
