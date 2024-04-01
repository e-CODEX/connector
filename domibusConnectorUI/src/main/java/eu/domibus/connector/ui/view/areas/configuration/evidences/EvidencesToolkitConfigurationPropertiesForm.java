package eu.domibus.connector.ui.view.areas.configuration.evidences;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.fields.SignatureConfigurationField;


@SpringComponent
@UIScope
public class EvidencesToolkitConfigurationPropertiesForm extends FormLayout {
    private final SignatureConfigurationField signature;
    private final EvidencesIssuerInfoField issuerInfo;

    public EvidencesToolkitConfigurationPropertiesForm(
            SignatureConfigurationField signature,
            EvidencesIssuerInfoField issuerInfo) {
        this.signature = signature;
        this.issuerInfo = issuerInfo;

        this.setResponsiveSteps(new ResponsiveStep("30cm", 1, ResponsiveStep.LabelsPosition.ASIDE));
        this.addFormItem(signature, "Signature");
        this.addFormItem(this.issuerInfo, "Issuer");
    }
}
