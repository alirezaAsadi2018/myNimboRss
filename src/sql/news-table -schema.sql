create database if not exists news;
use news;
create table if not exists news_table(id int primary key auto_increment, title text, dscp text, agency text, dt datetime) default charset = utf8mb4;




