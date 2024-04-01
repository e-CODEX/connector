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
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;


public class LotlCreator {
    public static TrustedListsCertificateSource createTrustedListsCertificateSource(Object authenticationCertificateTSL) {
        return createTrustedListsCertificateSource(authenticationCertificateTSL, false, null);
    }

    public static TrustedListsCertificateSource createTrustedListsCertificateSource(
            Object authenticationCertificateTSL,
            boolean isLOTL,
            DataLoader dl) {
        if (dl == null) {
            dl = new CommonsDataLoader();
        }
        try {

            org.w3c.dom.Document inMemoryTSL = createInMemoryTSL();

            ReactiveDataLoader reactiveDataLoader =
                    new ReactiveDataLoader(inMemoryTSL, authenticationCertificateTSL, dl);
            //
            //		dataLoader.isLOTL(this.isLOTL);

            //		TLInfo test = new TLInfo();

            TrustedListsCertificateSource trustedListCertificatesSource = new TrustedListsCertificateSource();

            //		TSLRepository tslRepository = new TSLRepository();
            //		tslRepository.setTrustedListsCertificateSource(trustedListCertificatesSource);

            FileCacheDataLoader onlineDataLoader = new FileCacheDataLoader();
            onlineDataLoader.setDataLoader(reactiveDataLoader);

            TLValidationJob job = new TLValidationJob();
            job.setOnlineDataLoader(onlineDataLoader);

            if (!isLOTL) {
                TLSource tlSource = new TLSource();
                tlSource.setUrl("inmemory:intermediatetsl");
                job.setTrustedListSources(tlSource);
            } else if (authenticationCertificateTSL instanceof String) {
                LOTLSource lotlSource = new LOTLSource();
                lotlSource.setUrl((String) authenticationCertificateTSL);
                job.setListOfTrustedListSources(lotlSource);
            } else if (authenticationCertificateTSL instanceof InputStream) {
                LOTLSource lotlSource = new LOTLSource();
                lotlSource.setUrl("inmemory:inputstream");
                job.setListOfTrustedListSources(lotlSource);
            } else if (authenticationCertificateTSL instanceof byte[]) {
                LOTLSource lotlSource = new LOTLSource();
                lotlSource.setUrl("inmemory:bytearray");
                job.setListOfTrustedListSources(lotlSource);
            } else {
                // trustedListCertificatesSource.setLotlCertificate(null);
            }

            job.onlineRefresh();
            return trustedListCertificatesSource;
        } catch (Exception e) {
            throw new RuntimeException(new ECodexException(e));
        } finally {
            if (authenticationCertificateTSL instanceof InputStream) {
                try {
                    ((InputStream) authenticationCertificateTSL).close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    // klara
    private static org.w3c.dom.Document createInMemoryTSL() throws ECodexException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ECodexException(e);
        }

        InputStream in = ResourceUtil.getStream("/TSL/DummyLOTL.xml");

        org.w3c.dom.Document document = null;

        try {
            document = db.parse(in);
        } catch (SAXException e) {
            throw new ECodexException(e);
        } catch (IOException e) {
            throw new ECodexException(e);
        }

        return document;
    }
}
