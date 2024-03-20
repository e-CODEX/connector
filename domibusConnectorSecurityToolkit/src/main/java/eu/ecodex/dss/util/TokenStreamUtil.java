/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/TokenStreamUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import eu.ecodex.dss.model.token.OriginalValidationReportContainer;
import eu.ecodex.dss.model.token.Signature;
import eu.ecodex.dss.model.token.Token;
//import eu.europa.ec.markt.dss.validation.report.ValidationReport;

/**
 * Utility class to decode/encode a {@link Token}
 * It uses a static JAXBContext as this is thread-safe in contrast to the (un)marshallers, which are created on each
 * method call.
 * If performance becomes an issue, these may be pooled.
 *
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class TokenStreamUtil {

    private static final LogDelegate LOG = new LogDelegate(TokenStreamUtil.class);

    // a message to be outputted in case a JAXBException occurs
    public static final String EX_HINT = "A " + JAXBException.class
          .getSimpleName() + " exception occurred; probably because of non-compliant data in the list of the " + OriginalValidationReportContainer.class
          .getSimpleName() + " object. Please use simple types wrapped in " + OriginalValidationReportContainer.SimpleTypeEntry.class.getSimpleName() + ".";

    // the factory is annotated as @XmlRegistry, which is specified to be thread-safe.
    // so we instantiate it only once for performance reasons
    private static final TokenJAXBObjectFactory FACTORY = new TokenJAXBObjectFactory();

    private static final Map<String, String> NS_PREFIXES_MAP;

    static {
        NS_PREFIXES_MAP = new HashMap<String, String>();
        NS_PREFIXES_MAP.put("http://www.w3.org/2000/09/xmldsig#", "ds");
    }



    /**
     * singleton in initializer pattern, to reliably create the context only if the object is actually used
     */
    private static class JAXBContextHolder {
        /**
         * the shared context
         */
        private static final JAXBContext INSTANCE;

        static {
            try {
                // NICE-TO-HAVE dynamically reference other classes based on e.g. a properties file.
	            // todo to be checked migration V4
                INSTANCE = JAXBContext.newInstance(TokenJAXBObjectFactory.class, OriginalValidationReportContainer.SimpleTypeEntry.class); //, ValidationReport.class);
            } catch (final JAXBException e) {
                LOG.lError("instantiation", e);
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    /**
     * private utility constructor
     */
    private TokenStreamUtil() {
    }

    /**
     * Decode the XML stream to the token
     *
     * @param xmlInputStream The input stream
     * @return a {@link Token}
     * @throws Exception The exception
     */
    @SuppressWarnings({"unchecked"})
    public static Token decodeXMLStream(final InputStream xmlInputStream) throws Exception {
        final Unmarshaller delegate = JAXBContextHolder.INSTANCE.createUnmarshaller();

        try {
            final JAXBElement<Token> xmlToken = (JAXBElement<Token>) delegate.unmarshal(xmlInputStream);
            final Token token = xmlToken.getValue();
            
            // 20150608 - AK - Reason for this line unknown
            // final Signature signatureData = token.getValidationVerificationSignatureData();

            return token;
        } catch (JAXBException e) {
            LOG.lError(EX_HINT);
            throw e;
        }
    }

    /**
     * Encode the token to the an XML stream.
     *
     * @param token The {@link Token}
     * @return a {@link java.io.OutputStream}
     * @throws Exception The exception
     */
    public static ByteArrayOutputStream encodeXMLStream(final Token token) throws Exception {
        final Marshaller delegate = JAXBContextHolder.INSTANCE.createMarshaller();
        delegate.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        final JAXBElement<Token> xmlToken = FACTORY.createTrustOkToken(token);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            delegate.marshal(xmlToken, output);
        } catch (JAXBException e) {
            LOG.lError(EX_HINT);
            throw e;
        }

        return output;
    }

}