# Users schema

# --- !Ups

CREATE TABLE user_settings (
    user_id bigint(20) NOT NULL,
    phone varchar(20),
    first_name varchar(50),
    last_name varchar(50)
    PRIMARY KEY (user_id)
);

# --- !Downs

DROP TABLE user_settings;