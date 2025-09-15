-- *********************************************************************
-- Update Database Script - from domibusConnector 4.4.x to 6.0.2 (PostgreSQL)
-- *********************************************************************

-- SET client_encoding = 'UTF8';

-- #################### 1/2 DROP INDEXES ####################

-- DROP BACKEND_MESSAGE_ID INDEX
DROP INDEX IF EXISTS UK_81o66ln4txujh8p62a6g6lqx9;

-- DROP EBMS_MESSAGE_ID INDEX
DROP INDEX IF EXISTS UK_e71rh4n71m4mpgcokhengr592;

-- #################### 2/2 UPDATE DB Version ####################

UPDATE DC_DB_VERSION SET TAG = 'V6.0.2' WHERE TAG = 'V4.4';
