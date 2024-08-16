/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
