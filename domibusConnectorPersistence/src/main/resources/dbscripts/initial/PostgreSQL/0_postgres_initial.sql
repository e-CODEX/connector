create table dc_db_version
(
    tag varchar(255) not null
        constraint pk_dc_db_version
        primary key
);

alter table dc_db_version
    owner to postgres;

create table dc_keystore
(
    id          bigint       not null
        primary key,
    description varchar(512),
    keystore    oid          not null,
    password    varchar(1024),
    type        varchar(50),
    uploaded    timestamp    not null,
    uuid        varchar(255) not null
        constraint uk_90ry06hw9optjgeay7s8mvyye
            unique
);

alter table dc_keystore
    owner to postgres;

create table dc_link_configuration
(
    id          bigint       not null
        primary key,
    config_name varchar(255) not null,
    link_impl   varchar(255)
);

alter table dc_link_configuration
    owner to postgres;

create table dc_link_config_property
(
    dc_link_configuration_id bigint       not null
        constraint fk62l6hjp3v8y2mgs1rfwaqslqm
            references dc_link_configuration,
    property_value           varchar(2048),
    property_name            varchar(255) not null,
    primary key (dc_link_configuration_id, property_name)
);

alter table dc_link_config_property
    owner to postgres;

create table dc_link_partner
(
    id             bigint       not null
        primary key,
    description    text,
    enabled        boolean      not null,
    name           varchar(255) not null
        constraint uk_50y2l6v1vlcoaimoae2rpk5r6
            unique,
    link_type      varchar(20),
    link_config_id bigint
        constraint fkdhl3vsslwv2bo9ttjc5lnm4h6
            references dc_link_configuration
);

alter table dc_link_partner
    owner to postgres;

create table dc_link_partner_property
(
    dc_link_partner_id bigint       not null
        constraint fkq1jp8n1v9eovkn9mmslnhlhhk
            references dc_link_partner,
    property_value     varchar(2048),
    property_name      varchar(255) not null,
    primary key (dc_link_partner_id, property_name)
);

alter table dc_link_partner_property
    owner to postgres;

create table dc_message_lane
(
    id          bigint       not null
        primary key,
    description text,
    name        varchar(255) not null
        constraint uk_ljuyrly9is6sioein0ro1yfh3
            unique
);

alter table dc_message_lane
    owner to postgres;

create table dc_message_lane_property
(
    dc_message_lane_id bigint       not null
        constraint fk8i4lmhlsfpwb2i9srbubyrhb4
            references dc_message_lane,
    property_value     varchar(2048),
    property_name      varchar(255) not null,
    primary key (dc_message_lane_id, property_name)
);

alter table dc_message_lane_property
    owner to postgres;

create table dc_msgcnt_detsig
(
    id             bigint not null
        primary key,
    signature      oid,
    signature_name varchar(255),
    signature_type varchar(255)
);

alter table dc_msgcnt_detsig
    owner to postgres;

create table dc_pmode_set
(
    id                bigint not null
        primary key,
    active            boolean,
    created           timestamp,
    description       text,
    pmodes            oid,
    fk_connectorstore bigint
        constraint fkawkfbejuoofu1ijhxhpqqjwdj
            references dc_keystore,
    fk_message_lane   bigint
        constraint fklnoic3soynw9bped4y6iqxjpk
            references dc_message_lane
);

alter table dc_pmode_set
    owner to postgres;

create table dc_transport_step
(
    id                          bigint       not null
        primary key,
    attempt                     integer      not null,
    connector_message_id        varchar(255) not null,
    created                     timestamp    not null,
    final_state_reached         timestamp,
    link_partner_name           varchar(255) not null,
    remote_message_id           varchar(255),
    transport_id                varchar(255),
    transport_system_message_id varchar(255),
    transported_message         text
);

alter table dc_transport_step
    owner to postgres;

create table dc_transport_step_status
(
    state             varchar(255) not null,
    transport_step_id bigint       not null
        constraint fk5g1jngh3f82ialbtnqq99h418
            references dc_transport_step,
    created           timestamp    not null,
    text              text,
    primary key (state, transport_step_id)
);

alter table dc_transport_step_status
    owner to postgres;

create table domibus_connector_action
(
    id           bigint       not null
        primary key,
    action       varchar(255) not null,
    fk_pmode_set bigint
        constraint fk249380r1rr1kt886abx7exj7g
            references dc_pmode_set
);

alter table domibus_connector_action
    owner to postgres;

create table domibus_connector_bigdata
(
    id                   bigint    not null
        primary key,
    checksum             text,
    connector_message_id varchar(255),
    content              oid,
    created              timestamp not null,
    last_access          timestamp,
    mimetype             varchar(255),
    name                 text
);

alter table domibus_connector_bigdata
    owner to postgres;

