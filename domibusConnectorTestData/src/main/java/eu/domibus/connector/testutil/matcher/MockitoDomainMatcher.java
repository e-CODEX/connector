/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.testutil.matcher;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import lombok.experimental.UtilityClass;
import org.mockito.ArgumentMatcher;

/**
 * The `MockitoDomainMatcher` class provides utility methods for creating argument matchers for
 * Mockito that match on specific properties of a `DomibusConnectorMessage` object.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@UtilityClass
public class MockitoDomainMatcher {
    public static ArgumentMatcher<DomibusConnectorMessage> eqToRefToMessageId(
        String refToMessageId) {
        return new RefToMessageIdMatcher(refToMessageId);
    }

    private static class RefToMessageIdMatcher implements ArgumentMatcher<DomibusConnectorMessage> {
        private final String messageReference;

        public RefToMessageIdMatcher(String messageReference) {
            if (messageReference == null) {
                throw new IllegalArgumentException("Message Reference cannot be null!");
            }
            this.messageReference = messageReference;
        }

        @Override
        public boolean matches(DomibusConnectorMessage message) {
            if (message == null) {
                return false;
            }
            return messageReference.equals(message.getMessageDetails().getRefToMessageId());
        }
    }
}
