# Users schema

# --- !Ups

CREATE TABLE users (
    id bigserial,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    primary key(id)
);

create TABLE accounts (
    id bigserial,
    user_id bigint NOT NULL,
    user_name varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

create TABLE coupons (
    number varchar(12) not null primary key,
    account_id bigint not null,
    is_active boolean not null,
    issue_date timestamp not null,
    expiration_date timestamp not null,
    type varchar(255) not null,
    url_key varchar(127) not null default ''
);

CREATE TABLE cards (
    number varchar(12) not null primary key,
    account_id bigint not null,
    is_active boolean not null,
    activation_date timestamp not null,
    last_transaction_date timestamp not null,
    balance decimal not null,
    last_update_date timestamp not null
);

CREATE TABLE transactions (
    id bigserial primary key,
    card_number varchar (12) not null,
    date timestamp not null,
    place varchar(255) not null,
    type varchar(127) not null,
    amount decimal not null,
    balance decimal not null
);



# --- !Downs

DROP TABLE users;
DROP TABLE accounts;
DROP TABLE coupons;
DROP TABLE cards;
DROP TABLE transactions;