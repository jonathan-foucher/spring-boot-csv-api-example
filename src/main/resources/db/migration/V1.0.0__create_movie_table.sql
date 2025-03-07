drop table if exists movie;
create table movie (
    id              bigserial       primary key,
    title           varchar(100)    not null,
    release_date    date            not null
);
