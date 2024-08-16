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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/ZipStreamUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Provides convenience-methods for ZIP documents.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@UtilityClass
public class ZipStreamUtil {
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

        final List<DSSDocument> documents = new ArrayList<>();

        try (
            var parentStream = zipDocument.openStream();
            var zipStream = new ZipInputStream(parentStream)
        ) {
            while (true) {
                final ZipEntry entry = zipStream.getNextEntry();
                if (entry == null) {
                    break;
                }

                if (StringUtils.isEmpty(entry.getName())) {
                    throw new IllegalArgumentException(
                        "The zip document in parameter contains an entry with an empty name.");
                }

                final var doc =
                    new InMemoryDocument(IOUtils.toByteArray(zipStream), entry.getName());
                documents.add(doc);
                zipStream.closeEntry();
            }
            return documents;
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
    public static DSSDocument extract(final DSSDocument zipDocument, final String name)
        throws IOException {

        if (!isZipFile(zipDocument)) {
            throw new IllegalArgumentException("The document in parameter is not in zip format!");
        }
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("The name in parameter is empty!");
        }

        try (
            var parentStream = zipDocument.openStream();
            var zipStream = new ZipInputStream(parentStream)
        ) {
            while (true) {
                final ZipEntry entry = zipStream.getNextEntry();
                if (entry == null) {
                    break;
                }

                if (name.equals(entry.getName())) {
                    final var doc =
                        new InMemoryDocument(IOUtils.toByteArray(zipStream), entry.getName());
                    zipStream.closeEntry();
                    return doc;
                }

                zipStream.closeEntry();
            }
            return null;
        }
    }

    /**
     * Validate if the document is a ZIP.
     *
     * @param zipDocument The {@link DSSDocument}
     * @return The result
     */
    public static boolean isZipFile(final DSSDocument zipDocument) {
        try (
            var inputStream = zipDocument.openStream();
            var buffer = new BufferedInputStream(inputStream);
            var data = new DataInputStream(buffer)
        ) {
            var test = data.readInt();
            return test == 0x504b0304;
        } catch (Exception e) {
            return false;
        }
    }
}
