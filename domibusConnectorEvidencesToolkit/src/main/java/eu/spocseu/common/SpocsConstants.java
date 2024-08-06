/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.spocseu.common;

import javax.xml.namespace.QName;
import lombok.Getter;

/**
 * This class contains constant values used in the Spocs application.
 */
@SuppressWarnings("checkstyle:TypeName")
public class SpocsConstants {
    /**
     * Represents the Qualified Name for the SPOCS service.
     *
     * <p>The SPOCS_SERVICE_QNAME variable holds the Qualified Name for the SPOCS service.
     * It is created using the namespace URI "http://spocsinterconnect.gateway.eu" and the local
     * part "GatewayService".
     *
     * <p>The SPOCS_SERVICE_QNAME constant should be used to refer to the QName of the SPOCS
     * service throughout the codebase.
     */
    public static final QName SPOCS_SERVICE_QNAME = new QName(
        "http://spocsinterconnect.gateway.eu", "GatewayService");

    /**
     * E_ADDRESS_SCHEMES is an enumeration class that represents different address schemes.
     */
    public enum E_ADDRESS_SCHEMES {
        RFC5322ADDRESS("RFC5322-Address"),
        BUSDOX("busdox-actorid-upis");
        private final String specName;

        E_ADDRESS_SCHEMES(String spec) {
            specName = spec;
        }

        public String getAsString() {
            return specName;
        }
    }

    /**
     * Enum representing authentication levels.
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public enum AUTHENTICATION_LEVEL {
        LEVEL_1("1"),
        LEVEL_2("2");
        private final String specName;

        AUTHENTICATION_LEVEL(String spec) {
            specName = spec;
        }

        public String getAsString() {
            return specName;
        }
    }

    /**
     * This enum represents the different roles that an actor can have.
     */
    @Getter
    public enum ActorRole {
        CITIZEN("citizen"),
        PUBLIC_ADMINISTRATION("public-administration"),
        PRIVATE_BUSINESS("private-business"),
        INDETERMINATE("indeterminate");
        private final String name;

