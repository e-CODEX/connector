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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/DocumentStreamUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import eu.europa.esig.dss.model.DSSDocument;
import java.io.InputStream;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

/**
 * provides convenience-methods for documents. e.g. getting the byte-data from the stream
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@UtilityClass
public class DocumentStreamUtil {
    /**
     * checks whether the document provides data; read: at least 1 byte. note that exceptions are
     * swallowed in order to determine only the final result.
     *
     * @param document the value
     * @return the result
     */
    public static boolean hasData(final DSSDocument document) {
        InputStream in = null;
        try {
            in = document.openStream();
            return in.available() > 0;
        } catch (final RuntimeException e) {
            // swallow
            return false;
        } catch (final Exception e) {
            // swallow
            return false;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * fetches the complete byte array of the document's content. note that - in contrast to
     * {@link #hasData(DSSDocument)} - exceptions are rethrown (but always as runtimeexception)
     *
     * @param document the value
     * @return the result
     */
    public static byte[] getData(final DSSDocument document) {
        InputStream in = null;
        try {
            in = document.openStream();
            return IOUtils.toByteArray(in);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
