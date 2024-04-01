package eu.domibus.connector.ui.view.areas.configuration.security;

import eu.domibus.connector.ui.fields.SignatureConfigurationField;
import eu.domibus.connector.ui.fields.SignatureValidationConfigurationField;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EcxContainerConfigForm extends com.vaadin.flow.component.formlayout.FormLayout {
    // is bound by binder
    private final SignatureValidationConfigurationField signatureValidation;
    private final SignatureConfigurationField signature;

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
