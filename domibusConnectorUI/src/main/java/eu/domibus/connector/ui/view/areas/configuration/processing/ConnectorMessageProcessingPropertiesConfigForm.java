/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.processing;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.controller.spring.ConnectorMessageProcessingProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This class is a form used for configuring the message processing properties of the connector. It
 * extends the FormLayout class.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectorMessageProcessingPropertiesConfigForm extends FormLayout {
    private final Checkbox sendGeneratedEvidencesToBackend = new Checkbox();
    private final Checkbox ebmsIdGeneratorEnabled = new Checkbox();
    private final TextField ebmsIdSuffix = new TextField();
    private final Select<ConnectorMessageProcessingProperties.PModeVerificationMode>
        outgoingPModeVerificationMode;
    private final Select<ConnectorMessageProcessingProperties.PModeVerificationMode>
        incomingPModeVerificationMode;

    /**
     * Constructor.
     */
    public ConnectorMessageProcessingPropertiesConfigForm() {
        outgoingPModeVerificationMode = new Select<>();
        outgoingPModeVerificationMode.setItems(
            ConnectorMessageProcessingProperties.PModeVerificationMode.values());
        incomingPModeVerificationMode = new Select<>();
        incomingPModeVerificationMode.setItems(
            ConnectorMessageProcessingProperties.PModeVerificationMode.values());

        this.setResponsiveSteps(new FormLayout.ResponsiveStep(
            "30cm",
            1,
            FormLayout.ResponsiveStep.LabelsPosition.ASIDE
        ));

        addFormItem(sendGeneratedEvidencesToBackend, "Send Generated Evidences Back to Backend");
        addFormItem(ebmsIdGeneratorEnabled, "Should connector create EBMS id");
        addFormItem(ebmsIdSuffix, "EBMS id suffix: UUID@<ebmsIdSuffix>");
        addFormItem(
            outgoingPModeVerificationMode, "PMode verification mode for outgoing business msg");
        addFormItem(
            incomingPModeVerificationMode, "PMode verification mode for incoming business msg");
    }
}
