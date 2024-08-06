/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
$Date: 2010-05-13 18:55:57 +0200 (Do, 14. Okt 2010) $
$Revision: 86 $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */

package eu.spocseu.edeliverygw.configuration;

import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;

/**
 * This Class represents the holds the <code>Configuration</code> of the eDelivery Project.
 *
 * @author oley
 */
@Getter
@Setter
public class EDeliveryDetails {
    private String gatewayName;
    private String streetAdress;
    private String locality;
    private String postalCode;
    private String country;
    private boolean checkSignature = false;
    private boolean checkMessage = false;
    private String gatewayAddress;
    private String gatewayDomain;
    private int defaultCitizenQAAlevel = 1;
    private boolean synchronGatewayMD = true;
    private static Properties spocsProperties;

    /**
     * Creates a new instance of EDeliveryDetails using data from the provided EDeliveryDetail
     * object.
     *
     * @param jaxBObject The EDeliveryDetail object containing the data.
     */
    public EDeliveryDetails(EDeliveryDetail jaxBObject) {
        setGatewayAddress(jaxBObject.getServer().getGatewayAddress());
        setGatewayName(jaxBObject.getServer().getGatewayName());
        // PostalAdress
        if (jaxBObject.getPostalAdress() != null) {
            setStreetAdress(jaxBObject.getPostalAdress().getStreetAddress());
            setLocality(jaxBObject.getPostalAdress().getLocality());
            setPostalCode(jaxBObject.getPostalAdress().getPostalCode());
            setCountry(jaxBObject.getPostalAdress().getCountry());
        }

        jaxBObject.getServer().getDefaultCitizenQAAlevel();
        if (jaxBObject.getServer().getDefaultCitizenQAAlevel() != null) {
            setDefaultCitizenQAAlevel(jaxBObject.getServer()
                                                .getDefaultCitizenQAAlevel());
        }
    }
}
