package eu.domibus.connector.ui.dto;

import java.util.LinkedList;


public class WebMessageDetail {
    private Service service;
    private Action action;
    private Party from;
    private Party to;
    private String originalSender;
    private String finalRecipient;
    private final LinkedList<WebMessageError> errors = new LinkedList<WebMessageError>();

    @Override
    public String toString() {
        return "WebMessageDetail [service=" + service + ", action=" + action + ", from=" + from + ", to=" + to
                + ", originalSender=" + originalSender + ", finalRecipient=" + finalRecipient + "]";
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }

    public String getFinalRecipient() {
        return finalRecipient;
    }

    public void setFinalRecipient(String finalRecipient) {
        this.finalRecipient = finalRecipient;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Party getFrom() {
        return from;
    }

    public void setFrom(Party from) {
        this.from = from;
    }

    public Party getTo() {
        return to;
    }

    public void setTo(Party to) {
        this.to = to;
    }

    public LinkedList<WebMessageError> getErrors() {
        return errors;
    }

    public void setServiceString(String service) {

    }

    public void setActionString(String action) {

    }

    public void setFromString(String from) {

    }

    public void setToString(String to) {

    }

    public static class Party {
        private String partyId;
        private String partyIdType;

        private String role;
        //		/**
        //		 * @param partyId
        //		 * @param partyIdType
        //		 */
        //		public Party(String partyId, String partyIdType) {
        //			super();
        //			this.partyId = partyId;
        //			this.partyIdType = partyIdType;
        //		}
        //		/**
        //		 * @param partyId
        //		 */
        //		public Party(String partyId) {
        //			super();
        //			this.partyId = partyId;
        //		}

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

        public String getPartyId() {
            return partyId;
        }

        public void setPartyId(String partyId) {
            this.partyId = partyId;
        }

        public String getPartyIdType() {
            return partyIdType;
        }

        public void setPartyIdType(String partyIdType) {
            this.partyIdType = partyIdType;
        }

        public String getPartyString() {
            return partyId + "(" + partyIdType + ")";
        }

        public void setPartyString(String partyString) {
            if (partyString.indexOf("(") > 0 && partyString.indexOf(")") > 0) {
                this.partyId = partyString.substring(0, partyString.indexOf("("));
                this.partyIdType = partyString.substring(partyString.indexOf("(") + 1, partyString.indexOf(")"));
            } else {
                this.partyId = partyString;
            }
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class Service {
        private String service;
        private String serviceType;

        /**
         * @param service
         * @param serviceType
         */
        public Service(String service, String serviceType) {
            super();
            this.service = service;
            this.serviceType = serviceType;
        }

        /**
         * @param service
         */
        public Service(String service) {
            super();
            this.service = service;
        }

        @Override
        public String toString() {
            return "Service [service=" + service + ", serviceType=" + serviceType + "]";
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public String getServiceString() {
            return serviceType != null ? service + "(" + serviceType + ")" : service;
        }
    }

    public static class Action {
        private String action;

        /**
         * @param action
         */
        public Action(String action) {
            super();
            this.action = action;
        }

        @Override
        public String toString() {
            return "Action [action=" + action + "]";
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public void setActionString(String action) {

        }
    }
}
