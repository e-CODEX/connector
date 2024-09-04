/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl.helper;

import eu.ecodex.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageContent;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Represents the different types of data that can be stored in the Domibus system. Each store type
 * is associated with a corresponding database string and domain class.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Getter
public enum StoreType {
    MESSAGE_CONTENT("domibus_message_content", DomibusConnectorMessageContent.class),
    MESSAGE_BUSINESSS_CONTENT_XML("dcm_business_xml", Object.class),
    MESSAGE_BUSINESS_CONTENT_DOCUMENT("dcm_business_document", Object.class),
    MESSAGE_ATTACHMENT("domibus_message_attachment", DomibusConnectorMessageAttachment.class),
    MESSAGE_ATTACHMENT_CONTENT("dcm_attachment", Object.class),
    MESSAGE_CONFIRMATION_XML("dcm_confirmation_xml", Object.class),
    MESSAGE_CONFIRMATION("domibus_message_confirmation", DomibusConnectorMessageConfirmation.class);
    private final String dbString;
    private final Class domainClazz;

    StoreType(String dbString, Class domainClazz) {
        this.dbString = dbString;
        this.domainClazz = domainClazz;
    }

    /**
     * Retrieves the StoreType enum value based on the provided database string.
     *
     * @param dbData The database string representing the StoreType.
     * @return The StoreType enum value corresponding to the provided database string.
     * @throws NoSuchElementException If the provided database string does not match any StoreType.
     * @throws NullPointerException   If the provided database string is null.
     */
    public static StoreType fromDbName(String dbData) throws NoSuchElementException {
        return Stream.of(StoreType.values())
                     .filter(s -> s.getDbString().equals(dbData))
                     .findFirst()
                     .get();
    }
}
