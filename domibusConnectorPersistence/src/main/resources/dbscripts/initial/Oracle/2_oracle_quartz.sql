
-- this script should not be needed, because the tables are generated automatically if missing
-- but to be safe, the ddl is included
create table QRTZ_JOB_DETAILS
(
    SCHED_NAME        VARCHAR2(120) not null,
    JOB_NAME          VARCHAR2(200) not null,
    JOB_GROUP         VARCHAR2(200) not null,
    DESCRIPTION       VARCHAR2(250),
    JOB_CLASS_NAME    VARCHAR2(250) not null,
    IS_DURABLE        VARCHAR2(1)   not null,
    IS_NONCONCURRENT  VARCHAR2(1)   not null,
    IS_UPDATE_DATA    VARCHAR2(1)   not null,
    REQUESTS_RECOVERY VARCHAR2(1)   not null,
    JOB_DATA          BLOB,
    constraint QRTZ_JOB_DETAILS_PK
        primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
)
    /

create index IDX_QRTZ_J_REQ_RECOVERY
    on QRTZ_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY)
    /

create index IDX_QRTZ_J_GRP
    on QRTZ_JOB_DETAILS (SCHED_NAME, JOB_GROUP)
    /

create table QRTZ_TRIGGERS
(
    SCHED_NAME     VARCHAR2(120) not null,
    TRIGGER_NAME   VARCHAR2(200) not null,
    TRIGGER_GROUP  VARCHAR2(200) not null,
    JOB_NAME       VARCHAR2(200) not null,
    JOB_GROUP      VARCHAR2(200) not null,
    DESCRIPTION    VARCHAR2(250),
    NEXT_FIRE_TIME NUMBER(13),
    PREV_FIRE_TIME NUMBER(13),
    PRIORITY       NUMBER(13),
    TRIGGER_STATE  VARCHAR2(16)  not null,
    TRIGGER_TYPE   VARCHAR2(8)   not null,
    START_TIME     NUMBER(13)    not null,
    END_TIME       NUMBER(13),
    CALENDAR_NAME  VARCHAR2(200),
    MISFIRE_INSTR  NUMBER(2),
    JOB_DATA       BLOB,
    constraint QRTZ_TRIGGERS_PK
        primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_TRIGGER_TO_JOBS_FK
        foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP) references QRTZ_JOB_DETAILS
)
    /

create index IDX_QRTZ_T_J
    on QRTZ_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP)
    /

create index IDX_QRTZ_T_JG
    on QRTZ_TRIGGERS (SCHED_NAME, JOB_GROUP)
    /

create index IDX_QRTZ_T_C
    on QRTZ_TRIGGERS (SCHED_NAME, CALENDAR_NAME)
    /

create index IDX_QRTZ_T_G
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP)
    /

create index IDX_QRTZ_T_STATE
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE)
    /

create index IDX_QRTZ_T_N_STATE
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE)
    /

create index IDX_QRTZ_T_N_G_STATE
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE)
    /

create index IDX_QRTZ_T_NEXT_FIRE_TIME
    on QRTZ_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME)
    /

create index IDX_QRTZ_T_NFT_ST
    on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME)
    /

create index IDX_QRTZ_T_NFT_MISFIRE
    on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME)
    /

create index IDX_QRTZ_T_NFT_ST_MISFIRE
    on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE)
    /

create index IDX_QRTZ_T_NFT_ST_MISFIRE_GRP
    on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE)
    /

create table QRTZ_SIMPLE_TRIGGERS
(
    SCHED_NAME      VARCHAR2(120) not null,
    TRIGGER_NAME    VARCHAR2(200) not null,
    TRIGGER_GROUP   VARCHAR2(200) not null,
    REPEAT_COUNT    NUMBER(7)     not null,
    REPEAT_INTERVAL NUMBER(12)    not null,
    TIMES_TRIGGERED NUMBER(10)    not null,
    constraint QRTZ_SIMPLE_TRIG_PK
        primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_SIMPLE_TRIG_TO_TRIG_FK
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS
)
    /

create table QRTZ_CRON_TRIGGERS
(
    SCHED_NAME      VARCHAR2(120) not null,
    TRIGGER_NAME    VARCHAR2(200) not null,
    TRIGGER_GROUP   VARCHAR2(200) not null,
    CRON_EXPRESSION VARCHAR2(120) not null,
    TIME_ZONE_ID    VARCHAR2(80),
    constraint QRTZ_CRON_TRIG_PK
        primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_CRON_TRIG_TO_TRIG_FK
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS
)
    /

