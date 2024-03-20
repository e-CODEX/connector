-- *********************************************************************
-- Update Database Script - from domibusConnector 4.2 to 4.3
-- *********************************************************************

SET FOREIGN_KEY_CHECKS=0;
-- requires MySQL >= 5.6.6, default since MySQL 8.0.2
-- SET EXPLICIT_DEFAULTS_FOR_TIMESTAMP = ON;
-- fixes UUID bug: https://bugs.mysql.com/bug.php?id=101820es UUID bug
-- also see: https://stackoverflow.com/questions/36296558/mysql-generating-duplicate-uuid
SET names utf8;

-- #################### 1/6 RENAME tables that need to be recreated ####################

rename table DOMIBUS_CONNECTOR_MESSAGE_INFO to bkp_dc_msg_info;
rename table DOMIBUS_CONNECTOR_SERVICE to BKP_DC_SERVICE;

-- #################### 2/6 CREATE tables, structural changes ####################

-- delayed to 4.4
-- alter table DOMIBUS_CONNECTOR_ACTION drop column PDF_REQUIRED;

CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
(
    ID               BIGINT NOT NULL,
    MESSAGE_ID       BIGINT NOT NULL,
    FK_FROM_PARTY_ID BIGINT,
    FK_TO_PARTY_ID   BIGINT,
    ORIGINAL_SENDER  VARCHAR(2048),
    FINAL_RECIPIENT  VARCHAR(2048),
    FK_SERVICE       BIGINT,
    FK_ACTION        BIGINT,
    CREATED          TIMESTAMP(6) NOT NULL,
    UPDATED          TIMESTAMP(6) NOT NULL
);

alter table DC_TRANSPORT_STEP add TRANSPORTED_MESSAGE LONGTEXT;
alter table DC_TRANSPORT_STEP add FINAL_STATE_REACHED TIMESTAMP(6);

alter table DOMIBUS_CONNECTOR_MSG_CONT
    add CONNECTOR_MESSAGE_ID VARCHAR(512);

alter table DOMIBUS_CONNECTOR_BIGDATA
    add CONNECTOR_MESSAGE_ID VARCHAR(255) null;

CREATE TABLE DOMIBUS_CONNECTOR_SERVICE
(
    ID           BIGINT       NOT NULL,
    FK_PMODE_SET BIGINT       NOT NULL,
    SERVICE      VARCHAR(255) NOT NULL,
    SERVICE_TYPE VARCHAR(255)
);


DELIMITER $$
DROP PROCEDURE IF EXISTS DropFK $$
CREATE PROCEDURE DropFK (
    IN parm_table_name VARCHAR(100),
    IN parm_key_name VARCHAR(100)
)
BEGIN
    -- Verify the foreign key exists
    IF EXISTS (SELECT NULL FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = DATABASE() AND CONSTRAINT_NAME = parm_key_name) THEN
-- Turn the parameters into local variables
        set @ParmTable = parm_table_name ;
        set @ParmKey = parm_key_name ;
-- Create the full statement to execute
        set @StatementToExecute = concat('ALTER TABLE ',@ParmTable,' DROP FOREIGN KEY ',@ParmKey);
-- Prepare and execute the statement that was built
        prepare DynamicStatement from @StatementToExecute ;
        execute DynamicStatement ;
-- Cleanup the prepared statement
        deallocate prepare DynamicStatement ;
    END IF;
END $$
DELIMITER ;
-- if the foreign key  FK_MESSAGESTEP_MESSAGE exists in table DC_TRANSPORT_STEP, it will be dropped
-- FK_MESSAGESTEP_MESSAGE is very likely already deleted, because the connector does not work, if it still exists.
-- To prevent errors I had to wrap the deletion in a procedure
call DropFK('DC_TRANSPORT_STEP', 'FK_MESSAGESTEP_MESSAGE');

alter table DOMIBUS_CONNECTOR_EVIDENCE modify UPDATED TIMESTAMP(6) not null;

alter table DOMIBUS_CONNECTOR_MESSAGE modify UPDATED TIMESTAMP(6) not null;
alter table DOMIBUS_CONNECTOR_MESSAGE modify CREATED TIMESTAMP(6) not null;

alter table DOMIBUS_CONNECTOR_MSG_CONT modify MESSAGE_ID BIGINT not null;
alter table DOMIBUS_CONNECTOR_MSG_CONT modify CREATED TIMESTAMP(6) not null;

alter table DOMIBUS_CONNECTOR_BIGDATA modify CREATED TIMESTAMP(6) not null;

alter table DC_PMODE_SET modify CREATED TIMESTAMP(6) not null;
alter table DC_PMODE_SET modify ACTIVE BOOLEAN not null;

-- #################### 3/6 TRANSFER & UPDATE data ####################

