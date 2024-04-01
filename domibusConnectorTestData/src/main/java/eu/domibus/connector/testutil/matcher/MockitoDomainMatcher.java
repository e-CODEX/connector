package eu.domibus.connector.testutil.matcher;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import org.mockito.ArgumentMatcher;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class MockitoDomainMatcher {
    public static ArgumentMatcher<DomibusConnectorMessage> eqToRefToMessageId(String refToMessageId) {
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
