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
    constraint UK_81o66ln4txujh8p62a6g6lqx9
        unique (BACKEND_MESSAGE_ID),
    constraint UK_e71rh4n71m4mpgcokhengr592
        unique (EBMS_MESSAGE_ID),
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

create table QRTZ_CALENDARS
(
    SCHED_NAME    varchar(120) not null,
    CALENDAR_NAME varchar(190) not null,
    CALENDAR      blob         not null,
    primary key (SCHED_NAME, CALENDAR_NAME)
);

create table QRTZ_FIRED_TRIGGERS
(
    SCHED_NAME        varchar(120) not null,
    ENTRY_ID          varchar(95)  not null,
    TRIGGER_NAME      varchar(190) not null,
    TRIGGER_GROUP     varchar(190) not null,
    INSTANCE_NAME     varchar(190) not null,
    FIRED_TIME        bigint       not null,
    SCHED_TIME        bigint       not null,
    PRIORITY          int          not null,
    STATE             varchar(16)  not null,
    JOB_NAME          varchar(190) null,
    JOB_GROUP         varchar(190) null,
    IS_NONCONCURRENT  varchar(1)   null,
    REQUESTS_RECOVERY varchar(1)   null,
    primary key (SCHED_NAME, ENTRY_ID)
);

create index IDX_QRTZ_FT_INST_JOB_REQ_RCVRY
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);

create index IDX_QRTZ_FT_JG
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);

create index IDX_QRTZ_FT_J_G
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index IDX_QRTZ_FT_TG
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

create index IDX_QRTZ_FT_TRIG_INST_NAME
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);

create index IDX_QRTZ_FT_T_G
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

create table QRTZ_JOB_DETAILS
(
    SCHED_NAME        varchar(120) not null,
    JOB_NAME          varchar(190) not null,
    JOB_GROUP         varchar(190) not null,
    DESCRIPTION       varchar(250) null,
    JOB_CLASS_NAME    varchar(250) not null,
    IS_DURABLE        varchar(1)   not null,
    IS_NONCONCURRENT  varchar(1)   not null,
    IS_UPDATE_DATA    varchar(1)   not null,
    REQUESTS_RECOVERY varchar(1)   not null,
    JOB_DATA          blob         null,
    primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

create index IDX_QRTZ_J_GRP
    on QRTZ_JOB_DETAILS (SCHED_NAME, JOB_GROUP);

create index IDX_QRTZ_J_REQ_RECOVERY
    on QRTZ_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);

create table QRTZ_LOCKS
(
    SCHED_NAME varchar(120) not null,
    LOCK_NAME  varchar(40)  not null,
    primary key (SCHED_NAME, LOCK_NAME)
);

create table QRTZ_PAUSED_TRIGGER_GRPS
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_GROUP varchar(190) not null,
    primary key (SCHED_NAME, TRIGGER_GROUP)
);

create table QRTZ_SCHEDULER_STATE
(
    SCHED_NAME        varchar(120) not null,
    INSTANCE_NAME     varchar(190) not null,
    LAST_CHECKIN_TIME bigint       not null,
    CHECKIN_INTERVAL  bigint       not null,
    primary key (SCHED_NAME, INSTANCE_NAME)
);

create table QRTZ_TRIGGERS
(
    SCHED_NAME     varchar(120) not null,
    TRIGGER_NAME   varchar(190) not null,
    TRIGGER_GROUP  varchar(190) not null,
    JOB_NAME       varchar(190) not null,
    JOB_GROUP      varchar(190) not null,
    DESCRIPTION    varchar(250) null,
    NEXT_FIRE_TIME bigint       null,
    PREV_FIRE_TIME bigint       null,
    PRIORITY       int          null,
    TRIGGER_STATE  varchar(16)  not null,
    TRIGGER_TYPE   varchar(8)   not null,
    START_TIME     bigint       not null,
    END_TIME       bigint       null,
    CALENDAR_NAME  varchar(190) null,
    MISFIRE_INSTR  smallint     null,
    JOB_DATA       blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_TRIGGERS_ibfk_1
        foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP) references QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

create table QRTZ_BLOB_TRIGGERS
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_NAME  varchar(190) not null,
    TRIGGER_GROUP varchar(190) not null,
    BLOB_DATA     blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_BLOB_TRIGGERS_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create index SCHED_NAME
    on QRTZ_BLOB_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

create table QRTZ_CRON_TRIGGERS
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(190) not null,
    TRIGGER_GROUP   varchar(190) not null,
    CRON_EXPRESSION varchar(120) not null,
    TIME_ZONE_ID    varchar(80)  null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_CRON_TRIGGERS_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create table QRTZ_SIMPLE_TRIGGERS
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(190) not null,
    TRIGGER_GROUP   varchar(190) not null,
    REPEAT_COUNT    bigint       not null,
    REPEAT_INTERVAL bigint       not null,
    TIMES_TRIGGERED bigint       not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_SIMPLE_TRIGGERS_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create table QRTZ_SIMPROP_TRIGGERS
(
    SCHED_NAME    varchar(120)   not null,
    TRIGGER_NAME  varchar(190)   not null,
    TRIGGER_GROUP varchar(190)   not null,
    STR_PROP_1    varchar(512)   null,
    STR_PROP_2    varchar(512)   null,
    STR_PROP_3    varchar(512)   null,
    INT_PROP_1    int            null,
    INT_PROP_2    int            null,
    LONG_PROP_1   bigint         null,
    LONG_PROP_2   bigint         null,
    DEC_PROP_1    decimal(13, 4) null,
    DEC_PROP_2    decimal(13, 4) null,
    BOOL_PROP_1   varchar(1)     null,
    BOOL_PROP_2   varchar(1)     null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_SIMPROP_TRIGGERS_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create index IDX_QRTZ_T_C
    on QRTZ_TRIGGERS (SCHED_NAME, CALENDAR_NAME);

create index IDX_QRTZ_T_G
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

create index IDX_QRTZ_T_J
    on QRTZ_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index IDX_QRTZ_T_JG
    on QRTZ_TRIGGERS (SCHED_NAME, JOB_GROUP);

create index IDX_QRTZ_T_NEXT_FIRE_TIME
    on QRTZ_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);

create index IDX_QRTZ_T_NFT_MISFIRE
    on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);

create index IDX_QRTZ_T_NFT_ST
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);

create index IDX_QRTZ_T_NFT_ST_MISFIRE
    on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);

create index IDX_QRTZ_T_NFT_ST_MISFIRE_GRP
    on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

create index IDX_QRTZ_T_N_G_STATE
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index IDX_QRTZ_T_N_STATE
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index IDX_QRTZ_T_STATE
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE);


