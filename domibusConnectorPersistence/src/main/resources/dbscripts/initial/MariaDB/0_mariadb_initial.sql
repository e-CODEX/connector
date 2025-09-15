-- not part of the jpa model
CREATE TABLE DC_DB_VERSION
(
    TAG VARCHAR(255) PRIMARY KEY
);

create table DC_KEYSTORE
(
    ID          bigint        not null
        primary key,
    DESCRIPTION varchar(512)  null,
    KEYSTORE    longblob      not null,
    PASSWORD    varchar(1024) null,
    TYPE        varchar(50)   null,
    UPLOADED    datetime(6)   not null,
    UUID        varchar(255)  not null,
    constraint UK_90ry06hw9optjgeay7s8mvyye
        unique (UUID)
);

create table DC_LINK_CONFIGURATION
(
    ID          bigint       not null
        primary key,
    CONFIG_NAME varchar(255) not null,
    LINK_IMPL   varchar(255) null
);

create table DC_LINK_CONFIG_PROPERTY
(
    DC_LINK_CONFIGURATION_ID bigint        not null,
    PROPERTY_VALUE           varchar(2048) null,
    PROPERTY_NAME            varchar(255)  not null,
    primary key (DC_LINK_CONFIGURATION_ID, PROPERTY_NAME),
    constraint FK62l6hjp3v8y2mgs1rfwaqslqm
        foreign key (DC_LINK_CONFIGURATION_ID) references DC_LINK_CONFIGURATION (ID)
);

# bit defaults to true, but entity inits with false
create table DC_LINK_PARTNER
(
    ID             bigint       not null
        primary key,
    DESCRIPTION    longtext     null,
    ENABLED        bit          not null,
    NAME           varchar(255) not null,
    LINK_TYPE      varchar(20)  null,
    LINK_CONFIG_ID bigint       null,
    constraint UK_50y2l6v1vlcoaimoae2rpk5r6
        unique (NAME),
    constraint FKdhl3vsslwv2bo9ttjc5lnm4h6
        foreign key (LINK_CONFIG_ID) references DC_LINK_CONFIGURATION (ID)
);

create table DC_LINK_PARTNER_PROPERTY
(
    DC_LINK_PARTNER_ID bigint        not null,
    PROPERTY_VALUE     varchar(2048) null,
    PROPERTY_NAME      varchar(255)  not null,
    primary key (DC_LINK_PARTNER_ID, PROPERTY_NAME),
    constraint FKq1jp8n1v9eovkn9mmslnhlhhk
        foreign key (DC_LINK_PARTNER_ID) references DC_LINK_PARTNER (ID)
);

create table DC_MESSAGE_LANE
(
    ID          bigint       not null
        primary key,
    DESCRIPTION longtext     null,
    NAME        varchar(255) not null,
    constraint UK_ljuyrly9is6sioein0ro1yfh3
        unique (NAME)
);

create table DC_MESSAGE_LANE_PROPERTY
(
    DC_MESSAGE_LANE_ID bigint        not null,
    PROPERTY_VALUE     varchar(2048) null,
    PROPERTY_NAME      varchar(255)  not null,
    primary key (DC_MESSAGE_LANE_ID, PROPERTY_NAME),
    constraint FK8i4lmhlsfpwb2i9srbubyrhb4
        foreign key (DC_MESSAGE_LANE_ID) references DC_MESSAGE_LANE (ID)
);

create table DC_MSGCNT_DETSIG
(
    ID             bigint       not null
        primary key,
    SIGNATURE      longblob     null,
    SIGNATURE_NAME varchar(255) null,
    SIGNATURE_TYPE varchar(255) null
);

create table DC_PMODE_SET
(
    ID                bigint      not null
        primary key,
    ACTIVE            bit         null,
    CREATED           datetime(6) null,
    DESCRIPTION       longtext    null,
    PMODES            longblob    null,
    FK_CONNECTORSTORE bigint      null,
    FK_MESSAGE_LANE   bigint      null,
    constraint FKawkfbejuoofu1ijhxhpqqjwdj
        foreign key (FK_CONNECTORSTORE) references DC_KEYSTORE (ID),
    constraint FKlnoic3soynw9bped4y6iqxjpk
        foreign key (FK_MESSAGE_LANE) references DC_MESSAGE_LANE (ID)
);

