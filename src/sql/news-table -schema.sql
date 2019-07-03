create database if not exists news;
use news;
drop table news_table;
create table if not exists news_table(id int primary key auto_increment, title text, dscp text, link text, dt datetime) default charset = utf8mb4;




