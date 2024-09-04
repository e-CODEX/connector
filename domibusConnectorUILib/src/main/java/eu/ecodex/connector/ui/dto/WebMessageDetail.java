/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.dto;

import java.util.LinkedList;
import lombok.Data;

/**
 * The WebMessageDetail class represents the detailed information of a web message. It contains the
 * service, action, sender, recipient, original sender, final recipient, and a list of errors.
 */
@SuppressWarnings("squid:S1135")
@Data
public class WebMessageDetail {
    /**
     * The Party class represents a party.
     */
    @Data
    public static class Party {
        private String partyId;
        private String partyIdType;
        private String role;

        /**
         * Constructs a new Party object with the specified partyId, partyIdType, and role.
         *
         * @param partyId     The party ID.
         * @param partyIdType The type of party ID.
         * @param role        The role of the party.
         */
        public Party(String partyId, String partyIdType, String role) {
            super();
            this.partyId = partyId;
            this.partyIdType = partyIdType;
            this.role = role;
        }

        @Override
        public String toString() {
            return partyId + "(" + partyIdType + ")";
        }

        public String getPartyString() {
            return partyId + "(" + partyIdType + ")";
        }

        /**
         * Sets the party string of the Party object.
         *
         * @param partyString The party string to be set.
         */
        public void setPartyString(String partyString) {
            if (partyString.indexOf("(") >= 1 && partyString.indexOf(")") >= 1) {
                this.partyId = partyString.substring(0, partyString.indexOf("("));
                this.partyIdType =
                    partyString.substring(partyString.indexOf("(") + 1, partyString.indexOf(")"));
            } else {
                this.partyId = partyString;
            }
        }
    }

    /**
     * The Service class represents a service.
     */
    @Data
    public static class Service {
        private String service;
        private String serviceType;

        /**
         * Constructs a new Service object with the specified service and service type.
         *
         * @param service     The service value.
         * @param serviceType The service type value.
         */
        public Service(String service, String serviceType) {
            super();
            this.service = service;
            this.serviceType = serviceType;
        }

        /**
         * Constructs a new Service object with the specified service value.
         *
         * @param service The service value.
         */
        public Service(String service) {
            super();
            this.service = service;
        }

        @Override
        public String toString() {
            return "Service [service=" + service + ", serviceType=" + serviceType + "]";
        }

        public String getServiceString() {
            return serviceType != null ? service + "(" + serviceType + ")" : service;
        }
    }

    /**
     * The Action class represents an action.
     */
    @Data
    public static class Action {
        private String action;

        /**
         * The Action class represents an action.
         *
         * @param action - the action value
         */
        public Action(String action) {
            super();
            this.action = action;
        }

        @Override
        public String toString() {
            return "Action [action=" + action + "]";
        }
    }

    private Service service;
    private Action action;
    private Party from;
    private Party to;
    private String originalSender;
    private String finalRecipient;
    private LinkedList<WebMessageError> errors = new LinkedList<>();

    @Override
    public String toString() {
        return "WebMessageDetail [service=" + service + ", action=" + action + ", from=" + from
            + ", to=" + to
            + ", originalSender=" + originalSender + ", finalRecipient=" + finalRecipient + "]";
    }

    public void setServiceString(String service) {
        // TODO see why this method body is empty
    }
}
