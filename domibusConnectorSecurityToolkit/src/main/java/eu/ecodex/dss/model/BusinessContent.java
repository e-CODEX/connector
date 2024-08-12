/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

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
import lombok.Data;

/**
 * This holds the document and its attachments.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@Data
public class BusinessContent {
    private DSSDocument document;
    private DSSDocument detachedSignature;
    private List<DSSDocument> attachments;

    /**
     * Checks whether this instance of the BusinessContent class has a document or not.
     *
     * @return true if the document is not null, false otherwise.
     */
    public boolean hasDocument() {
        return document != null;
    }

    /**
     * Sets the document.
     *
     * @param document the value (nullable)
     * @return this class' instance for chaining
     */
    public BusinessContent setDocument(final DSSDocument document) {
        this.document = document;
        return this;
    }

    /**
     * Checks whether a detachedSignature has been set.
     *
     * @return the result
     */
    public boolean hasDetachedSignature() {
        return detachedSignature != null;
    }

    /**
     * Sets the (optional) DETACHED signature for the document.
     *
     * @param document the value (nullable)
     * @return this class' instance for chaining
     */
    public BusinessContent setDetachedSignature(final DSSDocument document) {
        this.detachedSignature = document;
        return this;
    }

    /**
     * Adds an attachment, implicitly creates the underlying list if null.
     *
     * @param attachment the value (not nullable)
     * @return this class' instance for chaining
     */
    public BusinessContent addAttachment(final DSSDocument attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("the attachment must not be null");
        }
        if (attachments == null) {
            attachments = new LinkedList<>();
        }
        attachments.add(attachment);
        return this;
    }

    /**
     * Checks whether at least one attachment is available.
     *
     * @return the result
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * Gives direct access to the list of attachments.
     *
     * @return the value (maybe null)
     */
    public List<DSSDocument> getAttachments() {
        if (attachments == null) {
            attachments = new LinkedList<>();
        }
        return attachments;
    }

    /**
     * Sets the list of attachments.
     *
     * @param attachments the value (nullable)
     * @return this class' instance for chaining
     */
    public BusinessContent setAttachments(final List<DSSDocument> attachments) {
        this.attachments = attachments;
        return this;
    }
}
