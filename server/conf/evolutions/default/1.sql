# --- !Ups

create table "USER" ("ID" BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY, "FACEBOOK_ID" VARCHAR(255) NOT NULL, "RATING" INT NOT NULL DEFAULT 1200);
create table "GAME" ("ID" BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY, "USER1" BIGINT NOT NULL, "USER2" BIGINT NOT NULL, "RESULT" INT NOT NULL)

# --- !Downs

drop table "USER";
drop table "GAME"