# unprivileged mariadb user can use this, but this does not work for mysql
DELIMITER $$
DROP PROCEDURE IF EXISTS UpdateSeqStore $$
CREATE PROCEDURE UpdateSeqStore ()
BEGIN
    IF EXISTS (select SEQ_NAME
               from DOMIBUS_CONNECTOR_SEQ_STORE
               where SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGE.ID') THEN
        IF EXISTS(select SEQ_NAME
                  from DOMIBUS_CONNECTOR_SEQ_STORE
                  where SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGES.ID') THEN

            delete from DOMIBUS_CONNECTOR_SEQ_STORE where SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGES.ID';
        END IF;
    ELSE
        update DOMIBUS_CONNECTOR_SEQ_STORE
        set SEQ_NAME =   'DOMIBUS_CONNECTOR_MESSAGE.ID'
        where SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGES.ID';
    END IF;

    IF EXISTS (select SEQ_NAME
               from DOMIBUS_CONNECTOR_SEQ_STORE
               where SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCE.ID') THEN
        IF EXISTS(select SEQ_NAME
                  from DOMIBUS_CONNECTOR_SEQ_STORE
                  where SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCES.ID') THEN

            delete from DOMIBUS_CONNECTOR_SEQ_STORE where SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCES.ID';
        END IF;
    ELSE
        update DOMIBUS_CONNECTOR_SEQ_STORE
        set SEQ_NAME =   'DOMIBUS_CONNECTOR_EVIDENCE.ID'
        where SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCES.ID';
    END IF;
END
$$
DELIMITER ;

call UpdateSeqStore();

insert into DOMIBUS_CONNECTOR_MESSAGE_INFO
(MESSAGE_ID,
ID,
FK_FROM_PARTY_ID,
FK_TO_PARTY_ID,
ORIGINAL_SENDER,
FINAL_RECIPIENT,
FK_SERVICE,
FK_ACTION,
CREATED,
UPDATED)
select MESSAGE_ID,
       ID,
       FK_FROM_PARTY_ID,
       FK_TO_PARTY_ID,
       ORIGINAL_SENDER,
       FINAL_RECIPIENT,
       FK_SERVICE,
       FK_ACTION,
       CREATED,
       UPDATED
from bkp_dc_msg_info;

insert into DOMIBUS_CONNECTOR_SERVICE
(
    ID,
    FK_PMODE_SET,
    SERVICE,
    SERVICE_TYPE
)
select ID, FK_PMODE_SET, SERVICE, SERVICE_TYPE from BKP_DC_SERVICE;

-- update timestamps

UPDATE DOMIBUS_CONNECTOR_EVIDENCE e
    INNER JOIN DOMIBUS_CONNECTOR_MESSAGE m ON m.id = e.MESSAGE_ID
SET e.DELIVERED_GW=e.updated
WHERE e.DELIVERED_GW is null
  and e.TYPE in ('DELIVERY', 'RETRIVAL', 'RELAY_REMMD_ACCEPTANCE')
  and e.updated > str_to_date('01.06.2020', '%d.%m.%Y')
  and m.DIRECTION_SOURCE = 'GATEWAY'
  and m.DIRECTION_TARGET = 'BACKEND'
  and m.confirmed is not null;

UPDATE DOMIBUS_CONNECTOR_EVIDENCE e
    INNER JOIN DOMIBUS_CONNECTOR_MESSAGE m ON m.id = e.MESSAGE_ID
SET e.DELIVERED_NAT=e.updated
WHERE e.DELIVERED_NAT is null
  and e.TYPE in ('DELIVERY', 'RETRIVAL', 'RELAY_REMMD_ACCEPTANCE')
  and e.updated > str_to_date('01.06.2020', '%d.%m.%Y')
  and m.DIRECTION_SOURCE = 'BACKEND'
  and m.DIRECTION_TARGET = 'GATEWAY'
  and m.confirmed is not null;

-- #################### 4/6 DELETE temporary tables, frees fk names ####################

drop table bkp_dc_msg_info cascade;
drop table BKP_DC_SERVICE cascade;

-- #################### 5/6 ADD the constraints ####################

ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD CONSTRAINT PK_DC_SERVICE PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD CONSTRAINT FK_SERVICE_PMODE_SET_ID FOREIGN KEY (FK_PMODE_SET) REFERENCES DC_PMODE_SET(ID);
ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA ADD CONSTRAINT FK_DC_BIGDATA_MESSAGE_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID);

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT PK_DC_MSG_INFO PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_F_PARTY FOREIGN KEY (FK_FROM_PARTY_ID) REFERENCES DOMIBUS_CONNECTOR_PARTY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_T_PARTY FOREIGN KEY (FK_TO_PARTY_ID) REFERENCES DOMIBUS_CONNECTOR_PARTY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_SERVICE FOREIGN KEY (FK_SERVICE) REFERENCES DOMIBUS_CONNECTOR_SERVICE (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_ACTION FOREIGN KEY (FK_ACTION) REFERENCES DOMIBUS_CONNECTOR_ACTION (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_I FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID);

-- #################### 6/6 UPDATE Version ####################

SET FOREIGN_KEY_CHECKS = 1;

update DC_DB_VERSION set TAG='V4.3';