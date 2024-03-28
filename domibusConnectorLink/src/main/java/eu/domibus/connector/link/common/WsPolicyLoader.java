package eu.domibus.connector.link.common;

import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class WsPolicyLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsPolicyLoader.class);

    private final Resource wsPolicy;

    public WsPolicyLoader(Resource resource) {
        this.wsPolicy = resource;
    }

    public WSPolicyFeature loadPolicyFeature() {
        LOGGER.debug("Loading policy from resource: [{}]", wsPolicy);
        WSPolicyFeature policyFeature = new WSPolicyFeature();
        policyFeature.setEnabled(true);

        InputStream is = null;
        try {
            is = wsPolicy.getInputStream();
        } catch (IOException ioe) {
            throw new UncheckedIOException(String.format("ws policy [%s] cannot be read!", wsPolicy), ioe);
        }
        if (is == null) {
            throw new WsPolicyLoaderException(String.format(
                    "ws policy [%s] cannot be read! InputStream is nulL!",
                    wsPolicy
            ));
        }
        List<Element> policyElements = new ArrayList<Element>();
        try {
            Element e = StaxUtils.read(is).getDocumentElement();
            LOGGER.debug("adding policy element [{}]", e);
            policyElements.add(e);
        } catch (XMLStreamException ex) {
            throw new WsPolicyLoaderException("cannot parse policy " + wsPolicy, ex);
        }
        // policyFeature.getPolicyElements().addAll(policyElements);
        policyFeature.setPolicyElements(policyElements);
        return policyFeature;
    }
}
