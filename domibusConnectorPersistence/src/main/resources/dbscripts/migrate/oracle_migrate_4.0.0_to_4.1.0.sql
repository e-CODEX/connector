-- *********************************************************************
-- Update Database Script - from domibusConnector 4.0 to 4.1
-- *********************************************************************
-- updates the connector database from an 4.0 connector version to 4.1

-- #################### 1/6 RENAME tables that need to be recreated ####################
-- #################### 2/6 CREATE tables, structural changes ####################
CREATE TABLE "DOMIBUS_CONNECTOR_PROPERTY"
(
    "PROPERTY_NAME"  VARCHAR2(512)  NOT NULL,
    "PROPERTY_VALUE" VARCHAR2(1024) NULL
);

CREATE TABLE "DOMIBUS_CONNECTOR_USER"
(
    "ID"                     NUMBER(10)          NOT NULL,
    "USERNAME"               VARCHAR2(50)        NOT NULL,
    "ROLE"                   VARCHAR2(50)        NOT NULL,
    "LOCKED"                 NUMBER(1) DEFAULT 0 NOT NULL,
    "NUMBER_OF_GRACE_LOGINS" NUMBER(2) DEFAULT 5 NOT NULL,
    "GRACE_LOGINS_USED"      NUMBER(2) DEFAULT 0 NOT NULL,
    "CREATED"                TIMESTAMP           NOT NULL
);

CREATE TABLE "DOMIBUS_CONNECTOR_USER_PWD"
(
    "ID"          NUMBER(10)          NOT NULL,
    "USER_ID"     NUMBER(10)          NOT NULL,
    "PASSWORD"    VARCHAR2(1024)      NOT NULL,
    "SALT"        VARCHAR2(512)       NOT NULL,
    "CURRENT_PWD" NUMBER(1) DEFAULT 0 NOT NULL,
    "INITIAL_PWD" NUMBER(1) DEFAULT 0 NOT NULL,
    "CREATED"     TIMESTAMP           NOT NULL
);

CREATE TABLE DC_DB_VERSION (TAG VARCHAR(255) PRIMARY KEY);

ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR MODIFY ERROR_MESSAGE VARCHAR(2048);

ALTER TABLE "DOMIBUS_CONNECTOR_EVIDENCE" ADD CONNECTOR_MESSAGE_ID VARCHAR2(255);

CREATE INDEX "IXFK_DC_EV_01"       ON "DOMIBUS_CONNECTOR_EVIDENCE" ("CONNECTOR_MESSAGE_ID");
CREATE INDEX "IXFK_DC_USER_PWD_01" ON "DOMIBUS_CONNECTOR_USER_PWD" ("USER_ID");

DECLARE
    DOMIBUS_WEBADMIN_PROPERTY       VARCHAR2(250):= 'DOMIBUS_WEBADMIN_PROPERTY';
    DOMIBUS_WEBADMIN_USER           VARCHAR2(250):= 'DOMIBUS_WEBADMIN_USER';
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE ' || DOMIBUS_WEBADMIN_PROPERTY || ' CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE ' || DOMIBUS_WEBADMIN_USER || ' CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN
        -- "table not found" exceptions are ignored, anything else is raised
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;

-- #################### 3/6 TRANSFER data ####################

-- create default values for party, action and service if they do not already exist
INSERT INTO DOMIBUS_CONNECTOR_SERVICE
SELECT 'n.a.', 'n.a.'
FROM dual -- because oracle
WHERE NOT EXISTS (SELECT NULL -- whatever not used by exists
                  FROM DOMIBUS_CONNECTOR_SERVICE
                  WHERE SERVICE = 'n.a.'
    );

-- see above
INSERT INTO DOMIBUS_CONNECTOR_ACTION
SELECT 'n.a.', 0
FROM dual -- because oracle
WHERE NOT EXISTS (SELECT NULL -- whatever not used by exists
                  FROM DOMIBUS_CONNECTOR_ACTION
                  WHERE ACTION = 'n.a.'
    );


-- see above
INSERT INTO DOMIBUS_CONNECTOR_PARTY
SELECT 'n.a.', 'n.a.', 'n.a.'
FROM dual -- because oracle
WHERE NOT EXISTS (SELECT NULL -- whatever not used by exists
                  FROM DOMIBUS_CONNECTOR_PARTY
                  WHERE PARTY_ID = 'n.a.'
    );

update DOMIBUS_CONNECTOR_MESSAGE_INFO set FROM_PARTY_ID='n.a.', FROM_PARTY_ROLE='n.a.' where FROM_PARTY_ID is null;

UPDATE domibus_connector_message SET connector_message_id='_migrate_' || SYS_GUID() where CONNECTOR_MESSAGE_ID is null;

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_PROPERTY.ID', 1000);

INSERT INTO DOMIBUS_CONNECTOR_USER (ID, USERNAME, ROLE, LOCKED, NUMBER_OF_GRACE_LOGINS, GRACE_LOGINS_USED, CREATED)
VALUES (1000, 'admin', 'ADMIN', 0, 5, 0, current_timestamp);
INSERT INTO DOMIBUS_CONNECTOR_USER (ID, USERNAME, ROLE, LOCKED, NUMBER_OF_GRACE_LOGINS, GRACE_LOGINS_USED, CREATED)
VALUES (1001, 'user', 'USER', 0, 5, 0, current_timestamp);

INSERT INTO DOMIBUS_CONNECTOR_USER_PWD (ID, USER_ID, PASSWORD, SALT, CURRENT_PWD, INITIAL_PWD, CREATED)
VALUES (1000, 1000,
        '2bf5e637d0d82a75ca43e3be85df2c23febffc0cc221f5e010937005df478a19b5eaab59fe7e4e97f6b43ba648c169effd432e19817f386987d058c239236306',
        '5b424031616564356639', 1, 1, current_timestamp);
INSERT INTO DOMIBUS_CONNECTOR_USER_PWD (ID, USER_ID, PASSWORD, SALT, CURRENT_PWD, INITIAL_PWD, CREATED)
VALUES (1001, 1001,
        '2bf5e637d0d82a75ca43e3be85df2c23febffc0cc221f5e010937005df478a19b5eaab59fe7e4e97f6b43ba648c169effd432e19817f386987d058c239236306',
        '5b424031616564356639', 1, 1, current_timestamp);

-- update the seq_store
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_USER.ID', 1002);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_USER_PWD.ID', 1002);

-- #################### 4/6 DELETE temporary tables, frees fk names ####################
-- #################### 5/6 ADD the constraints ####################

ALTER TABLE "DOMIBUS_CONNECTOR_PROPERTY"
    ADD CONSTRAINT "PK_DC_PROPERTY"
        PRIMARY KEY ("PROPERTY_NAME")
            USING INDEX;

ALTER TABLE "DOMIBUS_CONNECTOR_USER"
    ADD CONSTRAINT "PK_DC_USER"
        PRIMARY KEY ("ID")
            USING INDEX;

ALTER TABLE "DOMIBUS_CONNECTOR_USER_PWD"
    ADD CONSTRAINT "PK_DC_USER_PW"
        PRIMARY KEY ("ID")
            USING INDEX;

ALTER TABLE "DOMIBUS_CONNECTOR_USER_PWD"
    ADD CONSTRAINT "FK_DC_USER_PWD_01"
        FOREIGN KEY ("USER_ID") REFERENCES "DOMIBUS_CONNECTOR_USER" ("ID");

-- #################### 6/6 UPDATE Version ####################

INSERT INTO DC_DB_VERSION (TAG) VALUES ('V4.1.5');
