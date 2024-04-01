package eu.ecodex.dss.util.tsl;

import eu.ecodex.dss.util.ECodexDataLoader;
import eu.ecodex.dss.util.LogDelegate;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;


@Deprecated // replace with native DSS classes
public class ReactiveDataLoader implements DataLoader {
    private static final LogDelegate LOG = new LogDelegate(ReactiveDataLoader.class);

    private final Document inMemoryTSL;
    private final Object TSL;
    private final DataLoader dataLoader;
    private boolean isLOTL;

    public ReactiveDataLoader(Document inMemoryTSL, Object authenticationCertificateTSL, ProxyConfig proxyManager) {
        this.inMemoryTSL = inMemoryTSL;
        this.TSL = authenticationCertificateTSL;

        ECodexDataLoader dataLoader = new ECodexDataLoader();
        dataLoader.setProxyConfig(proxyManager);

        this.dataLoader = dataLoader;

        this.isLOTL = false;
    }

    public ReactiveDataLoader(Document inMemoryTSL, Object authenticationCertificateTSL, DataLoader dataLoader) {
        this.inMemoryTSL = inMemoryTSL;
        this.TSL = authenticationCertificateTSL;

        //		this.dataLoader = new ECodexDataLoader();
        //		this.dataLoader.setProxyConfig(proxyManager);
        this.dataLoader = dataLoader;

        this.isLOTL = false;
    }

    @Override
    public byte[] get(String givenURL) {
        if (givenURL.toLowerCase().startsWith("http:") ||
                givenURL.toLowerCase().startsWith("https:") ||
                givenURL.toLowerCase().startsWith("ldap:")) {

            return this.dataLoader.get(givenURL);
        } else if (givenURL.toLowerCase().startsWith("file:")) {
            try {
                final URL location = new URL(givenURL);
                InputStream in = location.openStream();
                if (in == null) {
                    throw new IOException("Cannot retrieve the resource from URL " + givenURL);
                }

                return IOUtils.toByteArray(in);
            } catch (IOException ex) {
                throw new DSSException(givenURL, ex);
            }
        } else if (givenURL.equalsIgnoreCase("inmemory:bytearray")) {
            return (byte[]) TSL;
        } else if (givenURL.equalsIgnoreCase("inmemory:inputstream")) {
            try {
                return IOUtils.toByteArray((InputStream) TSL);
            } catch (IOException e) {
                throw new DSSException("Unable to create byte[] from Inputstream", e);
            }
        }
        // klara: Currently an intermediate LOTL is necessary as the Arhs library needs a LOTL to contact a given TSL
        else if (givenURL.equalsIgnoreCase("inmemory:intermediatetsl") && !isLOTL) {

            NodeList nodeList = inMemoryTSL.getElementsByTagName("tsl:TSLLocation");

            if (TSL instanceof String) {
                nodeList.item(0).setTextContent((String) TSL);
            } else if (TSL instanceof InputStream) {
                nodeList.item(0).setTextContent("inmemory:inputstream");
            } else if (TSL instanceof byte[]) {
                nodeList.item(0).setTextContent("inmemory:bytearray");
            } else {
                nodeList.item(0).setTextContent("");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(inMemoryTSL);
            Result outputTarget = new StreamResult(outputStream);

            try {
                TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            } catch (TransformerConfigurationException ex) {
                throw new DSSException(
                        "Internal TransformerConfigurationException within the DataLoader! Please contact the creator" +
                                " " +
                                "of the e-CODEX Trust Library!",
                        ex
                );
            } catch (TransformerException e) {
                throw new DSSException(
                        "Internal TransformerException within the DataLoader! Please contact the creator of the " +
                                "e-CODEX Trust Library!",
                        e
                );
            } catch (TransformerFactoryConfigurationError e) {
                throw new DSSException(
                        "Internal TransformerFactoryConfigurationError within the DataLoader! Please contact the " +
                                "creator of the e-CODEX Trust Library!",
                        e
                );
            }

            return outputStream.toByteArray();
        } else {
            LOG.lWarn("Unable to find DataLoader for given AuthenticationCertificateTSL \"" + givenURL + "\"");
            LOG.lWarn(
                    "AuthenticationCertificateTSL must be an URI String and currently has to start with \"http:\", " +
                            "\"https:\" or \"file:\"");
            throw new UnsupportedOperationException();
        }
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
    public byte[] post(String URL, byte[] content) {
        if (URL.toLowerCase().startsWith("http:") ||
                URL.toLowerCase().startsWith("https:")) {
            return dataLoader.post(URL, content);
        } else if (URL.toLowerCase().startsWith("file:")) {
            throw new UnsupportedOperationException();
        } else if (URL.equalsIgnoreCase("inmemory:intermediatetsl")) {
            throw new UnsupportedOperationException();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setContentType(String contentType) {
    }

    public void isLOTL(boolean isLOTL) {
        this.isLOTL = isLOTL;
    }
}
