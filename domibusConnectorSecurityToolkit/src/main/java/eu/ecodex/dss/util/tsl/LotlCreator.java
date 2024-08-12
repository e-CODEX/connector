/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.ecodex.dss.util.tsl;

import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.util.ResourceUtil;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.tsl.source.TLSource;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.experimental.UtilityClass;
import org.xml.sax.SAXException;

/**
 * The LotlCreator class provides methods for creating a trusted list certificate source. It
 * contains a static method, createTrustedListsCertificateSource, which can be used to create a
 * trusted list certificate source with the given parameters.
 */
@UtilityClass
public class LotlCreator {
    public static TrustedListsCertificateSource createTrustedListsCertificateSource(
        Object authenticationCertificateTSL) {
        return createTrustedListsCertificateSource(authenticationCertificateTSL, false, null);
    }

    /**
     * Creates a TrustedListsCertificateSource object with the provided parameters.
     *
     * @param authenticationCertificateTSL The authentication certificate TSL. It can be a String
     *                                     URL, an InputStream, or a byte array.
     * @param isLOTL                       A boolean value indicating if the provided certificate
     *                                     TSL is a LOTL (List of Trusted Lists).
     * @param dl                           The DataLoader object to use for data loading. If null, a
     *                                     default DataLoader will be used.
     * @return The created TrustedListsCertificateSource object.
     * @throws RuntimeException if an Exception occurs during the creation of the
     *                          TrustedListsCertificateSource.
     */
    public static TrustedListsCertificateSource createTrustedListsCertificateSource(
        Object authenticationCertificateTSL, boolean isLOTL, DataLoader dl) {
        if (dl == null) {
            dl = new CommonsDataLoader();
        }
        try {
            org.w3c.dom.Document inMemoryTSL = createInMemoryTSL();
            var reactiveDataLoader =
                new ReactiveDataLoader(inMemoryTSL, authenticationCertificateTSL, dl);

            var onlineDataLoader = new FileCacheDataLoader();
            onlineDataLoader.setDataLoader(reactiveDataLoader);

            var job = new TLValidationJob();
            job.setOnlineDataLoader(onlineDataLoader);

            if (!isLOTL) {
                var tlSource = new TLSource();
                tlSource.setUrl("inmemory:intermediatetsl");
                job.setTrustedListSources(tlSource);
            } else if (authenticationCertificateTSL instanceof String url) {
                var lotlSource = new LOTLSource();
                lotlSource.setUrl(url);
                job.setListOfTrustedListSources(lotlSource);
            } else if (authenticationCertificateTSL instanceof InputStream) {
                var lotlSource = new LOTLSource();
                lotlSource.setUrl("inmemory:inputstream");
                job.setListOfTrustedListSources(lotlSource);
            } else if (authenticationCertificateTSL instanceof byte[]) {
                var lotlSource = new LOTLSource();
                lotlSource.setUrl("inmemory:bytearray");
                job.setListOfTrustedListSources(lotlSource);
            }

            job.onlineRefresh();
            return new TrustedListsCertificateSource();
        } catch (Exception e) {
            throw new RuntimeException(new ECodexException(e));
        } finally {
            if (authenticationCertificateTSL instanceof InputStream inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    // klara
    private static org.w3c.dom.Document createInMemoryTSL() throws ECodexException {

        var builderFactory = DocumentBuilderFactory.newInstance();

        try {
            builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        builderFactory.setNamespaceAware(true);
        DocumentBuilder db;
        try {
            db = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ECodexException(e);
        }

        InputStream in = ResourceUtil.getStream("/TSL/DummyLOTL.xml");

        org.w3c.dom.Document document = null;

        try {
            document = db.parse(in);
        } catch (SAXException | IOException e) {
            throw new ECodexException(e);
        }

        return document;
    }
}
