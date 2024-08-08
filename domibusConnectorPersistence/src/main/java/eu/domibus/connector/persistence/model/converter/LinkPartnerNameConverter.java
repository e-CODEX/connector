/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The LinkPartnerNameConverter class is a JPA attribute converter that converts between the
 * DomibusConnectorLinkPartner.LinkPartnerName object and a String representation when persisting to
 * the database.
 *
 * <p>The converter is automatically applied to the LinkPartnerName attribute of the
 * DomibusConnectorLinkPartner class.
 */
@Converter(autoApply = true)
public class LinkPartnerNameConverter
    implements AttributeConverter<DomibusConnectorLinkPartner.LinkPartnerName, String> {
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
