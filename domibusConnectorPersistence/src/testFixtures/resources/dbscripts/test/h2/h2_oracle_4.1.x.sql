
CREATE TABLE DC_DB_VERSION (TAG VARCHAR(255) PRIMARY KEY);
INSERT INTO DC_DB_VERSION (TAG) VALUES ('V4.1.5');

--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_ACTION
--------------------------------------------------------

CREATE TABLE DOMIBUS_CONNECTOR_ACTION
(
   ACTION VARCHAR2(255 CHAR),
	PDF_REQUIRED NUMBER(1,0)
);
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_BACKEND_INFO
--------------------------------------------------------

CREATE TABLE DOMIBUS_CONNECTOR_BACKEND_INFO
(
    ID NUMBER(10,0),
	BACKEND_NAME VARCHAR2(255 CHAR),
	BACKEND_KEY_ALIAS VARCHAR2(255 CHAR),
	BACKEND_KEY_PASS VARCHAR2(255 CHAR),
	BACKEND_SERVICE_TYPE VARCHAR2(255 CHAR),
	BACKEND_ENABLED NUMBER(1,0) DEFAULT 1,
	BACKEND_DEFAULT NUMBER(1,0) DEFAULT 0,
	BACKEND_DESCRIPTION CLOB,
	BACKEND_PUSH_ADDRESS CLOB
);
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_BACK_2_S
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_BACK_2_S
   (	DOMIBUS_CONNECTOR_SERVICE_ID VARCHAR2(255 CHAR),
	DOMIBUS_CONNECTOR_BACKEND_ID NUMBER(10,0)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_BIGDATA
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_BIGDATA
   (	ID VARCHAR2(255 CHAR),
	CHECKSUM CLOB,
	CREATED TIMESTAMP (6),
	MESSAGE_ID NUMBER(10,0),
	LAST_ACCESS TIMESTAMP (6),
	NAME CLOB,
	CONTENT BLOB,
	MIMETYPE VARCHAR2(255 CHAR)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_CONT_TYPE
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_CONT_TYPE
   (	MESSAGE_CONTENT_TYPE VARCHAR2(255 CHAR)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_EVIDENCE
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_EVIDENCE
   (	ID NUMBER(10,0),
	MESSAGE_ID NUMBER(10,0),
	TYPE VARCHAR2(255 CHAR),
	EVIDENCE CLOB,
	DELIVERED_NAT TIMESTAMP (6),
	DELIVERED_GW TIMESTAMP (6),
	UPDATED TIMESTAMP (6),
	CONNECTOR_MESSAGE_ID VARCHAR2(255)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_MESSAGE
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE
   (	ID NUMBER(10,0),
	EBMS_MESSAGE_ID VARCHAR2(255 CHAR),
	BACKEND_MESSAGE_ID VARCHAR2(255 CHAR),
	BACKEND_NAME VARCHAR2(255 CHAR),
	CONNECTOR_MESSAGE_ID VARCHAR2(255 CHAR),
	CONVERSATION_ID VARCHAR2(255 CHAR),
	DIRECTION VARCHAR2(10),
	HASH_VALUE CLOB,
	CONFIRMED TIMESTAMP (6),
	REJECTED TIMESTAMP (6),
	DELIVERED_BACKEND TIMESTAMP (6),
	DELIVERED_GW TIMESTAMP (6),
	UPDATED TIMESTAMP (6),
	CREATED TIMESTAMP (6)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_MESSAGE_INFO
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
   (	ID NUMBER(10,0),
	MESSAGE_ID NUMBER(10,0),
	CONNECTOR_MESSAGE_ID VARCHAR2(255 CHAR),
	FROM_PARTY_ID VARCHAR2(255 CHAR),
	FROM_PARTY_ROLE VARCHAR2(255 CHAR),
	TO_PARTY_ID VARCHAR2(255 CHAR),
	TO_PARTY_ROLE VARCHAR2(255 CHAR),
	ORIGINAL_SENDER VARCHAR2(255 CHAR),
	FINAL_RECIPIENT VARCHAR2(255 CHAR),
	SERVICE VARCHAR2(255 CHAR),
	ACTION VARCHAR2(255 CHAR),
	CREATED TIMESTAMP (6),
	UPDATED TIMESTAMP (6)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_MSG_CONT
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_MSG_CONT
   (	ID NUMBER(10,0),
	MESSAGE_ID NUMBER(10,0),
	CONTENT_TYPE VARCHAR2(255 CHAR),
	CONTENT BLOB,
	CHECKSUM CLOB,
	CREATED TIMESTAMP (6)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_MSG_ERROR
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_MSG_ERROR
   (	ID NUMBER(10,0),
	MESSAGE_ID NUMBER(10,0),
	ERROR_MESSAGE VARCHAR2(2048),
	DETAILED_TEXT CLOB,
	ERROR_SOURCE CLOB,
	CREATED TIMESTAMP (6)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_PARTY
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_PARTY
   (	PARTY_ID VARCHAR2(255 CHAR),
	ROLE VARCHAR2(255 CHAR),
	PARTY_ID_TYPE VARCHAR2(512 CHAR)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_PROPERTIES
--------------------------------------------------------

CREATE TABLE DOMIBUS_CONNECTOR_PROPERTIES (
    ID NUMBER(10, 0) NOT NULL,
  	PROPERTY_NAME VARCHAR2(128) NOT NULL,
	PROPERTY_VALUE VARCHAR2(100)
);
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_PROPERTY
--------------------------------------------------------

CREATE TABLE DOMIBUS_CONNECTOR_PROPERTY (
    PROPERTY_NAME VARCHAR2(512) NOT NULL,
	PROPERTY_VALUE VARCHAR2(1024)
);
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_SEQ_STORE
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_SEQ_STORE
   (	SEQ_NAME VARCHAR2(255 CHAR),
	SEQ_VALUE NUMBER(10,0)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_SERVICE
--------------------------------------------------------

  CREATE TABLE DOMIBUS_CONNECTOR_SERVICE
   (	SERVICE VARCHAR2(255 CHAR),
	SERVICE_TYPE VARCHAR2(255 CHAR)
   );
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_USER
--------------------------------------------------------

CREATE TABLE DOMIBUS_CONNECTOR_USER (
    ID NUMBER(10,0) NOT NULL,
    USERNAME VARCHAR2(50),
    ROLE VARCHAR2(50),
    LOCKED NUMBER(1,0) DEFAULT 0,
    NUMBER_OF_GRACE_LOGINS NUMBER(2,0) DEFAULT 5,
    GRACE_LOGINS_USED NUMBER(2,0) DEFAULT 0,
    CREATED TIMESTAMP (6)
);
--------------------------------------------------------
--  DDL for Table DOMIBUS_CONNECTOR_USER_PWD
--------------------------------------------------------

CREATE TABLE DOMIBUS_CONNECTOR_USER_PWD (
    ID NUMBER(10,0) NOT NULL,
	USER_ID NUMBER(10,0),
	PASSWORD VARCHAR2(1024),
	SALT VARCHAR2(512),
	CURRENT_PWD NUMBER(1,0) DEFAULT 0,
	INITIAL_PWD NUMBER(1,0) DEFAULT 0,
	CREATED TIMESTAMP (6)
);
--------------------------------------------------------
--  DDL for Sequence HIBERNATE_SEQUENCE
--------------------------------------------------------

CREATE SEQUENCE HIBERNATE_SEQUENCE INCREMENT BY 1 START WITH 1000;
--------------------------------------------------------
--  DDL for Index PK_CONNECTOR_MESSAGE_INFO
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_CONNECTOR_MESSAGE_INFO ON DOMIBUS_CONNECTOR_MESSAGE_INFO (ID);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_ACTION
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_ACTION ON DOMIBUS_CONNECTOR_ACTION (ACTION);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_BACK_01
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_BACK_01 ON DOMIBUS_CONNECTOR_BACKEND_INFO (ID);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_EVIDENCE
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_EVIDENCE ON DOMIBUS_CONNECTOR_EVIDENCE (ID);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_MESSAGE
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_MESSAGE ON DOMIBUS_CONNECTOR_MESSAGE (ID);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_MSG_ERROR
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_MSG_ERROR ON DOMIBUS_CONNECTOR_MSG_ERROR (ID);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_MSG__01
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_MSG__01 ON DOMIBUS_CONNECTOR_MSG_CONT (ID);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_PARTY
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_PARTY ON DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_SEQ_STORE
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_SEQ_STORE ON DOMIBUS_CONNECTOR_SEQ_STORE (SEQ_NAME);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_SERVICE
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_SERVICE ON DOMIBUS_CONNECTOR_SERVICE (SERVICE);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_USER
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_USER ON DOMIBUS_CONNECTOR_USER (ID);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONNECTOR_USER_01
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONNECTOR_USER_01 ON DOMIBUS_CONNECTOR_USER_PWD (ID);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONN_01
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONN_01 ON DOMIBUS_CONNECTOR_BACK_2_S (DOMIBUS_CONNECTOR_SERVICE_ID, DOMIBUS_CONNECTOR_BACKEND_ID);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONN_02
--------------------------------------------------------

  CREATE UNIQUE INDEX PK_DOMIBUS_CONN_02 ON DOMIBUS_CONNECTOR_CONT_TYPE (MESSAGE_CONTENT_TYPE);
--------------------------------------------------------
--  DDL for Index PK_DOMIBUS_CONN_03
--------------------------------------------------------

-- CREATE UNIQUE INDEX PK_DOMIBUS_CONN_03 ON DOMIBUS_CONNECTOR_PROPERTIES (PROPERTY_NAME);
--------------------------------------------------------
--  DDL for Index UNIQUE_CONNECTOR_MESSAGE_ID
--------------------------------------------------------

  CREATE UNIQUE INDEX UNIQUE_CONNECTOR_MESSAGE_ID ON DOMIBUS_CONNECTOR_MESSAGE (CONNECTOR_MESSAGE_ID);
--------------------------------------------------------
--  DDL for Index UN_DOMIBUS_CONNECTOR_BACK_01
--------------------------------------------------------

  CREATE UNIQUE INDEX UN_DOMIBUS_CONNECTOR_BACK_01 ON DOMIBUS_CONNECTOR_BACKEND_INFO (BACKEND_NAME);
--------------------------------------------------------
--  DDL for Index UN_DOMIBUS_CONNECTOR_BACK_02
--------------------------------------------------------

  CREATE UNIQUE INDEX UN_DOMIBUS_CONNECTOR_BACK_02 ON DOMIBUS_CONNECTOR_BACKEND_INFO (BACKEND_KEY_ALIAS);
--------------------------------------------------------
--  DDL for Index UQ_DOMIBUS_CONNE_EBMS_MESSAGE
--------------------------------------------------------

  CREATE UNIQUE INDEX UQ_DOMIBUS_CONNE_EBMS_MESSAGE ON DOMIBUS_CONNECTOR_MESSAGE (EBMS_MESSAGE_ID);
--------------------------------------------------------
--  DDL for Index UQ_DOMIBUS_CONNE_NAT_MESSAGE_
--------------------------------------------------------

  CREATE UNIQUE INDEX UQ_DOMIBUS_CONNE_NAT_MESSAGE_ ON DOMIBUS_CONNECTOR_MESSAGE (BACKEND_MESSAGE_ID);
--------------------------------------------------------
--  DDL for Index IXFK_DOMIBUS_CONN_DOMIBUS01
--------------------------------------------------------

  CREATE INDEX IXFK_DOMIBUS_CONN_DOMIBUS01 ON DOMIBUS_CONNECTOR_USER_PWD (USER_ID);
--------------------------------------------------------
--  DDL for Index IXFK_DOMIBUS_CONN_EV01
--------------------------------------------------------

  CREATE INDEX IXFK_DOMIBUS_CONN_EV01 ON DOMIBUS_CONNECTOR_EVIDENCE (CONNECTOR_MESSAGE_ID);
--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_SEQ_STORE
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_SEQ_STORE MODIFY (SEQ_NAME NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_SEQ_STORE MODIFY (SEQ_VALUE NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_SEQ_STORE ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_SEQ_STORE PRIMARY KEY (SEQ_NAME);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_PROPERTY
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_PROPERTY MODIFY (PROPERTY_NAME NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_BACK_2_S
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S MODIFY (DOMIBUS_CONNECTOR_SERVICE_ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S MODIFY (DOMIBUS_CONNECTOR_BACKEND_ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S ADD CONSTRAINT PK_DOMIBUS_CONN_01 PRIMARY KEY (DOMIBUS_CONNECTOR_SERVICE_ID, DOMIBUS_CONNECTOR_BACKEND_ID);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_USER
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_USER ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_USER PRIMARY KEY (ID);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_MESSAGE
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE MODIFY (ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_MESSAGE PRIMARY KEY (ID);

  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD CONSTRAINT UNIQUE_CONNECTOR_MESSAGE_ID UNIQUE (CONNECTOR_MESSAGE_ID);

  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD CONSTRAINT UQ_DOMIBUS_CONNE_EBMS_MESSAGE UNIQUE (EBMS_MESSAGE_ID);

  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD CONSTRAINT UQ_DOMIBUS_CONNE_NAT_MESSAGE_ UNIQUE (BACKEND_MESSAGE_ID);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_MSG_ERROR
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR MODIFY (ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR MODIFY (MESSAGE_ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR MODIFY (ERROR_MESSAGE NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR MODIFY (CREATED NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_MSG_ERROR PRIMARY KEY (ID);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_EVIDENCE
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE MODIFY (ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE MODIFY (MESSAGE_ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_EVIDENCE PRIMARY KEY (ID);
--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_BIGDATA
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA MODIFY (ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA ADD PRIMARY KEY (ID);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_PROPERTIES
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_PROPERTIES ADD CONSTRAINT PK_DOMIBUS_CONN_03 PRIMARY KEY (PROPERTY_NAME);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_CONT_TYPE
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_CONT_TYPE MODIFY (MESSAGE_CONTENT_TYPE NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_CONT_TYPE ADD CONSTRAINT PK_DOMIBUS_CONN_02 PRIMARY KEY (MESSAGE_CONTENT_TYPE);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_USER_PWD
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_USER_PWD ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_USER_01 PRIMARY KEY (ID);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_MSG_CONT
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT MODIFY (ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_MSG__01 PRIMARY KEY (ID);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_PARTY
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_PARTY MODIFY (PARTY_ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_PARTY MODIFY (ROLE NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_PARTY ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_PARTY PRIMARY KEY (PARTY_ID, ROLE);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_MESSAGE_INFO
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO MODIFY (ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO MODIFY (MESSAGE_ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT PK_CONNECTOR_MESSAGE_INFO PRIMARY KEY (ID);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_SERVICE
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_SERVICE MODIFY (SERVICE NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_SERVICE MODIFY (SERVICE_TYPE NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_SERVICE PRIMARY KEY (SERVICE);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_BACKEND_INFO
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO MODIFY (ID NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO MODIFY (BACKEND_NAME NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO MODIFY (BACKEND_KEY_ALIAS NOT NULL ENABLE);
  ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_BACK_01 PRIMARY KEY (ID);
  ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO ADD CONSTRAINT UN_DOMIBUS_CONNECTOR_BACK_01 UNIQUE (BACKEND_NAME);

  ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO ADD CONSTRAINT UN_DOMIBUS_CONNECTOR_BACK_02 UNIQUE (BACKEND_KEY_ALIAS);

--------------------------------------------------------
--  Constraints for Table DOMIBUS_CONNECTOR_ACTION
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_ACTION MODIFY (ACTION NOT NULL);
  ALTER TABLE DOMIBUS_CONNECTOR_ACTION MODIFY (PDF_REQUIRED NOT NULL);
  ALTER TABLE DOMIBUS_CONNECTOR_ACTION ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_ACTION PRIMARY KEY (ACTION);

--------------------------------------------------------
--  Ref Constraints for Table DOMIBUS_CONNECTOR_BACK_2_S
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S ADD CONSTRAINT FK_DOMIBUS_CONN_DOMIBUS_CON_01 FOREIGN KEY (DOMIBUS_CONNECTOR_BACKEND_ID)
	  REFERENCES DOMIBUS_CONNECTOR_BACKEND_INFO (ID) ;
  ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S ADD CONSTRAINT FK_DOMIBUS_CONN_DOMIBUS_CON_02 FOREIGN KEY (DOMIBUS_CONNECTOR_SERVICE_ID)
	  REFERENCES DOMIBUS_CONNECTOR_SERVICE (SERVICE) ;
--------------------------------------------------------
--  Ref Constraints for Table DOMIBUS_CONNECTOR_BIGDATA
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_BIGDATA FOREIGN KEY (MESSAGE_ID)
	  REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ;
--------------------------------------------------------
--  Ref Constraints for Table DOMIBUS_CONNECTOR_EVIDENCE
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_EVIDENCES FOREIGN KEY (MESSAGE_ID)
	  REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ;
--------------------------------------------------------
--  Ref Constraints for Table DOMIBUS_CONNECTOR_MESSAGE_INFO
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_MESSAGE_1 FOREIGN KEY (ACTION)
	  REFERENCES DOMIBUS_CONNECTOR_ACTION (ACTION) ;
  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_MESSAGE_2 FOREIGN KEY (SERVICE)
	  REFERENCES DOMIBUS_CONNECTOR_SERVICE (SERVICE) ;
  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_MESSAGE_3 FOREIGN KEY (FROM_PARTY_ID, FROM_PARTY_ROLE)
	  REFERENCES DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE) ;
  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_MESSAGE_4 FOREIGN KEY (TO_PARTY_ID, TO_PARTY_ROLE)
	  REFERENCES DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE) ;
  ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_MESSAGE_I FOREIGN KEY (MESSAGE_ID)
	  REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ;
--------------------------------------------------------
--  Ref Constraints for Table DOMIBUS_CONNECTOR_MSG_CONT
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT ADD CONSTRAINT FK_DOMIBUS_CONN_DOMIBUS_CON_04 FOREIGN KEY (MESSAGE_ID)
	  REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ;
--------------------------------------------------------
--  Ref Constraints for Table DOMIBUS_CONNECTOR_MSG_ERROR
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_MSG_ERROR FOREIGN KEY (MESSAGE_ID)
	  REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ;
--------------------------------------------------------
--  Ref Constraints for Table DOMIBUS_CONNECTOR_USER_PWD
--------------------------------------------------------

  ALTER TABLE DOMIBUS_CONNECTOR_USER_PWD ADD CONSTRAINT FK_DOMIBUS_CONN_DOMIBUS_CON_06 FOREIGN KEY (USER_ID)
	  REFERENCES DOMIBUS_CONNECTOR_USER (ID) ;


