-- *********************************************************************
-- Update Database Script - from domibusConnector 4.4.x to 6.0.2 (MariaDB)
-- *********************************************************************

SET names utf8;

-- #################### 1/2 DROP INDEXES ####################

-- DROP BACKEND_MESSAGE_ID INDEX
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE DROP INDEX UK_81o66ln4txujh8p62a6g6lqx9;
-- DROP EBMS_MESSAGE_ID INDEX
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE DROP INDEX UK_e71rh4n71m4mpgcokhengr592;

-- #################### 2/2 UPDATE DB Version ####################

UPDATE DC_DB_VERSION SET TAG='V6.0.2' WHERE TAG='V4.4';
