/*
Navicat MySQL Data Transfer

Source Server         : localhost_test
Source Server Version : 50612
Source Host           : localhost:3306
Source Database       : bmw

Target Server Type    : MYSQL
Target Server Version : 50612
File Encoding         : 65001

Date: 2015-01-30 17:18:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for bmw_location
-- ----------------------------
DROP TABLE IF EXISTS `bmw_location`;
CREATE TABLE `bmw_location` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `longitude` decimal(15,10) unsigned DEFAULT NULL,
  `latitude` decimal(15,10) unsigned DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_time` (`user_id`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bmw_msg_board
-- ----------------------------
DROP TABLE IF EXISTS `bmw_msg_board`;
CREATE TABLE `bmw_msg_board` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `creator_id` int(11) NOT NULL,
  `receiverr_id` int(10) unsigned NOT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `images` varchar(200) DEFAULT '' COMMENT '图片地址，多个用逗号分隔，有数量限制',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `state` char(1) DEFAULT 'N' COMMENT 'N:新建,R:已读,D:删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bmw_note
-- ----------------------------
DROP TABLE IF EXISTS `bmw_note`;
CREATE TABLE `bmw_note` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `images` varchar(200) DEFAULT '' COMMENT '图片地址，多个用逗号分隔，有数量限制',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for bmw_user
-- ----------------------------
DROP TABLE IF EXISTS `bmw_user`;
CREATE TABLE `bmw_user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `account` varchar(30) NOT NULL,
  `account_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账号类型,1:手机号',
  `password` varchar(50) NOT NULL,
  `nick_name` varchar(30) NOT NULL,
  `image_url` varchar(30) DEFAULT NULL,
  `sex` char(1) DEFAULT NULL COMMENT 'M/F',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
