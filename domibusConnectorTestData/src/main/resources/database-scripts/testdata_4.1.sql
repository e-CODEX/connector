--liquibase formatted sql
--
--changeset initialPersistenceTestdata_1
----------------------- Values for DOMIBUS_CONNECTOR_BACKEND_INFO ------------------------------

-- create 2 clients
INSERT INTO DOMIBUS_CONNECTOR_BACKEND_INFO (ID, BACKEND_NAME, BACKEND_KEY_ALIAS, BACKEND_KEY_PASS, BACKEND_SERVICE_TYPE, BACKEND_DESCRIPTION, BACKEND_PUSH_ADDRESS)
VALUES (1, 'bob', 'bob', '', 'EPO', 'a epo backend', null);

INSERT INTO DOMIBUS_CONNECTOR_BACKEND_INFO (ID, BACKEND_NAME, BACKEND_KEY_ALIAS, BACKEND_KEY_PASS, BACKEND_DESCRIPTION, BACKEND_PUSH_ADDRESS, BACKEND_ENABLED, BACKEND_DEFAULT)
VALUES (1, 'CN=alice', 'alice', '', 'a alice backend', 'http://BACKEND_ALICE:8080//services/domibusConnectorDeliveryWebservice', true, true);


-- import test gateways
INSERT INTO DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE, PARTY_ID_TYPE) VALUES
  ('gw1', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE, PARTY_ID_TYPE) VALUES
  ('gw2', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE, PARTY_ID_TYPE) VALUES
  ('gw3', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE, PARTY_ID_TYPE) VALUES
  ('gw4', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');

INSERT INTO DOMIBUS_CONNECTOR_SERVICE (SERVICE, SERVICE_TYPE) VALUES ('Connector-TEST', 'urn:e-codex:services:');
INSERT INTO DOMIBUS_CONNECTOR_SERVICE (SERVICE, SERVICE_TYPE) VALUES  ('System-TEST', 'urn:e-codex:services:');

INSERT INTO DOMIBUS_CONNECTOR_ACTION ("ACTION", PDF_REQUIRED)
VALUES ('SubmissionAcceptanceRejection', false);

INSERT INTO DOMIBUS_CONNECTOR_ACTION ("ACTION", PDF_REQUIRED)
VALUES ('RelayREMMDAcceptanceRejection', false);

INSERT INTO DOMIBUS_CONNECTOR_ACTION ("ACTION", PDF_REQUIRED)
VALUES ('DeliveryNonDeliveryToRecipient', false);

INSERT INTO DOMIBUS_CONNECTOR_ACTION ("ACTION", PDF_REQUIRED)
VALUES ('RetrievalNonRetrievalToRecipient', false);

INSERT INTO DOMIBUS_CONNECTOR_ACTION ("ACTION", PDF_REQUIRED)
VALUES ('System-TEST', false);


