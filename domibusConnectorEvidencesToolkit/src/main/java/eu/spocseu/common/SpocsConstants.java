package eu.spocseu.common;

import javax.xml.namespace.QName;


public class SpocsConstants {
    public static QName SPOCS_SERVICE_QNAME = new QName("http://spocsinterconnect.gateway.eu", "GatewayService");

    public enum E_ADDRESS_SCHEMES {
        RFC5322ADDRESS("RFC5322-Address"),
        BUSDOX("busdox-actorid-upis");
        private String specName;

        E_ADDRESS_SCHEMES(String spec) {
            specName = spec;
        }

        public String getAsString() {
            return specName;
        }
    }

    public enum AUTHENTICATION_LEVEL {
        LEVEL_1("1"),
        LEVEL_2("2");
        private String specName;

        AUTHENTICATION_LEVEL(String spec) {
            specName = spec;
        }

        public String getAsString() {
            return specName;
        }
    }

    public enum ActorRole {
        CITIZEN("citizen"),
        PUBLIC_ADMINISTRATION("public-administration"),
        PRIVATE_BUSINESS("private-business"),
        INDETERMINATE("indeterminate");

        private String name;

        ActorRole(String _name) {
            name = _name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Evidences {
        SUBMISSION_ACCEPTANCE_REJECTION(
                "SubmissionAcceptanceRejection",
                "http:uri.etsi.org/02640/Event#Acceptance",
                "http:uri.etsi.org/02640/Event#Rejection"
        ),

        RELAY_REM_MD_ACCEPTANCE_REJECTION(
                "RelayREMMDAcceptanceRejection",
                "http:uri.etsi.org/02640/Event#Acceptance",
                "http:uri.etsi.org/02640/Event#Rejection"
        ),

        RELAY_REM_MD_FAILURE("RelayREMMDFailure", "", "http:uri.etsi.org/02640/Event#DeliveryExpiration"),

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
                "http:uri.etsi.org/02640/Event#Acceptance",
                "http:uri.etsi.org/02640/Event#Rejection"
        ),

        RECEIVED_BY_NON_REM_SYSTEM("ReceivedByNonREMSystem", "", "");

        private String name;
        private String faultEventCode;
        private String successEventCode;

        Evidences(String _name, String _successEventCode, String _faultEventCode) {
            name = _name;
            faultEventCode = _faultEventCode;
            successEventCode = _successEventCode;
        }

        public static Evidences valueOfElementName(String elementName) {
            if (SUBMISSION_ACCEPTANCE_REJECTION.getName().equals(elementName)) {
                return SUBMISSION_ACCEPTANCE_REJECTION;
            } else if (ACCEPTANCE_REJECTION_BY_RECIPIENT.getName().equals(elementName)) {
                return ACCEPTANCE_REJECTION_BY_RECIPIENT;
            } else if (DELIVERY_NON_DELIVERY_TO_RECIPIENT.getName().equals(elementName)) {
                return DELIVERY_NON_DELIVERY_TO_RECIPIENT;
            } else if (RECEIVED_BY_NON_REM_SYSTEM.getName().equals(elementName)) {
                return RECEIVED_BY_NON_REM_SYSTEM;
            } else if (RELAY_REM_MD_ACCEPTANCE_REJECTION.getName().equals(elementName)) {
                return RELAY_REM_MD_ACCEPTANCE_REJECTION;
            } else if (RELAY_REM_MD_FAILURE.getName().equals(elementName)) {
                return RELAY_REM_MD_FAILURE;
            } else if (RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT.getName().equals(elementName)) {
                return RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT;
            }
            throw new IllegalArgumentException("wrong elemen name did not found the element: " + elementName);
        }

        public String getFaultEventCode() {
            return faultEventCode;
        }

        public String getSuccessEventCode() {
            return successEventCode;
        }

        public String getName() {
            return name;
        }
    }

    public enum COUNTRY_CODES {
        GERMANY("DE", "Germany"),
        AUSTRIA("AT", "Austria"),
        BELGIUM("BE", "Belgium"),
        CRYPRUS("CY", "Cyprus"),
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
        private String code;
        private String name;

        COUNTRY_CODES(String _code, String _name) {
            code = _code;
            name = _name;
        }

        /**
         * Returns the found Country code.
         *
         * @param country The code, the country name or the countryCode values to
         *                find the related enum value
         * @return the found object.
         */
        public static COUNTRY_CODES getCountryCode(String country) {
            for (COUNTRY_CODES compare : values()) {
                if (compare.getCode().equalsIgnoreCase(country)) return compare;
                if (compare.getName().equalsIgnoreCase(country)) return compare;
            }
            return valueOf(country);
        }

        /**
         * Returns the country code in ISO 3116 format
         */
        public String getCode() {
            return code;
        }

        /**
         * @return The country name
         */
        public String getName() {
            return name;
        }
    }
}
