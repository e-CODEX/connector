package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DomibusConnectorParty.PartyRoleType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PartyRoleTypeJpaConverter implements AttributeConverter<PartyRoleType, String> {


    @Override
    public String convertToDatabaseColumn(PartyRoleType attribute) {
        return attribute.getDbName();
    }


    @Override
    public PartyRoleType convertToEntityAttribute(String dbData) {
        return PartyRoleType.ofDbName(dbData);
    }


}
