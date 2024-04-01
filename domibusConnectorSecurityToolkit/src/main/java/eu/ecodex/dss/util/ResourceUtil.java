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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Utility class to store a document
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class ResourceUtil {
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

    public static byte[] getBytes(final String name) {
        InputStream stream = streamFromClass(name);
        if (stream == null) {
            stream = streamFromSystem(name);
        }
        if (stream != null) {
            try {
                final byte[] bytes = IOUtils.toByteArray(stream);
                return bytes;
            } catch (IOException ex) {
                return null;
            } finally {
                IOUtils.closeQuietly(stream);
            }
        }
        return null;
    }

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
