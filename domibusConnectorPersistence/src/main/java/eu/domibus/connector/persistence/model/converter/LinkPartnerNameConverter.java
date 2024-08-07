package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LinkPartnerNameConverter implements AttributeConverter<DomibusConnectorLinkPartner.LinkPartnerName, String> {

    @Override
    public String convertToDatabaseColumn(DomibusConnectorLinkPartner.LinkPartnerName attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getLinkName();
    }

    @Override
    public DomibusConnectorLinkPartner.LinkPartnerName convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new DomibusConnectorLinkPartner.LinkPartnerName(dbData);
    }
}
