# --- First database schema

# --- !Ups

set ignorecase true;

CREATE TABLE cat (
  id bigint not null,
  name varchar(255) not null,
  color varchar(255),
  picture blob,
  breed varchar(255),
  gender varchar(1),
  
  constraint pc_cat primary key (id)
);






create sequence cat_seq start with 1000;

# --- !Downs



drop table cat;



drop sequence cat_seq;

