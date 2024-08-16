/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/* ---------------------------------------------------------------------------
             COMPETITIVENESS AND INNOVATION FRAMEWORK PROGRAMME
                   ICT Policy Support Programme (ICT PSP)
           Preparing the implementation of the Services Directive
                   ICT PSP call identifier: ICT PSP-2008-2
             ICT PSP main Theme identifier: CIP-ICT-PSP.2008.1.1
                           Project acronym: SPOCS
   Project full title: Simple Procedures Online for Cross-border Services
                         Grant agreement no.: 238935
                               www.eu-spocs.eu
------------------------------------------------------------------------------
    WP3 Interoperable delivery, eSafe, secure and interoperable exchanges
                       and acknowledgement of receipt
------------------------------------------------------------------------------
        Open module implementing the eSafe document exchange protocol
------------------------------------------------------------------------------

$URL: svn:https://svnext.bos-bremen.de/SPOCS/AllWpImplementation/EDelivery-Gateway
$Date: 2010-10-14 18:55:57 +0200 (Do, 14. Okt 2010) $
$Revision: 86 $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */

package eu.spocseu.edeliverygw.evidences.exception;

import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.evidences.Evidence;
import lombok.Getter;
import lombok.Setter;
import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.EventReasonsType;

/**
 * The EvidenceException will be thrown in the case of evidences with fault codes.
 *
 * @author Lindemann
 */
@Getter
@Setter
public class EvidenceException extends Exception {
    private static final long serialVersionUID = 1L;
    private final Evidence evidence;
    private REMErrorEvent errorEvent;

    protected EvidenceException(
        String message, Evidence evidence,
        REMErrorEvent errorEvent, Throwable cause) {
        super(message, cause);
        this.evidence = evidence;
        this.errorEvent = errorEvent;
    }

    protected EvidenceException(
        Evidence evidence, REMErrorEvent errorEvent,
        Throwable cause) {
        super(cause);
        this.evidence = evidence;
        if (this.evidence.getXSDObject().getEventReasons() == null) {
            this.errorEvent = errorEvent;
            var reasons = new EventReasonsType();
            var reason = new EventReasonType();
            reason.setCode(this.errorEvent.getEventCode());
            if (cause != null) {
                reason.setDetails(cause.getMessage());
            } else {
                reason.setDetails(this.errorEvent.getEventDetails());
            }
            reasons.getEventReason().add(reason);
            this.evidence.getXSDObject().setEventReasons(reasons);
        }
    }

    protected EvidenceException(
        String message, Evidence evidence,
        REMErrorEvent errorEvent) {
        super(message);
        this.evidence = evidence;
        this.errorEvent = errorEvent;
    }
}
