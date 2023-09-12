drop database if exists okProject;
create database okProject;
use okProject;

drop table if exists member;
create table member(
name varchar(20),
id varchar(20),
password varchar(20),
email varchar(30),
primary key(id)
);
