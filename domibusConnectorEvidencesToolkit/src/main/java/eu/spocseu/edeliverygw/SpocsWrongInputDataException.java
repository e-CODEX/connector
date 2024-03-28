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
package eu.spocseu.edeliverygw;

/**
 * This exception will be thrown in the case of wrong input data. For example if
 * the input data could not be parsed with jaxb. In this case the user has to
 * change the input parameter and try it then again.
 *
 * @author Lindemann
 */
public class SpocsWrongInputDataException extends Exception {
    private static final long serialVersionUID = -3191051364594522380L;

    public SpocsWrongInputDataException(String _message, Throwable _cause) {
        super(_message, _cause);
    }

    public SpocsWrongInputDataException(Throwable _cause) {
        super(_cause);
    }

    public SpocsWrongInputDataException(String _message) {
        super(_message);
    }
}
