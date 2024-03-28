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
import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.EventReasonsType;


/**
 * The EvidenceException will be thrown in the case of evidences with fault
 * codes.
 *
 * @author Lindemann
 */
public class EvidenceException extends Exception {
    private static final long serialVersionUID = 1L;
    private Evidence evidence;
    private REMErrorEvent errorEvent;

    protected EvidenceException(
            String message, Evidence _evidence, REMErrorEvent _errorEvent, Throwable _cause) {
        super(message, _cause);
        evidence = _evidence;
        errorEvent = _errorEvent;
    }

    protected EvidenceException(
            Evidence _evidence, REMErrorEvent _errorEvent, Throwable _cause) {
        super(_cause);
        evidence = _evidence;
        if (evidence.getXSDObject().getEventReasons() == null) {
            errorEvent = _errorEvent;
            EventReasonsType reasons = new EventReasonsType();
            EventReasonType reason = new EventReasonType();
            reason.setCode(errorEvent.getEventCode());
            if (_cause != null) {
                reason.setDetails(_cause.getMessage());
            } else {
                reason.setDetails(errorEvent.getEventDetails());
            }
            reasons.getEventReason().add(reason);
            evidence.getXSDObject().setEventReasons(reasons);
        }
    }

    protected EvidenceException(
            String message, Evidence _evidence, REMErrorEvent _errorEvent) {
        super(message);
        evidence = _evidence;
        errorEvent = _errorEvent;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence _evidence) {
        this.evidence = _evidence;
    }

    public REMErrorEvent getErrorEvent() {
        return errorEvent;
    }
}
