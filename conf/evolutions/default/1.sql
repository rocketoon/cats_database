# --- First database schema

# --- !Ups
create sequence cat_seq start with 1000;

CREATE TABLE cat (
  id bigint not null default nextval('cat_seq'),
  name varchar(255) not null,
  color varchar(255),
  picture bytea,
  breed varchar(255),
  gender varchar(1),
  
  constraint pc_cat primary key (id)
);

# --- !Downs

drop table cat;

drop sequence cat_seq;

