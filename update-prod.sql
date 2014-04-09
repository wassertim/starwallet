# Users schema

# --- !Ups
RENAME TABLE  accounts TO identities;

create TABLE accounts (
  identity_id bigint(20),
  stars_count bigint(20),
  sync_date timestamp,
  PRIMARY KEY (identity_id)
);

ALTER TABLE cards
  CHANGE account_id identity_id INT,
  ADD pin_code varchar(6) AFTER last_update_date;

ALTER TABLE coupons
  CHANGE account_id identity_id INT;

# --- !Downs
RENAME TABLE  identities TO accounts;
ALTER TABLE accounts
  DROP stars_count,
  DROP sync_date;

ALTER TABLE cards
  DROP pin_code,
  CHANGE identity_id account_id INT;

ALTER TABLE coupons
  CHANGE identity_id account_id INT;