        ActorRole(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Evidences is an enum that represents different types of evidence in a system. Each evidence
     * type has a name, a fault event code, and a success event code. It also provides a method to
     * retrieve an evidence element based on its name.
     */
    @Getter
    public enum Evidences {
        SUBMISSION_ACCEPTANCE_REJECTION(
            "SubmissionAcceptanceRejection",
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_ACCEPTANCE,
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_REJECTION
        ),
        RELAY_REM_MD_ACCEPTANCE_REJECTION(
            "RelayREMMDAcceptanceRejection",
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_ACCEPTANCE,
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_REJECTION
        ),
        RELAY_REM_MD_FAILURE(
            "RelayREMMDFailure",
            "",
            "http:uri.etsi.org/02640/Event#DeliveryExpiration"
        ),
        DELIVERY_NON_DELIVERY_TO_RECIPIENT(
            "DeliveryNonDeliveryToRecipient",
            "http:uri.etsi.org/02640/Event#Delivery",
            "http:uri.etsi.org/02640/Event#NonDelivery"
        ),
        RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT(
            "RetrievalNonRetrievalByRecipient",
            "http:uri.etsi.org/02640/Event#Retrieval",
            "http:uri.etsi.org/02640/Event#RetrievalExpiration"
        ),
        ACCEPTANCE_REJECTION_BY_RECIPIENT(
            "AcceptanceRejectionByRecipient",
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_ACCEPTANCE,
            Constants.HTTP_URI_ETSI_ORG_02640_EVENT_REJECTION
        ),
        RECEIVED_BY_NON_REM_SYSTEM(
            "ReceivedByNonREMSystem",
            "",
            ""
        );
        private final String name;
        private final String faultEventCode;
        private final String successEventCode;

        Evidences(
            String name, String successEventCode, String faultEventCode) {
            this.name = name;
            this.faultEventCode = faultEventCode;
            this.successEventCode = successEventCode;
        }

        /**
         * Returns the Evidences element with the specified name.
         *
         * @param elementName the name of the element
         * @return the Evidences element with the specified name
         * @throws IllegalArgumentException if the element with the specified name is not found
         */
        public static Evidences valueOfElementName(String elementName) {
            if (SUBMISSION_ACCEPTANCE_REJECTION.getName().equals(elementName)) {
                return SUBMISSION_ACCEPTANCE_REJECTION;
            } else if (ACCEPTANCE_REJECTION_BY_RECIPIENT.getName().equals(
                elementName)) {
                return ACCEPTANCE_REJECTION_BY_RECIPIENT;
            } else if (DELIVERY_NON_DELIVERY_TO_RECIPIENT.getName().equals(
                elementName)) {
                return DELIVERY_NON_DELIVERY_TO_RECIPIENT;
            } else if (RECEIVED_BY_NON_REM_SYSTEM.getName().equals(elementName)) {
                return RECEIVED_BY_NON_REM_SYSTEM;
            } else if (RELAY_REM_MD_ACCEPTANCE_REJECTION.getName().equals(
                elementName)) {
                return RELAY_REM_MD_ACCEPTANCE_REJECTION;
            } else if (RELAY_REM_MD_FAILURE.getName().equals(elementName)) {
                return RELAY_REM_MD_FAILURE;
            } else if (RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT.getName().equals(
                elementName)) {
                return RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT;
            }
            throw new IllegalArgumentException(
                "wrong element name did not found the element: " + elementName
            );
        }

        private static class Constants {
            public static final String HTTP_URI_ETSI_ORG_02640_EVENT_ACCEPTANCE =
                "http:uri.etsi.org/02640/Event#Acceptance";
            public static final String HTTP_URI_ETSI_ORG_02640_EVENT_REJECTION =
                "http:uri.etsi.org/02640/Event#Rejection";
        }
    }

    /**
     * Enum class representing country codes in ISO 3116 format.
     */
    @Getter
    public enum COUNTRY_CODES {
        GERMANY("DE", "Germany"),
        AUSTRIA("AT", "Austria"),
        BELGIUM("BE", "Belgium"),
        CYPRUS("CY", "Cyprus"),
        CZECH_REPUBLIC("CZ", "Czech Republic"),
        DENMARK("DK", "Denmark"),
        ESTONIA("EE", "Estonia"),
        FINLAND("FI", "Finland"),
        FRANCE("FR", "France"),
        GREECE("GR", "Greece"),
        HUNGARY("HU", "Hungary"),
        IRELAND("IE", "Ireland"),
        ITALY("IT", "Italy"),
        LATVIA("LV", "Latvia"),
        LITHUANIA("LT", "Lithuania"),
        LUXEMBOURG("LU", "Luxembourg"),
        MALTA("MT", "Malta"),
        NETHERLANDS("NL", "Netherlands"),
        POLAND("PL", "Poland"),
        PORTUGAL("PT", "Portugal"),
        ROMANIA("RO", "Romania"),
        SLOVAKIA("SK", "Slovakia"),
        SLOVENIA("SI", "Slovenia"),
        SPAIN("ES", "Spain"),
        SWEDEN("SE", "Sweden"),
        UNITED_KINGDOM("GB", "United Kingdom");
        private final String code;
        private final String name;

        COUNTRY_CODES(String code, String name) {
            this.code = code;
            this.name = name;
        }

        /**
         * Returns the found Country code.
         *
         * @param country The code, the country name or the countryCode values to find the related
         *                enum value
         * @return the found object.
         */
        public static COUNTRY_CODES getCountryCode(String country) {
            for (COUNTRY_CODES compare : values()) {
                if (compare.getCode().equalsIgnoreCase(country)) {
                    return compare;
                }
                if (compare.getName().equalsIgnoreCase(country)) {
                    return compare;
                }
            }
            return valueOf(country);
        }
    }
}
