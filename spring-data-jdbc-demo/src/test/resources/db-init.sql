--SET MODE=MySQL;
SET FOREIGN_KEY_CHECKS=0;
drop table if exists user_info;
CREATE TABLE IF NOT EXISTS "user_info"(
  id bigint NOT NULL auto_increment ,
  user_code varchar(36) DEFAULT NULL,
  user_name varchar(30) DEFAULT NULL,
  `mobile_no` varchar(13) DEFAULT NULL ,
  `address` varchar(20) DEFAULT NULL ,
  `version` int(11)  DEFAULT '1' Not NULL ,
  `is_delete` tinyint(1) DEFAULT '0' Not NULL ,
  `is_valid` tinyint(1) DEFAULT '0' Not NULL ,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `head_image_data` blob  DEFAULT NULL,
  PRIMARY KEY (id)
) ;
 insert into "user_info"(user_code,user_name,mobile_no,address,create_time) values ('u000','change','000','tang',now());
 insert into "user_info"(user_code,user_name,mobile_no,address,create_time) values ('u001','Jack','001','hang zhou',now());
 insert into "user_info"(user_code,user_name,mobile_no,address,create_time,update_time) values ('u002','Elon Reeve Musk','002','Mars',now(),now());
 insert into "user_info"(user_code,user_name,mobile_no,address,create_time) values ('u003','Bill Gates','003',null,now());