create table domibus_connector_message
(
    id                   bigint       not null
        primary key,
    backend_message_id   varchar(255),
    backend_name         varchar(255),
    confirmed            timestamp,
    connector_message_id varchar(255) not null
        constraint uk_s9y5ajqyjnjb7gjf2na4ae7ur
            unique,
    conversation_id      varchar(255),
    created              timestamp    not null,
    delivered_gw         timestamp,
    delivered_backend    timestamp,
    direction_source     varchar(20),
    direction_target     varchar(20),
    ebms_message_id      varchar(255),
    gateway_name         varchar(255),
    hash_value           text,
    rejected             timestamp,
    updated              timestamp    not null
);

alter table domibus_connector_message
    owner to postgres;

create table domibus_connector_evidence
(
    id            bigint    not null
        primary key,
    delivered_nat timestamp,
    delivered_gw  timestamp,
    evidence      text,
    type          varchar(255),
    updated       timestamp not null,
    message_id    bigint    not null
        constraint fk4jxg7xyfgfl8txay9slwcafj1
            references domibus_connector_message
);

alter table domibus_connector_evidence
    owner to postgres;

create table domibus_connector_msg_cont
(
    id                    bigint    not null
        primary key,
    checksum              text,
    connector_message_id  varchar(255),
    content               oid,
    content_type          varchar(255),
    created               timestamp not null,
    deleted               timestamp,
    digest                varchar(512),
    payload_description   text,
    payload_identifier    varchar(512),
    payload_mimetype      varchar(255),
    payload_name          varchar(512),
    payload_size          bigint,
    storage_provider_name varchar(255),
    storage_reference_id  varchar(512),
    detached_signature_id bigint
        constraint fk7emymoigdt3qsplyri0dq1xow
            references dc_msgcnt_detsig,
    message_id            bigint
        constraint fkda043m9h695ogla2sg58kxkb1
            references domibus_connector_message
);

alter table domibus_connector_msg_cont
    owner to postgres;

create table domibus_connector_msg_error
(
    id            bigint        not null
        primary key,
    created       timestamp     not null,
    detailed_text text,
    error_message varchar(2048) not null,
    error_source  text,
    message_id    bigint        not null
        constraint fki0wrarse6i0t5nj4r82p1e4n
            references domibus_connector_message
);

alter table domibus_connector_msg_error
    owner to postgres;

create table domibus_connector_party
(
    id            bigint       not null
        primary key,
    party_id      varchar(255) not null,
    party_id_type varchar(512) not null,
    identifier    varchar(255),
    role          varchar(255),
    role_type     varchar(50),
    fk_pmode_set  bigint
        constraint fkal7yndgaiapslndruuu48g34
            references dc_pmode_set
);

alter table domibus_connector_party
    owner to postgres;

create table domibus_connector_property
(
    id             integer       not null
        primary key,
    property_name  varchar(2048) not null,
    property_value varchar(2048)
);

alter table domibus_connector_property
    owner to postgres;

create table domibus_connector_seq_store
(
    seq_name  varchar(255) not null
        primary key,
    seq_value bigint
);

alter table domibus_connector_seq_store
    owner to postgres;

create table domibus_connector_service
(
    id           bigint       not null
        primary key,
    service      varchar(255) not null,
    service_type varchar(255),
    fk_pmode_set bigint
        constraint fkbj0847csnu0cbi0u92j81lrn0
            references dc_pmode_set
);

alter table domibus_connector_service
    owner to postgres;

create table domibus_connector_message_info
(
    id               bigint    not null
        primary key,
    created          timestamp not null,
    final_recipient  varchar(2048),
    original_sender  varchar(2048),
    updated          timestamp not null,
    fk_action        bigint
        constraint fkadkw4ku0o3a3x80felptltnfr
            references domibus_connector_action,
    fk_from_party_id bigint
        constraint fkhbvkhb64ltjr9pjpvds09t6h7
            references domibus_connector_party,
    message_id       bigint    not null
        constraint fkuvd19003ob697v6e8ovgw140
            references domibus_connector_message,
    fk_service       bigint
        constraint fkoltsh7wsh3a0pjg7aagltlbbo
            references domibus_connector_service,
    fk_to_party_id   bigint
        constraint fka5oheqmhn4eu4j1yuyi3femsh
            references domibus_connector_party
);

alter table domibus_connector_message_info
    owner to postgres;

create table domibus_connector_user
(
    id                     bigint      not null
        primary key,
    created                timestamp   not null,
    grace_logins_used      bigint      not null,
    locked                 boolean     not null,
    number_of_grace_logins bigint      not null,
    role                   varchar(50) not null,
    username               varchar(50) not null
);

alter table domibus_connector_user
    owner to postgres;

create table domibus_connector_user_pwd
(
    id          bigint        not null
        primary key,
    created     timestamp     not null,
    current_pwd boolean       not null,
    initial_pwd boolean       not null,
    password    varchar(1024) not null,
    salt        varchar(512)  not null,
    user_id     bigint        not null
        constraint fk62doe366dlq21rv9ysf7hfk4e
            references domibus_connector_user
);

alter table domibus_connector_user_pwd
    owner to postgres;
