/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/ECodexException.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service;

import lombok.NoArgsConstructor;

/**
 * this is the only type of exceptions that the service methods should expose. so, they should catch
 * all exceptions deemed as "library-scoped" and wrap them accordingly.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@NoArgsConstructor
public class ECodexException extends Exception {
    /**
     * See {@link RuntimeException#RuntimeException(String)}.
     *
     * @param message textual information
     */
    public ECodexException(final String message) {
        super(message);
    }

    /**
     * See {@link RuntimeException#RuntimeException(Throwable)}.
     *
     * @param cause the parent cause
     */
    public ECodexException(final Throwable cause) {
        super(cause);
    }

    /**
     * See {@link RuntimeException#RuntimeException(String, Throwable)}.
     *
     * @param message textual information
     * @param cause   the parent cause
     */
    public ECodexException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Wraps a non-ecodexexception.
     *
     * @param e the caught exception
     * @return the wrapped (or not-wrapped) exception
     */
    public static ECodexException wrap(final Exception e) {
        if (e instanceof ECodexException exception) {
            return exception;
        } else {
            return new ECodexException(e);
        }
    }
}
