package eu.domibus.connector.ui.view.areas.configuration.processing;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.controller.spring.ConnectorMessageProcessingProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectorMessageProcessingPropertiesConfigForm extends FormLayout {

    private final Checkbox sendGeneratedEvidencesToBackend = new Checkbox();
    private final Checkbox ebmsIdGeneratorEnabled = new Checkbox();
    private final TextField ebmsIdSuffix = new TextField();
    private Select<ConnectorMessageProcessingProperties.PModeVerificationMode> outgoingPModeVerificationMode;
    private Select<ConnectorMessageProcessingProperties.PModeVerificationMode> incomingPModeVerificationMode;

    public ConnectorMessageProcessingPropertiesConfigForm() {

        outgoingPModeVerificationMode = new Select<>(ConnectorMessageProcessingProperties.PModeVerificationMode.values());
        incomingPModeVerificationMode = new Select<>(ConnectorMessageProcessingProperties.PModeVerificationMode.values());

        this.setResponsiveSteps(new FormLayout.ResponsiveStep("30cm", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        addFormItem(sendGeneratedEvidencesToBackend, "Send Generated Evidences Back to Backend");
        addFormItem(ebmsIdGeneratorEnabled, "Should connector create EBMS id");
        addFormItem(ebmsIdSuffix, "EBMS id suffix: UUID@<ebmsIdSuffix>");
        addFormItem(outgoingPModeVerificationMode, "PMode verification mode for outgoing business msg");
        addFormItem(incomingPModeVerificationMode, "PMode verification mode for incoming business msg");

    }


}
