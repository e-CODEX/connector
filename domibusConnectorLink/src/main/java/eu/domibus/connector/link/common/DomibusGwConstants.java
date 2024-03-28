package eu.domibus.connector.link.common;

import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class DomibusGwConstants {
    public static final Set<String> EVIDENCE_TYPE_NAMES =
            (Arrays.stream(DomibusConnectorConfirmationType.values()).map(t -> t.value()).collect(Collectors.toSet()));
    public static final String ASIC_S_NAME = "ASIC-S";
    public static final String ASIC_S_DESCRIPTION_NAME = "ASIC-S";
    public static final String TOKEN_XML_NAME = "tokenXML";
    public static final String TOKEN_XML_DESCRIPTION_NAME = "tokenXML";
    public static final String MESSAGE_CONTENT_DESCRIPTION_NAME = "messageContent";
    public static final String ASIC_S_MIMETYPE = "application/vnd.etsi.asic-s+zip";
}
