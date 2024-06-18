drop table if exists FILMS_DIRECTORS CASCADE;
drop table if exists FILMS_GENRES CASCADE;
drop table if exists FRIENDSHIP CASCADE;
drop table if exists LIKES CASCADE;
drop table if exists MPA CASCADE;
drop table if exists GENRES CASCADE;
drop table if exists DIRECTORS CASCADE;
drop table if exists FILMS CASCADE;
drop table if exists USERS CASCADE;
drop table if exists REVIEW_LIKES CASCADE;
drop table if exists REVIEW_DISLIKES CASCADE;
drop table if exists REVIEWS CASCADE;

create table if not exists MPA
(
MPA_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
NAME   CHARACTER VARYING(20) NOT NULL
);

create table if not exists GENRES
(
GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
NAME     CHARACTER VARYING(20) NOT NULL
);

create table if not exists DIRECTORS
(
    DIRECTOR_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME     CHARACTER VARYING(20) NOT NULL
);

create table if not exists FILMS
(
    FILM_ID      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME         CHARACTER VARYING(255) NOT NULL,
    DESCRIPTION  CHARACTER VARYING(200) NOT NULL,
    RELEASE_DATE DATE,
    DURATION     INTEGER NOT NULL,
    MPA_ID       INTEGER NOT NULL REFERENCES MPA (MPA_ID)
);

create table if not exists USERS
(
    USER_ID  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL    CHARACTER VARYING(255) NOT NULL,
    LOGIN    CHARACTER VARYING(100) NOT NULL,
    NAME     CHARACTER VARYING(255),
    BIRTHDAY DATE
);

create table if not exists FILMS_GENRES
(
    FILM_ID  BIGINT  REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    GENRE_ID INTEGER REFERENCES GENRES (GENRE_ID) ON DELETE CASCADE
);

create table if not exists FRIENDSHIP
(
    USER_ID   BIGINT NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    FRIEND_ID BIGINT NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    IS_MUTUAL BOOLEAN NOT NULL,
    constraint FRIENDSHIP_PK
        primary key (USER_ID, FRIEND_ID)
);

create table if not exists LIKES
(
    FILM_ID BIGINT  NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    USER_ID BIGINT NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

create table if not exists REVIEWS
(
    REVIEW_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    FILM_ID BIGINT  NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    USER_ID BIGINT NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    CONTENT CHARACTER VARYING(200) NOT NULL,
    USEFUL INTEGER NOT NULL DEFAULT 0,
    IS_POSITIVE BOOLEAN NOT NULL
);

create table if not exists REVIEW_LIKES
(
    REVIEW_ID BIGINT NOT NULL REFERENCES REVIEWS (REVIEW_ID) ON DELETE CASCADE,
    USER_ID BIGINT NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

create table if not exists REVIEW_DISLIKES
(
    REVIEW_ID BIGINT NOT NULL REFERENCES REVIEWS (REVIEW_ID) ON DELETE CASCADE,
    USER_ID BIGINT NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

create table if not exists FILMS_DIRECTORS
(
    FILM_ID     BIGINT  REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    DIRECTOR_ID INTEGER REFERENCES DIRECTORS (DIRECTOR_ID) ON DELETE CASCADE
);