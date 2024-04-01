/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/XmlStreamUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.util;

import eu.europa.esig.dss.model.DSSDocument;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Provide conveniences methods for XML stream.
 *
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class XmlStreamUtil {
    /**
     * The default constructor for XmlStreamUtil.
     */
    private XmlStreamUtil() {
    }

    /**
     * Validate if the document is an XML and well-formed
     *
     * @param document The {@link DSSDocument}
     * @return The result
     */
    public static boolean isXmlFile(final DSSDocument document) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder parser = factory.newDocumentBuilder();
            parser.parse(document.openStream());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
