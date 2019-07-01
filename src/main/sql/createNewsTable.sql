SET @username = 'admin', @password = 'admin';    
GRANT ALL PRIVILEGES ON *.* TO 'admin'@'localhost' IDENTIFIED BY 'admin';
create database news;
use news;
create table news_table(id int primary key auto_increment, title text, dscp text);



