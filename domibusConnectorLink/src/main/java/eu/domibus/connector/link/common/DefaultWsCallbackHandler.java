/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.link.common;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class is the default callback handler for web services.
 * It implements the CallbackHandler interface and is responsible for handling the callbacks.
 */
@Component("defaultCallbackHandler")
public class DefaultWsCallbackHandler implements CallbackHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWsCallbackHandler.class);

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        LOGGER.trace("default callback handler called with callbacks [{}]", (Object[]) callbacks);
        // just do nothing...
    }
}
