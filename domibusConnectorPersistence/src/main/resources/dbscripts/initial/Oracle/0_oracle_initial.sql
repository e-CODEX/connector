-- not part of the jpa model
CREATE TABLE DC_DB_VERSION
(
    TAG VARCHAR2(255) PRIMARY KEY
)/
create table DC_KEYSTORE
(
    ID          number(19, 0)      not null,
    DESCRIPTION varchar2(512 char),
    KEYSTORE    blob               not null,
    PASSWORD    varchar2(1024 char),
    TYPE        varchar2(50 char),
    UPLOADED    timestamp          not null,
    UUID        varchar2(255 char) not null,
    primary key (ID)
)
/
create table DC_LINK_CONFIG_PROPERTY
(
    DC_LINK_CONFIGURATION_ID number(19, 0)      not null,
    PROPERTY_VALUE           varchar2(2048 char),
    PROPERTY_NAME            varchar2(255 char) not null,
    primary key (DC_LINK_CONFIGURATION_ID, PROPERTY_NAME)
)
/
create table DC_LINK_CONFIGURATION
(
    ID          number(19, 0)      not null,
    CONFIG_NAME varchar2(255 char) not null,
    LINK_IMPL   varchar2(255 char),
    primary key (ID)
)
/
create table DC_LINK_PARTNER
(
    ID             number(19, 0)      not null,
    DESCRIPTION    clob,
    ENABLED        number(1, 0)       not null,
    NAME           varchar2(255 char) not null,
    LINK_TYPE      varchar2(20 char),
    LINK_CONFIG_ID number(19, 0),
    primary key (ID)
)
/
create table DC_LINK_PARTNER_PROPERTY
(
    DC_LINK_PARTNER_ID number(19, 0)      not null,
    PROPERTY_VALUE     varchar2(2048 char),
    PROPERTY_NAME      varchar2(255 char) not null,
    primary key (DC_LINK_PARTNER_ID, PROPERTY_NAME)
)
/
create table DC_MESSAGE_LANE
(
    ID          number(19, 0)      not null,
    DESCRIPTION clob,
    NAME        varchar2(255 char) not null,
    primary key (ID)
)
/
create table DC_MESSAGE_LANE_PROPERTY
(
    DC_MESSAGE_LANE_ID number(19, 0)      not null,
    PROPERTY_VALUE     varchar2(2048 char),
    PROPERTY_NAME      varchar2(255 char) not null,
    primary key (DC_MESSAGE_LANE_ID, PROPERTY_NAME)
)
/
create table DC_MSGCNT_DETSIG
(
    ID             number(19, 0) not null,
    SIGNATURE      blob,
    SIGNATURE_NAME varchar2(255 char),
    SIGNATURE_TYPE varchar2(255 char),
    primary key (ID)
)
/
create table DC_PMODE_SET
(
    ID                number(19, 0) not null,
    ACTIVE            number(1, 0),
    CREATED           timestamp,
    DESCRIPTION       clob,
    PMODES            blob,
    FK_CONNECTORSTORE number(19, 0),
    FK_MESSAGE_LANE   number(19, 0),
    primary key (ID)
)
/
create table DC_TRANSPORT_STEP
(
    ID                          number(19, 0)      not null,
    ATTEMPT                     number(10, 0)      not null,
    CONNECTOR_MESSAGE_ID        varchar2(255 char) not null,
    CREATED                     timestamp          not null,
    FINAL_STATE_REACHED         timestamp,
    LINK_PARTNER_NAME           varchar2(255 char) not null,
    REMOTE_MESSAGE_ID           varchar2(255 char),
    TRANSPORT_ID                varchar2(255 char),
    TRANSPORT_SYSTEM_MESSAGE_ID varchar2(255 char),
    TRANSPORTED_MESSAGE         clob,
    primary key (ID)
)
/
create table DC_TRANSPORT_STEP_STATUS
(
    STATE             varchar2(255 char) not null,
    TRANSPORT_STEP_ID number(19, 0)      not null,
    CREATED           timestamp          not null,
    TEXT              clob,
    primary key (STATE, TRANSPORT_STEP_ID)
)
/
create table DOMIBUS_CONNECTOR_ACTION
(
    ID           number(19, 0)      not null,
    ACTION       varchar2(255 char) not null,
    FK_PMODE_SET number(19, 0),
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_BIGDATA
(
    ID                   number(19, 0) not null,
    CHECKSUM             clob,
    CONNECTOR_MESSAGE_ID varchar2(255 char),
    CONTENT              blob,
    CREATED              timestamp     not null,
    LAST_ACCESS          timestamp,
    MIMETYPE             varchar2(255 char),
    NAME                 clob,
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_EVIDENCE
(
    ID            number(19, 0) not null,
    DELIVERED_NAT timestamp,
    DELIVERED_GW  timestamp,
    EVIDENCE      clob,
    TYPE          varchar2(255 char),
    UPDATED       timestamp     not null,
    MESSAGE_ID    number(19, 0) not null,
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_MESSAGE
(
    ID                   number(19, 0)      not null,
    BACKEND_MESSAGE_ID   varchar2(255 char),
    BACKEND_NAME         varchar2(255 char),
    CONFIRMED            timestamp,
    CONNECTOR_MESSAGE_ID varchar2(255 char) not null,
    CONVERSATION_ID      varchar2(255 char),
    CREATED              timestamp          not null,
    DELIVERED_GW         timestamp,
    DELIVERED_BACKEND    timestamp,
    DIRECTION_SOURCE     varchar2(20 char),
    DIRECTION_TARGET     varchar2(20 char),
    EBMS_MESSAGE_ID      varchar2(255 char),
    GATEWAY_NAME         varchar2(255 char),
    HASH_VALUE           clob,
    REJECTED             timestamp,
    UPDATED              timestamp          not null,
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_MESSAGE_INFO
(
    ID               number(19, 0) not null,
    CREATED          timestamp     not null,
    FINAL_RECIPIENT  varchar2(2048 char),
    ORIGINAL_SENDER  varchar2(2048 char),
    UPDATED          timestamp     not null,
    FK_ACTION        number(19, 0),
    FK_FROM_PARTY_ID number(19, 0),
    MESSAGE_ID       number(19, 0) not null,
    FK_SERVICE       number(19, 0),
    FK_TO_PARTY_ID   number(19, 0),
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_MSG_CONT
(
    ID                    number(19, 0) not null,
    CHECKSUM              clob,
    CONNECTOR_MESSAGE_ID  varchar2(255 char),
    CONTENT               blob,
    CONTENT_TYPE          varchar2(255 char),
    CREATED               timestamp     not null,
    DELETED               timestamp,
    DIGEST                varchar2(512 char),
    PAYLOAD_DESCRIPTION   clob,
    PAYLOAD_IDENTIFIER    varchar2(512 char),
    PAYLOAD_MIMETYPE      varchar2(255 char),
    PAYLOAD_NAME          varchar2(512 char),
    PAYLOAD_SIZE          number(19, 0),
    STORAGE_PROVIDER_NAME varchar2(255 char),
    STORAGE_REFERENCE_ID  varchar2(512 char),
    DETACHED_SIGNATURE_ID number(19, 0),
    MESSAGE_ID            number(19, 0),
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_MSG_ERROR
(
    ID            number(19, 0)       not null,
    CREATED       timestamp           not null,
    DETAILED_TEXT clob,
    ERROR_MESSAGE varchar2(2048 char) not null,
    ERROR_SOURCE  clob,
    MESSAGE_ID    number(19, 0)       not null,
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_PARTY
(
    ID            number(19, 0)      not null,
    PARTY_ID      varchar2(255 char) not null,
    PARTY_ID_TYPE varchar2(512 char) not null,
    IDENTIFIER    varchar2(255 char),
    ROLE          varchar2(255 char),
    ROLE_TYPE     varchar2(50 char),
    FK_PMODE_SET  number(19, 0),
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_PROPERTY
(
    ID             number(10, 0)       not null,
    PROPERTY_NAME  varchar2(2048 char) not null,
    PROPERTY_VALUE varchar2(2048 char),
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_SEQ_STORE
(
    SEQ_NAME  varchar2(255 char) not null,
    SEQ_VALUE number(19, 0),
    primary key (SEQ_NAME)
)
/
create table DOMIBUS_CONNECTOR_SERVICE
(
    ID           number(19, 0)      not null,
    SERVICE      varchar2(255 char) not null,
    SERVICE_TYPE varchar2(255 char),
    FK_PMODE_SET number(19, 0),
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_USER
(
    ID                     number(19, 0)     not null,
    CREATED                timestamp         not null,
    GRACE_LOGINS_USED      number(19, 0)     not null,
    LOCKED                 number(1, 0)      not null,
    NUMBER_OF_GRACE_LOGINS number(19, 0)     not null,
    ROLE                   varchar2(50 char) not null,
    USERNAME               varchar2(50 char) not null,
    primary key (ID)
)
/
create table DOMIBUS_CONNECTOR_USER_PWD
(
    ID          number(19, 0)       not null,
    CREATED     timestamp           not null,
    CURRENT_PWD number(1, 0)        not null,
    INITIAL_PWD number(1, 0)        not null,
    password    varchar2(1024 char) not null,
    salt        varchar2(512 char)  not null,
    USER_ID     number(19, 0)       not null,
    primary key (ID)
)
/
alter table DC_KEYSTORE
    add constraint UK_90ry06hw9optjgeay7s8mvyye unique (UUID)
/
alter table DC_LINK_PARTNER
    add constraint UK_50y2l6v1vlcoaimoae2rpk5r6 unique (NAME)
/
alter table DC_MESSAGE_LANE
    add constraint UK_ljuyrly9is6sioein0ro1yfh3 unique (NAME)
/
alter table DOMIBUS_CONNECTOR_MESSAGE
    add constraint UK_81o66ln4txujh8p62a6g6lqx9 unique (BACKEND_MESSAGE_ID)
/
alter table DOMIBUS_CONNECTOR_MESSAGE
    add constraint UK_s9y5ajqyjnjb7gjf2na4ae7ur unique (CONNECTOR_MESSAGE_ID)
/
alter table DOMIBUS_CONNECTOR_MESSAGE
    add constraint UK_e71rh4n71m4mpgcokhengr592 unique (EBMS_MESSAGE_ID)
/
alter table DC_LINK_CONFIG_PROPERTY
    add constraint FK62l6hjp3v8y2mgs1rfwaqslqm foreign key (DC_LINK_CONFIGURATION_ID) references DC_LINK_CONFIGURATION
/
alter table DC_LINK_PARTNER
    add constraint FKdhl3vsslwv2bo9ttjc5lnm4h6 foreign key (LINK_CONFIG_ID) references DC_LINK_CONFIGURATION
/
alter table DC_LINK_PARTNER_PROPERTY
    add constraint FKq1jp8n1v9eovkn9mmslnhlhhk foreign key (DC_LINK_PARTNER_ID) references DC_LINK_PARTNER
/
alter table DC_MESSAGE_LANE_PROPERTY
    add constraint FK8i4lmhlsfpwb2i9srbubyrhb4 foreign key (DC_MESSAGE_LANE_ID) references DC_MESSAGE_LANE
/
alter table DC_PMODE_SET
    add constraint FKawkfbejuoofu1ijhxhpqqjwdj foreign key (FK_CONNECTORSTORE) references DC_KEYSTORE
/
alter table DC_PMODE_SET
    add constraint FKlnoic3soynw9bped4y6iqxjpk foreign key (FK_MESSAGE_LANE) references DC_MESSAGE_LANE
/
alter table DC_TRANSPORT_STEP_STATUS
    add constraint FK5g1jngh3f82ialbtnqq99h418 foreign key (TRANSPORT_STEP_ID) references DC_TRANSPORT_STEP
/
alter table DOMIBUS_CONNECTOR_ACTION
    add constraint FK249380r1rr1kt886abx7exj7g foreign key (FK_PMODE_SET) references DC_PMODE_SET
/
alter table DOMIBUS_CONNECTOR_EVIDENCE
    add constraint FK4jxg7xyfgfl8txay9slwcafj1 foreign key (MESSAGE_ID) references DOMIBUS_CONNECTOR_MESSAGE
/
alter table DOMIBUS_CONNECTOR_MESSAGE_INFO
    add constraint FKadkw4ku0o3a3x80felptltnfr foreign key (FK_ACTION) references DOMIBUS_CONNECTOR_ACTION
/
alter table DOMIBUS_CONNECTOR_MESSAGE_INFO
    add constraint FKhbvkhb64ltjr9pjpvds09t6h7 foreign key (FK_FROM_PARTY_ID) references DOMIBUS_CONNECTOR_PARTY
/
alter table DOMIBUS_CONNECTOR_MESSAGE_INFO
    add constraint FKuvd19003ob697v6e8ovgw140 foreign key (MESSAGE_ID) references DOMIBUS_CONNECTOR_MESSAGE
/
alter table DOMIBUS_CONNECTOR_MESSAGE_INFO
    add constraint FKoltsh7wsh3a0pjg7aagltlbbo foreign key (FK_SERVICE) references DOMIBUS_CONNECTOR_SERVICE
/
alter table DOMIBUS_CONNECTOR_MESSAGE_INFO
    add constraint FKa5oheqmhn4eu4j1yuyi3femsh foreign key (FK_TO_PARTY_ID) references DOMIBUS_CONNECTOR_PARTY
/
alter table DOMIBUS_CONNECTOR_MSG_CONT
    add constraint FK7emymoigdt3qsplyri0dq1xow foreign key (DETACHED_SIGNATURE_ID) references DC_MSGCNT_DETSIG
/
alter table DOMIBUS_CONNECTOR_MSG_CONT
    add constraint FKda043m9h695ogla2sg58kxkb1 foreign key (MESSAGE_ID) references DOMIBUS_CONNECTOR_MESSAGE
/
alter table DOMIBUS_CONNECTOR_MSG_ERROR
    add constraint FKi0wrarse6i0t5nj4r82p1e4n foreign key (MESSAGE_ID) references DOMIBUS_CONNECTOR_MESSAGE
/
alter table DOMIBUS_CONNECTOR_PARTY
    add constraint FKal7yndgaiapslndruuu48g34 foreign key (FK_PMODE_SET) references DC_PMODE_SET
/
alter table DOMIBUS_CONNECTOR_SERVICE
    add constraint FKbj0847csnu0cbi0u92j81lrn0 foreign key (FK_PMODE_SET) references DC_PMODE_SET
/
alter table DOMIBUS_CONNECTOR_USER_PWD
    add constraint FK62doe366dlq21rv9ysf7hfk4e foreign key (USER_ID) references DOMIBUS_CONNECTOR_USER
/