create table DC_TRANSPORT_STEP
(
    ID                          bigint       not null
        primary key,
    ATTEMPT                     int          not null,
    CONNECTOR_MESSAGE_ID        varchar(255) not null,
    CREATED                     datetime(6)  not null,
    FINAL_STATE_REACHED         datetime(6)  null,
    LINK_PARTNER_NAME           varchar(255) not null,
    REMOTE_MESSAGE_ID           varchar(255) null,
    TRANSPORT_ID                varchar(255) null,
    TRANSPORT_SYSTEM_MESSAGE_ID varchar(255) null,
    TRANSPORTED_MESSAGE         longtext     null
);

create table DC_TRANSPORT_STEP_STATUS
(
    STATE             varchar(255) not null,
    TRANSPORT_STEP_ID bigint       not null,
    CREATED           datetime(6)  not null,
    TEXT              longtext     null,
    primary key (STATE, TRANSPORT_STEP_ID),
    constraint FK5g1jngh3f82ialbtnqq99h418
        foreign key (TRANSPORT_STEP_ID) references DC_TRANSPORT_STEP (ID)
);

create table DOMIBUS_CONNECTOR_ACTION
(
    ID           bigint       not null
        primary key,
    ACTION       varchar(255) not null,
    FK_PMODE_SET bigint       null,
    constraint FK249380r1rr1kt886abx7exj7g
        foreign key (FK_PMODE_SET) references DC_PMODE_SET (ID)
);

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

create table DOMIBUS_CONNECTOR_MESSAGE
(
    ID                   bigint       not null
        primary key,
    BACKEND_MESSAGE_ID   varchar(255) null,
    BACKEND_NAME         varchar(255) null,
    CONFIRMED            datetime(6)  null,
    CONNECTOR_MESSAGE_ID varchar(255) not null,
    CONVERSATION_ID      varchar(255) null,
    CREATED              datetime(6)  not null,
    DELIVERED_GW         datetime(6)  null,
    DELIVERED_BACKEND    datetime(6)  null,
    DIRECTION_SOURCE     varchar(20)  null,
    DIRECTION_TARGET     varchar(20)  null,
    EBMS_MESSAGE_ID      varchar(255) null,
    GATEWAY_NAME         varchar(255) null,
    HASH_VALUE           longtext     null,
    REJECTED             datetime(6)  null,
    UPDATED              datetime(6)  not null,
    constraint UK_s9y5ajqyjnjb7gjf2na4ae7ur
        unique (CONNECTOR_MESSAGE_ID)
);

create table DOMIBUS_CONNECTOR_EVIDENCE
(
    ID            bigint       not null
        primary key,
    DELIVERED_NAT datetime(6)  null,
    DELIVERED_GW  datetime(6)  null,
    EVIDENCE      longtext     null,
    TYPE          varchar(255) null,
    UPDATED       datetime(6)  not null,
    MESSAGE_ID    bigint       not null,
    constraint FK4jxg7xyfgfl8txay9slwcafj1
        foreign key (MESSAGE_ID) references DOMIBUS_CONNECTOR_MESSAGE (ID)
);

create table DOMIBUS_CONNECTOR_MSG_CONT
(
    ID                    bigint       not null
        primary key,
    CHECKSUM              longtext     null,
    CONNECTOR_MESSAGE_ID  varchar(255) null,
    CONTENT               longblob     null,
    CONTENT_TYPE          varchar(255) null,
    CREATED               datetime(6)  not null,
    DELETED               datetime(6)  null,
    DIGEST                varchar(512) null,
    PAYLOAD_DESCRIPTION   longtext     null,
    PAYLOAD_IDENTIFIER    varchar(512) null,
    PAYLOAD_MIMETYPE      varchar(255) null,
    PAYLOAD_NAME          varchar(512) null,
    PAYLOAD_SIZE          bigint       null,
    STORAGE_PROVIDER_NAME varchar(255) null,
    STORAGE_REFERENCE_ID  varchar(512) null,
    DETACHED_SIGNATURE_ID bigint       null,
    MESSAGE_ID            bigint       null,
    constraint FK7emymoigdt3qsplyri0dq1xow
        foreign key (DETACHED_SIGNATURE_ID) references DC_MSGCNT_DETSIG (ID),
    constraint FKda043m9h695ogla2sg58kxkb1
        foreign key (MESSAGE_ID) references DOMIBUS_CONNECTOR_MESSAGE (ID)
);

