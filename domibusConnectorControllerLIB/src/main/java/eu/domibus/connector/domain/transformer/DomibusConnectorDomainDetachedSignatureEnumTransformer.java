package eu.domibus.connector.domain.transformer;

import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms DetachedSignature enums
 * between domain and transition model 
 * is necessary because PKCS7 has a different name
 * in transition model (PKCS_7)
 * 
 */
public class DomibusConnectorDomainDetachedSignatureEnumTransformer {

    private final static Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorDomainDetachedSignatureEnumTransformer.class);
    
    public static DomibusConnectorDetachedSignatureMimeType transformDetachedSignatureMimeTypeDomainToTransition(DetachedSignatureMimeType domainMimeType) {
        if (DetachedSignatureMimeType.PKCS7 == domainMimeType) {
            return DomibusConnectorDetachedSignatureMimeType.PKCS_7;
        }
        return DomibusConnectorDetachedSignatureMimeType.valueOf(domainMimeType.name()); 
    }
    
    public static DetachedSignatureMimeType transformDetachedSignatureMimeTypeTransitionToDomain(DomibusConnectorDetachedSignatureMimeType mimeTypeTO) {
        if (DomibusConnectorDetachedSignatureMimeType.PKCS_7 == mimeTypeTO) {
            return DetachedSignatureMimeType.PKCS7;
        }
        return DetachedSignatureMimeType.valueOf(mimeTypeTO.name());
    }
    
    
}