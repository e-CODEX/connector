/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/trunk/apps/ecodex-container/src/main/java/eu/ecodex/dss/util
 * /TokenStreamUtil.java $
 * $Revision: 1730 $
 * $Date: 2013-03-07 22:46:35 +0100 (Thu, 07 Mar 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.util;

import eu.europa.esig.dss.model.DSSDocument;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


/**
 * Utility class to validate a token xml file
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1730 $ - $Date: 2013-03-07 22:46:35 +0100 (Thu, 07 Mar 2013) $
 */
public final class TokenXMLValidatorUtil {
    private static final LogDelegate LOG = new LogDelegate(TokenXMLValidatorUtil.class);
    private static final String XMLDSIG_SYSTEM_ID = "http://www.w3.org/2000/09/xmldsig#Object";
    private static final String XMLDSIG_CORE_SCHEMA = "/eu/ecodex/dss/schemas/xmldsig-core-schema.xsd";
    private static final String TRUST_OK_TOKEN_SCHEMA = "/eu/ecodex/dss/schemas/TrustOkToken.xsd";
    private static final Schema TOKEN_SCHEMA;

    static {
        try {
            // load the XSDs from the resources
            final InputStream xsdToken = TokenXMLValidatorUtil.class.getResourceAsStream(TRUST_OK_TOKEN_SCHEMA);
            if (xsdToken == null) {
                throw new FileNotFoundException("the xsd file could not be found: " + TRUST_OK_TOKEN_SCHEMA);
            }
            final InputStream xsdXmldsig = TokenXMLValidatorUtil.class.getResourceAsStream(XMLDSIG_CORE_SCHEMA);
            if (xsdXmldsig == null) {
                throw new FileNotFoundException("the xsd file could not be found: " + XMLDSIG_CORE_SCHEMA);
            }

            // create a datacontainer for the resolver
            final ExternalLSInput extDsig = new ExternalLSInput();
            extDsig.setSystemId(XMLDSIG_SYSTEM_ID);
            extDsig.setStringData(IOUtils.toString(xsdXmldsig));

            // setup the schema factory and the resolve
            final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // cache the existing resolver
            final LSResourceResolver resourceResolver = factory.getResourceResolver();
            factory.setResourceResolver(new LSResourceResolver() {
                @Override
                public LSInput resolveResource(
                        final String type,
                        final String namespaceURI,
                        final String publicId,
                        final String systemId,
                        final String baseURI) {
                    // check for the dsig and return if requested
                    if (extDsig.getSystemId().equals(systemId)) {
                        return extDsig;
                    }
                    // delegate to the old resolver if present
                    return (resourceResolver == null) ? null : resourceResolver.resolveResource(
                            type,
                            namespaceURI,
                            publicId,
                            systemId,
                            baseURI
                    );
                }
            });

            // create the schema
            TOKEN_SCHEMA = factory.newSchema(new Source[]{new StreamSource(xsdToken)});
        } catch (Exception e) {
            LOG.lError("creation of the TOKEN_SCHEMA failed", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * The utility constructor
     */
    private TokenXMLValidatorUtil() {
    }

    public static boolean isTokenSchemaValid(final DSSDocument document) {
        try {
            // check pre-condition
            if (document == null) {
                throw new IllegalArgumentException("The token document provided in parameter must not be null");
            }

            // get the source of the document
            final Source xmlFile = new StreamSource(document.openStream());
            // instantiate a new validator
            final Validator validator = TOKEN_SCHEMA.newValidator();
            // and validate
            validator.validate(xmlFile);
            return true;
        } catch (SAXException e) {
            // if the file is not compliant, we end up here
            LOG.lInfo("the TOKEN_SCHEMA validated with a negative result on the document in parameter", e);
        } catch (IOException e) {
            // in case the document could not be read
            LOG.lError("the TOKEN_SCHEMA encountered a read error on the document in parameter", e);
        } catch (Exception e) {
            // something else happened that was not foreseen
            LOG.lError("the TOKEN_SCHEMA validation failed for an unknown reason", e);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.w3c.dom.ls.LSInput
     */
    private static class ExternalLSInput implements LSInput {
        private Reader characterStream;
        private InputStream byteStream;
        private String stringData;
        private String systemId;
        private String publicId;
        private String baseURI;
        private String encoding;
        private boolean certifiedText;

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#getCharacterStream()
         */
        @Override
        public Reader getCharacterStream() {
            return characterStream;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#setCharacterStream(java.io.Reader)
         */
        @Override
        public void setCharacterStream(final Reader characterStream) {
            this.characterStream = characterStream;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#getByteStream()
         */
        @Override
        public InputStream getByteStream() {
            return byteStream;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#setByteStream(java.io.InputStream)
         */
        @Override
        public void setByteStream(final InputStream byteStream) {
            this.byteStream = byteStream;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#getStringData()
         */
        @Override
        public String getStringData() {
            return stringData;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#setStringData(java.lang.String)
         */
        @Override
        public void setStringData(final String stringData) {
            this.stringData = stringData;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#getSystemId()
         */
        @Override
        public String getSystemId() {
            return systemId;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#setSystemId(java.lang.String)
         */
        @Override
        public void setSystemId(final String systemId) {
            this.systemId = systemId;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#getPublicId()
         */
        @Override
        public String getPublicId() {
            return publicId;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#setPublicId(java.lang.String)
         */
        @Override
        public void setPublicId(final String publicId) {
            this.publicId = publicId;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#getBaseURI()
         */
        @Override
        public String getBaseURI() {
            return baseURI;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#setBaseURI(java.lang.String)
         */
        @Override
        public void setBaseURI(final String baseURI) {
            this.baseURI = baseURI;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#getEncoding()
         */
        @Override
        public String getEncoding() {
            return encoding;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#setEncoding(java.lang.String)
         */
        @Override
        public void setEncoding(final String encoding) {
            this.encoding = encoding;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#getCertifiedText()
         */
        @Override
        public boolean getCertifiedText() {
            return certifiedText;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.w3c.dom.ls.LSInput#setCertifiedText(boolean)
         */
        @Override
        public void setCertifiedText(final boolean certifiedText) {
            this.certifiedText = certifiedText;
        }
    }
}
