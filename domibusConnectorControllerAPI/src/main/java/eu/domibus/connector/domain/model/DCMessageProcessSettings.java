package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;


public class DCMessageProcessSettings {
    private AdvancedElectronicSystemType validationServiceName;

    public AdvancedElectronicSystemType getValidationServiceName() {
        return validationServiceName;
    }

    public void setValidationServiceName(AdvancedElectronicSystemType validationServiceName) {
        this.validationServiceName = validationServiceName;
    }
}
