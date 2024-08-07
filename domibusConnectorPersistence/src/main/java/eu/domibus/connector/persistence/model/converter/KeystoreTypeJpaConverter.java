package eu.domibus.connector.persistence.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore.KeystoreType;

@Converter(autoApply = true)
public class KeystoreTypeJpaConverter implements AttributeConverter<KeystoreType, String> {


    @Override
    public String convertToDatabaseColumn(KeystoreType attribute) {
        return attribute.getDbName();
    }


    @Override
    public KeystoreType convertToEntityAttribute(String dbData) {
        return KeystoreType.ofDbName(dbData);
    }


}