create table QRTZ_SIMPROP_TRIGGERS
(
    SCHED_NAME    VARCHAR2(120) not null,
    TRIGGER_NAME  VARCHAR2(200) not null,
    TRIGGER_GROUP VARCHAR2(200) not null,
    STR_PROP_1    VARCHAR2(512),
    STR_PROP_2    VARCHAR2(512),
    STR_PROP_3    VARCHAR2(512),
    INT_PROP_1    NUMBER(10),
    INT_PROP_2    NUMBER(10),
    LONG_PROP_1   NUMBER(13),
    LONG_PROP_2   NUMBER(13),
    DEC_PROP_1    NUMBER(13, 4),
    DEC_PROP_2    NUMBER(13, 4),
    BOOL_PROP_1   VARCHAR2(1),
    BOOL_PROP_2   VARCHAR2(1),
    constraint QRTZ_SIMPROP_TRIG_PK
        primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_SIMPROP_TRIG_TO_TRIG_FK
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS
)
    /

create table QRTZ_BLOB_TRIGGERS
(
    SCHED_NAME    VARCHAR2(120) not null,
    TRIGGER_NAME  VARCHAR2(200) not null,
    TRIGGER_GROUP VARCHAR2(200) not null,
    BLOB_DATA     BLOB,
    constraint QRTZ_BLOB_TRIG_PK
        primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_BLOB_TRIG_TO_TRIG_FK
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS
)
    /

create table QRTZ_CALENDARS
(
    SCHED_NAME    VARCHAR2(120) not null,
    CALENDAR_NAME VARCHAR2(200) not null,
    CALENDAR      BLOB          not null,
    constraint QRTZ_CALENDARS_PK
        primary key (SCHED_NAME, CALENDAR_NAME)
)
    /

create table QRTZ_PAUSED_TRIGGER_GRPS
(
    SCHED_NAME    VARCHAR2(120) not null,
    TRIGGER_GROUP VARCHAR2(200) not null,
    constraint QRTZ_PAUSED_TRIG_GRPS_PK
        primary key (SCHED_NAME, TRIGGER_GROUP)
)
    /

create table QRTZ_FIRED_TRIGGERS
(
    SCHED_NAME        VARCHAR2(120) not null,
    ENTRY_ID          VARCHAR2(95)  not null,
    TRIGGER_NAME      VARCHAR2(200) not null,
    TRIGGER_GROUP     VARCHAR2(200) not null,
    INSTANCE_NAME     VARCHAR2(200) not null,
    FIRED_TIME        NUMBER(13)    not null,
    SCHED_TIME        NUMBER(13)    not null,
    PRIORITY          NUMBER(13)    not null,
    STATE             VARCHAR2(16)  not null,
    JOB_NAME          VARCHAR2(200),
    JOB_GROUP         VARCHAR2(200),
    IS_NONCONCURRENT  VARCHAR2(1),
    REQUESTS_RECOVERY VARCHAR2(1),
    constraint QRTZ_FIRED_TRIGGER_PK
        primary key (SCHED_NAME, ENTRY_ID)
)
    /

create index IDX_QRTZ_FT_TRIG_INST_NAME
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME)
    /

create index IDX_QRTZ_FT_INST_JOB_REQ_RCVRY
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY)
    /

create index IDX_QRTZ_FT_J_G
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP)
    /

create index IDX_QRTZ_FT_JG
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP)
    /

create index IDX_QRTZ_FT_T_G
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    /

create index IDX_QRTZ_FT_TG
    on QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP)
    /

create table QRTZ_SCHEDULER_STATE
(
    SCHED_NAME        VARCHAR2(120) not null,
    INSTANCE_NAME     VARCHAR2(200) not null,
    LAST_CHECKIN_TIME NUMBER(13)    not null,
    CHECKIN_INTERVAL  NUMBER(13)    not null,
    constraint QRTZ_SCHEDULER_STATE_PK
        primary key (SCHED_NAME, INSTANCE_NAME)
)
    /

create table QRTZ_LOCKS
(
    SCHED_NAME VARCHAR2(120) not null,
    LOCK_NAME  VARCHAR2(40)  not null,
    constraint QRTZ_LOCKS_PK
        primary key (SCHED_NAME, LOCK_NAME)
)
    /
