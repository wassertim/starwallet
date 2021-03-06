# Users schema

# --- !Ups

CREATE TABLE users (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

create TABLE identities (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    user_id bigint(20) NOT NULL,
    user_name varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    is_active bit not null default 0,
    activation_email varchar(255) NULL,
    PRIMARY KEY (id)
);

create TABLE coupons (
    number varchar(12) not null,
    identity_id bigint (20) not null,
    is_active bit not null default 0,
    issue_date timestamp not null,
    expiration_date timestamp not null,
    type varchar(255) not null,
    url_key varchar(127) not null default '',
    primary key (number)
);

CREATE TABLE cards (
    number varchar(12) not null,
    identity_id bigint (20) not null,
    is_active bit not null default 1,
    activation_date timestamp not null,
    last_transaction_date timestamp not null,
    balance decimal not null,
    last_update_date timestamp not null,
    pin_code varchar(6),
    primary key (number)
);

CREATE TABLE transactions (
    card_number varchar (12) not null,
    `date` timestamp not null,
    place varchar(255) not null,
    `type` varchar(127) not null,
    amount decimal not null,
    balance decimal not null,
    primary key (card_number, `date`)
);

create TABLE accounts (
  identity_id bigint(20),
  stars_count bigint(20),
  sync_date timestamp,
  email varchar(64),
  PRIMARY KEY (identity_id)
);

CREATE TABLE pin_codes (
  card_number varchar(12) not null,
  pin_code varchar(6) not null,
  primary key (card_number)
);

CREATE TABLE user_settings (
  user_id bigint(20) NOT NULL,
  phone varchar(20),
  first_name varchar(50),
  last_name varchar(50),
  email_domain varchar(128),
  PRIMARY KEY (user_id)
);
# --- !Downs
DROP TABLE user_settings;
DROP TABLE pin_codes;
DROP TABLE users;
DROP TABLE identities;
DROP TABLE accounts;
DROP TABLE coupons;
DROP TABLE cards;
DROP TABLE transactions;