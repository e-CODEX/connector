-- *********************************************************************
-- Update Database Script - from domibusConnector 4.3 to 4.4
-- *********************************************************************

SET FOREIGN_KEY_CHECKS=0;
-- requires MySQL >= 5.6.6, default since MySQL 8.0.2
SET EXPLICIT_DEFAULTS_FOR_TIMESTAMP = ON;
-- fixes UUID bug: https://bugs.mysql.com/bug.php?id=101820es UUID bug
-- also see: https://stackoverflow.com/questions/36296558/mysql-generating-duplicate-uuid
SET names utf8;

-- #################### 1/6 RENAME tables that need to be recreated ####################
RENAME TABLE DOMIBUS_CONNECTOR_BIGDATA to bkp_dc_bigdata;

-- #################### 2/6 CREATE tables, structural changes ####################

create table DOMIBUS_CONNECTOR_BIGDATA
(
    ID                   bigint       not null
        primary key,
    CHECKSUM             longtext     null,
    CONNECTOR_MESSAGE_ID varchar(255) null,
    CONTENT              longblob     null,
    CREATED              datetime(6)  not null,
    LAST_ACCESS          datetime(6)  null,
    MIMETYPE             varchar(255) null,
    NAME                 longtext     null
);

alter table DOMIBUS_CONNECTOR_ACTION drop column PDF_REQUIRED;

ALTER TABLE DC_PMODE_SET ADD COLUMN PMODES LONGBLOB;
ALTER TABLE DC_PMODE_SET ADD COLUMN FK_CONNECTORSTORE BIGINT;

ALTER TABLE DOMIBUS_CONNECTOR_PARTY ADD COLUMN ROLE_TYPE VARCHAR(50);

CREATE TABLE DC_KEYSTORE
(
	ID       bigint         NOT NULL,
	UUID     VARCHAR(255)   NOT NULL,
	KEYSTORE longblob       NOT NULL,
	PASSWORD VARCHAR(1024),
	UPLOADED datetime(6)    NOT NULL,
	DESCRIPTION VARCHAR(512),
	TYPE VARCHAR(50),
	CONSTRAINT PK_DC_KEYSTORE PRIMARY KEY (ID ASC)
)
;


ALTER TABLE DC_KEYSTORE 
 ADD CONSTRAINT UQ_DC_KEYSTORE UNIQUE (UUID ASC)
;

insert into DOMIBUS_CONNECTOR_BIGDATA
select B.ID as ID,
       B.CHECKSUM as CHECKSUM,
       B.CREATED as CREATED,
       null as CONNECTOR_MESSAGE_ID,
       B.CONTENT as CONTENT,
       B.LAST_ACCESS as LAST_ACCESS,
       B.NAME as NAME,
       B.MIMETYPE as MIMETYPE
from bkp_dc_bigdata B;


ALTER TABLE DC_TRANSPORT_STEP_STATUS MODIFY created DATETIME(6);

-- #################### 3/6 TRANSFER & UPDATE data ####################

-- Set rejected timestamp to all messages not rejected or confirmed and older than 5 days.
-- Number of days may be changed if the 5 at the end of the query is changed.
UPDATE DOMIBUS_CONNECTOR_MESSAGE SET REJECTED = now() WHERE REJECTED IS NULL AND CONFIRMED IS NULL AND CONNECTOR_MESSAGE_ID IS NULL
AND CREATED < DATE_ADD(now(), INTERVAL -5 DAY);

-- Set a CONNECTOR_MESSAGE_ID to every message that does not have one yet by using the technical ID.
UPDATE DOMIBUS_CONNECTOR_MESSAGE SET CONNECTOR_MESSAGE_ID=ID WHERE CONNECTOR_MESSAGE_ID is null;

-- #################### 4/6 DELETE temporary tables, frees fk names ####################

drop table bkp_dc_bigdata cascade;

-- #################### 5/6 ADD the constraints ####################

ALTER TABLE DC_PMODE_SET 
 ADD CONSTRAINT FK_DC_PMODE_SET_02
	FOREIGN KEY (FK_CONNECTORSTORE) REFERENCES DC_KEYSTORE (ID) ON DELETE No Action ON UPDATE No Action
;

-- #################### 6/6 UPDATE Version ####################

update DC_DB_VERSION set TAG='V4.4';



SET FOREIGN_KEY_CHECKS = 1;