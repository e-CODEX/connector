-- *********************************************************************
-- Update Database Script - from domibusConnector 6.0.2 to 6.1.1 (MySQL)
-- *********************************************************************

SET names utf8;

-- ###################### 1/2 ALTER COLUMNS ######################

ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT MODIFY COLUMN CHECKSUM LONGTEXT;
ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT MODIFY COLUMN CONTENT LONGBLOB;
ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT MODIFY COLUMN PAYLOAD_DESCRIPTION LONGTEXT;
ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT MODIFY COLUMN CREATED DATETIME;
ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT MODIFY COLUMN DELETED DATETIME;

-- #################### 2/2 UPDATE DB Version ####################

UPDATE DC_DB_VERSION SET TAG='V6.1.1' WHERE TAG='V6.0.2';
