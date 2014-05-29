# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table o_users (
  id                        bigint not null,
  username                  varchar(255),
  password                  varchar(255),
  email                     varchar(255),
  permission                integer,
  use_full_time             boolean,
  show_emotes               boolean,
  constraint ck_o_users_permission check (permission in (0,1,2,3,4)),
  constraint pk_o_users primary key (id))
;

create sequence o_users_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists o_users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists o_users_seq;

