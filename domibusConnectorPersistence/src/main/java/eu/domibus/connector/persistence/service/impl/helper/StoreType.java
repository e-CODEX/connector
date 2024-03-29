package eu.domibus.connector.persistence.service.impl.helper;

import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;

import java.util.NoSuchElementException;
import java.util.stream.Stream;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
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
     * @param dbData - the dbName of the StoreType
     * @return - the found store type
     * @throws NoSuchElementException if there is no StoreType with dbData present
     */
    public static StoreType fromDbName(String dbData) throws NoSuchElementException {
        return Stream.of(StoreType.values()).filter(s -> s.getDbString().equals(dbData)).findFirst().get();
    }

    public Class getDomainClazz() {
        return domainClazz;
    }

    public String getDbString() {
        return dbString;
    }
}
