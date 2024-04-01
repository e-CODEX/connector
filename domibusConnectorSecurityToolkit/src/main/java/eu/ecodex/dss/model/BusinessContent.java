/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/BusinessContent.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.model;

import eu.europa.esig.dss.model.DSSDocument;

import java.util.LinkedList;
import java.util.List;


/**
 * this holds the document and its attachments
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class BusinessContent {
    private DSSDocument document;
    private DSSDocument detachedSignature;
    private List<DSSDocument> attachments;

    /**
     * checks whether a document has been set
     *
     * @return the result
     */
    public boolean hasDocument() {
        return document != null;
    }

    /**
     * gives access to the document
     *
     * @return the value (may be null)
     */
    public DSSDocument getDocument() {
        return document;
    }

    /**
     * sets the document
     *
     * @param document the value (nullable)
     * @return this class' instance for chaining
     */
    public BusinessContent setDocument(final DSSDocument document) {
        this.document = document;
        return this;
    }

    /**
     * checks whether a detachedSignature has been set
     *
     * @return the result
     */
    public boolean hasDetachedSignature() {
        return detachedSignature != null;
    }

    /**
     * gives access to the (optional) DETACHED signature for the document
     *
     * @return the value (may be null)
     */
    public DSSDocument getDetachedSignature() {
        return detachedSignature;
    }

    /**
     * sets the (optional) DETACHED signature for the document
     *
     * @param document the value (nullable)
     * @return this class' instance for chaining
     */
    public BusinessContent setDetachedSignature(final DSSDocument document) {
        this.detachedSignature = document;
        return this;
    }

    /**
     * adds an attachment, implicitely creates the underlying list if null
     *
     * @param attachment the value (not nullable)
     * @return this class' instance for chaining
     */
    public BusinessContent addAttachment(final DSSDocument attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("the attachment must not be null");
        }
        if (attachments == null) {
            attachments = new LinkedList<DSSDocument>();
        }
        attachments.add(attachment);
        return this;
    }

    /**
     * checks whether at least one attachment is available
     *
     * @return the result
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * gives direct access to the list of attachments
     *
     * @return the value (may be null)
     */
    public List<DSSDocument> getAttachments() {
        if (attachments == null) {
            attachments = new LinkedList<DSSDocument>();
        }
        return attachments;
    }

    /**
     * sets the list of attachments
     *
     * @param attachments the value (nullable)
     * @return this class' instance for chaining
     */
    public BusinessContent setAttachments(final List<DSSDocument> attachments) {
        this.attachments = attachments;
        return this;
    }
}
