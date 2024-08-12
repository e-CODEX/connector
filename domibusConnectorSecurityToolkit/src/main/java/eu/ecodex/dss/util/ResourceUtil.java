/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/test/java/eu/ecodex/dss/ResourceUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

/**
 * Utility class to store a document.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@UtilityClass
public class ResourceUtil {
    /**
     * Retrieves a URL for the specified resource name.
     *
     * @param name the name of the resource for which to retrieve the URL
     * @return the URL representing the resource, or null if the resource cannot be found or an I/O
     *      error occurs
     */
    public static URL getURL(final String name) {
        InputStream stream = streamFromClass(name);
        IOUtils.closeQuietly(stream);
        if (stream != null) {
            return urlFromClass(name);
        }
        stream = streamFromSystem(name);
        IOUtils.closeQuietly(stream);
        return urlFromSystem(name);
    }

    private static URL urlFromSystem(final String name) {
        return ClassLoader.getSystemClassLoader().getResource(name);
    }

    private static URL urlFromClass(final String name) {
        return ResourceUtil.class.getResource(name);
    }

    /**
     * Returns the byte array representation of the data stored in the specified resource.
     *
     * @param name the name of the resource
     * @return the byte array representation of the resource data, or null if the resource cannot be
     *      found or an I/O error occurs
     */
    public static byte[] getBytes(final String name) {
        var stream = streamFromClass(name);
        if (stream == null) {
            stream = streamFromSystem(name);
        }
        if (stream != null) {
            try {
                return IOUtils.toByteArray(stream);
            } catch (IOException ex) {
                return new byte[0];
            } finally {
                IOUtils.closeQuietly(stream);
            }
        }
        return new byte[0];
    }

    /**
     * Retrieves an InputStream for the specified resource name.
     *
     * @param name the name of the resource to retrieve
     * @return the InputStream of the resource if found, otherwise null
     */
    public static InputStream getStream(final String name) {
        InputStream stream = streamFromClass(name);
        if (stream == null) {
            stream = streamFromSystem(name);
        }
        return stream;
    }

    private static InputStream streamFromSystem(final String name) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(name);
    }

    private static InputStream streamFromClass(final String name) {
        return ResourceUtil.class.getResourceAsStream(name);
    }
}
