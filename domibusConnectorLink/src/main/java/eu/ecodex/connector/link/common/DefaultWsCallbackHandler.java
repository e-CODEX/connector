/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.common;

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