create table DOMIBUS_CONNECTOR_MSG_ERROR
(
    ID            bigint        not null
        primary key,
    CREATED       datetime(6)   not null,
    DETAILED_TEXT longtext      null,
    ERROR_MESSAGE varchar(2048) not null,
    ERROR_SOURCE  longtext      null,
    MESSAGE_ID    bigint        not null,
    constraint FKi0wrarse6i0t5nj4r82p1e4n
        foreign key (MESSAGE_ID) references DOMIBUS_CONNECTOR_MESSAGE (ID)
);

create table DOMIBUS_CONNECTOR_PARTY
(
    ID            bigint       not null
        primary key,
    PARTY_ID      varchar(255) not null,
    PARTY_ID_TYPE varchar(512) not null,
    IDENTIFIER    varchar(255) null,
    ROLE          varchar(255) null,
    ROLE_TYPE     varchar(50)  null,
    FK_PMODE_SET  bigint       null,
    constraint FKal7yndgaiapslndruuu48g34
        foreign key (FK_PMODE_SET) references DC_PMODE_SET (ID)
);

create table DOMIBUS_CONNECTOR_PROPERTY
(
    ID             int           not null
        primary key,
    PROPERTY_NAME  varchar(2048) not null,
    PROPERTY_VALUE varchar(2048) null
);

create table DOMIBUS_CONNECTOR_SEQ_STORE
(
    SEQ_NAME  varchar(255) not null
        primary key,
    SEQ_VALUE bigint       null
);

create table DOMIBUS_CONNECTOR_SERVICE
(
    ID           bigint       not null
        primary key,
    SERVICE      varchar(255) not null,
    SERVICE_TYPE varchar(255) null,
    FK_PMODE_SET bigint       null,
    constraint FKbj0847csnu0cbi0u92j81lrn0
        foreign key (FK_PMODE_SET) references DC_PMODE_SET (ID)
);

create table DOMIBUS_CONNECTOR_MESSAGE_INFO
(
    ID               bigint        not null
        primary key,
    CREATED          datetime(6)   not null,
    FINAL_RECIPIENT  varchar(2048) null,
    ORIGINAL_SENDER  varchar(2048) null,
    UPDATED          datetime(6)   not null,
    FK_ACTION        bigint        null,
    FK_FROM_PARTY_ID bigint        null,
    MESSAGE_ID       bigint        not null,
    FK_SERVICE       bigint        null,
    FK_TO_PARTY_ID   bigint        null,
    constraint FKa5oheqmhn4eu4j1yuyi3femsh
        foreign key (FK_TO_PARTY_ID) references DOMIBUS_CONNECTOR_PARTY (ID),
    constraint FKadkw4ku0o3a3x80felptltnfr
        foreign key (FK_ACTION) references DOMIBUS_CONNECTOR_ACTION (ID),
    constraint FKhbvkhb64ltjr9pjpvds09t6h7
        foreign key (FK_FROM_PARTY_ID) references DOMIBUS_CONNECTOR_PARTY (ID),
    constraint FKoltsh7wsh3a0pjg7aagltlbbo
        foreign key (FK_SERVICE) references DOMIBUS_CONNECTOR_SERVICE (ID),
    constraint FKuvd19003ob697v6e8ovgw140
        foreign key (MESSAGE_ID) references DOMIBUS_CONNECTOR_MESSAGE (ID)
);

# bit defaults to true
create table DOMIBUS_CONNECTOR_USER
(
    ID                     bigint      not null
        primary key,
    CREATED                datetime(6) not null,
    GRACE_LOGINS_USED      bigint      not null,
    LOCKED                 bit         not null,
    NUMBER_OF_GRACE_LOGINS bigint      not null,
    ROLE                   varchar(50) not null,
    USERNAME               varchar(50) not null
);

# bit defaults to true
create table DOMIBUS_CONNECTOR_USER_PWD
(
    ID          bigint        not null
        primary key,
    CREATED     datetime(6)   not null,
    CURRENT_PWD bit           not null,
    INITIAL_PWD bit           not null,
    password    varchar(1024) not null,
    salt        varchar(512)  not null,
    USER_ID     bigint        not null,
    constraint FK62doe366dlq21rv9ysf7hfk4e
        foreign key (USER_ID) references DOMIBUS_CONNECTOR_USER (ID)
);
