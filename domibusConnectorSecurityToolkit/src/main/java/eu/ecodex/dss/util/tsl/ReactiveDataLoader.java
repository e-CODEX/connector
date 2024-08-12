/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.ecodex.dss.util.tsl;

import eu.ecodex.dss.util.ECodexDataLoader;
import eu.ecodex.dss.util.LogDelegate;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

/**
 * The ReactiveDataLoader class is a deprecated class used for loading data from different sources.
 * It implements the DataLoader interface.
 *
 * @deprecated This class is deprecated and should be replaced with native DSS classes.
 */
@SuppressWarnings("squid:S1135")
@Deprecated // replace with native DSS classes
public class ReactiveDataLoader implements DataLoader {
    private static final LogDelegate LOG = new LogDelegate(ReactiveDataLoader.class);
    private final Document inMemoryTSL;
    private final Object tsl;
    private final DataLoader dataLoader;
    private boolean isLOTL;

    /**
     * This class represents a reactive data loader that is used to load data asynchronously.
     *
     * @param inMemoryTSL                  The in-memory TSL document.
     * @param authenticationCertificateTSL The authentication certificate TSL. It can be a String
     *                                     URL, an InputStream, or a byte array.
     * @param proxyManager                 The ProxyConfig object that contains proxy
     *                                     configurations.
     */
    public ReactiveDataLoader(
        Document inMemoryTSL, Object authenticationCertificateTSL, ProxyConfig proxyManager) {
        this.inMemoryTSL = inMemoryTSL;
        this.tsl = authenticationCertificateTSL;

        var loader = new ECodexDataLoader();
        loader.setProxyConfig(proxyManager);

        this.dataLoader = loader;

        this.isLOTL = false;
    }

    /**
     * This class represents a reactive data loader that is used to load data asynchronously.
     *
     * @param inMemoryTSL                  The in-memory TSL document.
     * @param authenticationCertificateTSL The authentication certificate TSL. It can be a String
     *                                     URL, an InputStream, or a byte array.
     * @param dataLoader                   The DataLoader object to use for data loading.
     */
    public ReactiveDataLoader(
        Document inMemoryTSL, Object authenticationCertificateTSL, DataLoader dataLoader) {
        this.inMemoryTSL = inMemoryTSL;
        this.tsl = authenticationCertificateTSL;
        this.dataLoader = dataLoader;
        this.isLOTL = false;
    }

    @Override
    public byte[] post(String url, byte[] content) {
        if (url.toLowerCase().startsWith("http:")
            || url.toLowerCase().startsWith("https:")) {
            return dataLoader.post(url, content);
        } else if (url.toLowerCase().startsWith("file:")) {
            throw new UnsupportedOperationException();
        } else if (url.equalsIgnoreCase("inmemory:intermediatetsl")) {
            throw new UnsupportedOperationException();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void isLOTL(boolean isLOTL) {
        this.isLOTL = isLOTL;
    }

    @Override
    public void setContentType(String contentType) {
        // TODO see why this method body is empty
    }

    @Override
    public DataAndUrl get(List<String> urlStrings) {
        // Not needed till now
        return null;
    }

    @Override
    public byte[] get(String url, boolean refresh) {
        return this.get(url);
    }

    @Override
    public byte[] get(String givenURL) {
        if (givenURL.toLowerCase().startsWith("http:")
            || givenURL.toLowerCase().startsWith("https:")
            || givenURL.toLowerCase().startsWith("ldap:")) {

            return this.dataLoader.get(givenURL);
        } else if (givenURL.toLowerCase().startsWith("file:")) {
            try {
                final var location = new URL(givenURL);
                InputStream in = location.openStream();
                if (in == null) {
                    throw new IOException("Cannot retrieve the resource from URL " + givenURL);
                }

                return IOUtils.toByteArray(in);
            } catch (IOException ex) {
                throw new DSSException(givenURL, ex);
            }
        } else if (givenURL.equalsIgnoreCase("inmemory:bytearray")) {
            return (byte[]) tsl;
        } else if (givenURL.equalsIgnoreCase("inmemory:inputstream")) {
            try {
                return IOUtils.toByteArray((InputStream) tsl);
            } catch (IOException e) {
                throw new DSSException("Unable to create byte[] from Inputstream", e);
            }
        } else if (givenURL.equalsIgnoreCase("inmemory:intermediatetsl") && !isLOTL) {
            // klara: Currently an intermediate LOTL is necessary as the Arhs library needs a
            // LOTL to contact a given TSL

            var nodeList = inMemoryTSL.getElementsByTagName("tsl:TSLLocation");

            switch (tsl) {
                case String content -> nodeList.item(0).setTextContent(content);
                case InputStream ignored -> nodeList.item(0).setTextContent("inmemory:inputstream");
                case byte[] ignored -> nodeList.item(0).setTextContent("inmemory:bytearray");
                case null, default -> nodeList.item(0).setTextContent("");
            }

            var outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(inMemoryTSL);
            Result outputTarget = new StreamResult(outputStream);

            try {
                var transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                var transformer = transformerFactory.newTransformer();
                transformer.transform(xmlSource, outputTarget);
            } catch (TransformerConfigurationException ex) {
                throw new DSSException(
                    "Internal TransformerConfigurationException within the DataLoader! Please "
                        + "contact the creator of the e-CODEX Trust Library!",
                    ex
                );
            } catch (TransformerException e) {
                throw new DSSException(
                    "Internal TransformerException within the DataLoader! Please contact the "
                        + "creator of the e-CODEX Trust Library!",
                    e
                );
            } catch (TransformerFactoryConfigurationError e) {
                throw new DSSException(
                    "Internal TransformerFactoryConfigurationError within the DataLoader! Please "
                        + "contact the creator of the e-CODEX Trust Library!",
                    e
                );
            }

            return outputStream.toByteArray();
        } else {
            LOG.lWarn(
                "Unable to find DataLoader for given AuthenticationCertificateTSL \""
                    + givenURL + "\""
            );
            LOG.lWarn(
                "AuthenticationCertificateTSL must be an URI String and currently has "
                    + "to start with \"http:\", \"https:\" or \"file:\""
            );
            throw new UnsupportedOperationException();
        }
    }
}
