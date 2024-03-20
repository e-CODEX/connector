package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.persistence.service.impl.helper.StoreType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StoreTypeJpaConverter implements AttributeConverter<StoreType, String> {


    @Override
    public String convertToDatabaseColumn(StoreType attribute) {
        return attribute.getDbString();
    }


    @Override
    public StoreType convertToEntityAttribute(String dbData) {
        return StoreType.fromDbName(dbData);
    }


}
