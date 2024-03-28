package eu.domibus.connector.link.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;


@Component("defaultCallbackHandler")
public class DefaultWsCallbackHandler implements CallbackHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWsCallbackHandler.class);

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        LOGGER.trace("default callback handler called with callbacks [{}]", (Object[]) callbacks);
        // just do nothing...
    }
}
