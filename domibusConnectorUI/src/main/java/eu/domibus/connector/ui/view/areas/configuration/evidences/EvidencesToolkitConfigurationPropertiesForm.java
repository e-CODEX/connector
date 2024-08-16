/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.evidences;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.fields.SignatureConfigurationField;

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
