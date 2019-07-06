create database if not exists news;
use news;
create table if not exists url_table(id int primary key auto_increment, url text) default charset = utf8mb4;
delete from url_table;
insert into url_table (url) values ("https://news.google.com/rss");
insert into url_table (url) values ("https://www.tabnak.ir/fa/rss/allnews");
insert into url_table (url) values ("https://www.farsnews.com/rss");
insert into url_table (url) values ("https://www.varzesh3.com/rss/all");
insert into url_table (url) values ("https://www.yjc.ir/en/rss/allnews");



