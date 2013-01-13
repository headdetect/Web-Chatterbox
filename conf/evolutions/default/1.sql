# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table o_users (
  id                        bigint auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  email                     varchar(255),
  permission                integer,
  use_full_time             tinyint(1) default 0,
  show_emotes               tinyint(1) default 0,
  ip_address                varchar(255),
  constraint ck_o_users_permission check (permission in (0,1,2,3,4)),
  constraint pk_o_users primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table o_users;

SET FOREIGN_KEY_CHECKS=1;

