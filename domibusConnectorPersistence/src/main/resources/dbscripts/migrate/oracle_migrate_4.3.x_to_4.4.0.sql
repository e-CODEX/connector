-- *********************************************************************
-- Update Database Script - from domibusConnector 4.3 to 4.4
-- *********************************************************************
-- create new entity DC_KEYSTORE
-- extend entity DC_PMODE_SET by PMODES and FK_CONNECTORSTORE
--

-- #################### 1/6 RENAME tables that need to be recreated ####################


-- #################### 2/6 CREATE tables, structural changes ####################

alter table DOMIBUS_CONNECTOR_ACTION drop column PDF_REQUIRED;

ALTER TABLE DC_PMODE_SET ADD "PMODES" BLOB;
ALTER TABLE DC_PMODE_SET ADD "FK_CONNECTORSTORE" NUMBER(10);

ALTER TABLE DOMIBUS_CONNECTOR_PARTY ADD "ROLE_TYPE" VARCHAR2(50 CHAR);

CREATE TABLE  "DC_KEYSTORE"
(
	"ID" NUMBER(10) NOT NULL,
	"UUID" VARCHAR2(255 CHAR) NOT NULL,
	"KEYSTORE" BLOB NOT NULL,
	"PASSWORD" VARCHAR2(1024 BYTE),
	"UPLOADED" TIMESTAMP NOT NULL,
	"DESCRIPTION" VARCHAR2(512 CHAR),
	"TYPE" VARCHAR2(50 CHAR)
)
;

ALTER TABLE  "DC_KEYSTORE" 
 ADD CONSTRAINT "PK_DC_KEYSTORE"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DC_KEYSTORE" 
 ADD CONSTRAINT "UQ_DC_KEYSTORE" UNIQUE ("UUID") 
 USING INDEX
;

-- if not done with last upgrade 4.2.x to 4.3.x
-- REMOVE FK and replace with CONNECTOR_MESSAGE_ID
ALTER TABLE "DOMIBUS_CONNECTOR_BIGDATA"
ADD "CONNECTOR_MESSAGE_ID" VARCHAR2(255 CHAR);

ALTER TABLE "DOMIBUS_CONNECTOR_BIGDATA"
    DROP COLUMN "MESSAGE_ID";

-- #################### 3/6 TRANSFER & UPDATE data ####################

-- Set rejected timestamp to all messages not rejected or confirmed and older than 5 days.
-- Number of days may be changed if the 5 at the end of the query is changed.
UPDATE DOMIBUS_CONNECTOR_MESSAGE SET REJECTED = sysdate WHERE REJECTED IS NULL AND CONFIRMED IS NULL AND CONNECTOR_MESSAGE_ID IS NULL 
AND CREATED < sysdate - 5;

-- Set a CONNECTOR_MESSAGE_ID to every message that does not have one yet by using the technical ID.
UPDATE DOMIBUS_CONNECTOR_MESSAGE SET CONNECTOR_MESSAGE_ID=ID WHERE CONNECTOR_MESSAGE_ID is null;

-- #################### 4/6 DELETE temporary tables, frees fk names ####################


-- #################### 5/6 ADD the constraints ####################


ALTER TABLE  "DC_PMODE_SET" 
 ADD CONSTRAINT "FK_DC_PMODE_SET_02"
	FOREIGN KEY ("FK_CONNECTORSTORE") REFERENCES  "DC_KEYSTORE" ("ID")
;

-- #################### 6/6 UPDATE Version ####################

update DC_DB_VERSION set TAG='V4.4';

