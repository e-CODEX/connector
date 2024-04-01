/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/ZipStreamUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Provides convenience-methods for ZIP documents.
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class ZipStreamUtil {
    /**
     * utility constructor
     */
    private ZipStreamUtil() {
    }

    /**
     * Extracts the documents contained in the zip document.
     *
     * @param zipDocument the zip document
     * @return the result
     * @throws java.io.IOException The exception
     */
    public static List<DSSDocument> extract(final DSSDocument zipDocument) throws IOException {
        if (!isZipFile(zipDocument)) {
            throw new IllegalArgumentException("The document in parameter is not in zip format!");
        }

        final List<DSSDocument> documents = new ArrayList<DSSDocument>();

        InputStream parentStream = null;
        ZipInputStream zipStream = null;
        try {
            parentStream = zipDocument.openStream();
            zipStream = new ZipInputStream(parentStream);

            while (true) {
                final ZipEntry entry = zipStream.getNextEntry();
                if (entry == null) {
                    break;
                }

                if (StringUtils.isEmpty(entry.getName())) {
                    throw new IllegalArgumentException(
                            "The zip document in parameter contains an entry with an empty name.");
                }

                final InMemoryDocument doc = new InMemoryDocument(IOUtils.toByteArray(zipStream), entry.getName());
                documents.add(doc);
                zipStream.closeEntry();
            }
            return documents;
        } finally {
            IOUtils.closeQuietly(parentStream);
            IOUtils.closeQuietly(zipStream);
        }
    }

    /**
     * Extracts the document with the requested name contained in the zip document.
     *
     * @param zipDocument the zip document
     * @param name        the name of the requested file (including the path)
     * @return the result
     * @throws java.io.IOException The exception
     */
    public static DSSDocument extract(final DSSDocument zipDocument, final String name) throws IOException {
        if (!isZipFile(zipDocument)) {
            throw new IllegalArgumentException("The document in parameter is not in zip format!");
        }
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The name in parameter is empty!");
        }

        InputStream parentStream = null;
        ZipInputStream zipStream = null;
        try {
            parentStream = zipDocument.openStream();
            zipStream = new ZipInputStream(parentStream);

            while (true) {
                final ZipEntry entry = zipStream.getNextEntry();
                if (entry == null) {
                    break;
                }

                if (name.equals(entry.getName())) {
                    final InMemoryDocument doc = new InMemoryDocument(IOUtils.toByteArray(zipStream), entry.getName());
                    zipStream.closeEntry();
                    return doc;
                }

                zipStream.closeEntry();
            }
            return null;
        } finally {
            IOUtils.closeQuietly(parentStream);
            IOUtils.closeQuietly(zipStream);
        }
    }

    /**
     * Validate if the document is a ZIP.
     *
     * @param zipDocument The {@link DSSDocument}
     * @return The result
     */
    public static boolean isZipFile(final DSSDocument zipDocument) {
        InputStream inputStream = null;
        BufferedInputStream buffer = null;
        DataInputStream data = null;

        try {
            inputStream = zipDocument.openStream();
            buffer = new BufferedInputStream(inputStream);
            data = new DataInputStream(buffer);

            int test = data.readInt();
            return test == 0x504b0304;
        } catch (Exception e) {
            return false;
        } finally {
            IOUtils.closeQuietly(data);
            IOUtils.closeQuietly(buffer);
            IOUtils.closeQuietly(inputStream);
        }
    }
}
