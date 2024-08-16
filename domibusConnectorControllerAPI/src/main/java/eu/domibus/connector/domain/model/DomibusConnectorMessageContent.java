/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.model;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

/**
 * The DomibusConnectorMessageContent holds the main content of a message. This is the XML data of
 * the main Form of the message and the printable document that most of the
 * {@link DomibusConnectorAction} require.
 *
 * <p>A message is a business message only if a messageContent is present
 *
 * @author riederb
 * @version 1.0 updated 29-Dez-2017 10:12:49
 */
@Data
@NoArgsConstructor
@SuppressWarnings("squid:S1135")
public class DomibusConnectorMessageContent implements Serializable {
    private byte[] xmlContent;
    // TODO: this should also be a LargeFileReference so it is also processed by content deletion!
    @Nullable
    private DomibusConnectorMessageDocument document;

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("document", this.document);
        return builder.toString();
    }
}
