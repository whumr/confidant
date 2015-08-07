/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50612
Source Host           : localhost:3306
Source Database       : benz

Target Server Type    : MYSQL
Target Server Version : 50612
File Encoding         : 65001

Date: 2015-08-07 17:00:39
*/

CREATE DATABASE `benz` /*!40100 DEFAULT CHARACTER SET utf8 */

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for country
-- ----------------------------
DROP TABLE IF EXISTS `country`;
CREATE TABLE `country` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` varchar(10) DEFAULT NULL COMMENT 'local:国内,gat:港澳台,dny:东南亚,europe:欧洲,america:美洲',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dates
-- ----------------------------
DROP TABLE IF EXISTS `dates`;
CREATE TABLE `dates` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `start_date` date NOT NULL,
  `start_place` int(10) unsigned DEFAULT NULL,
  `days` tinyint(3) unsigned NOT NULL,
  `budget` int(10) unsigned DEFAULT NULL,
  `member_count` tinyint(3) unsigned DEFAULT NULL,
  `travel_type` char(1) DEFAULT NULL COMMENT 'c:汽车,t:火车,p:飞机,d:自驾,b:自行车,w:徒步',
  `comments` varchar(500) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for member
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `account` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `nick_name` varchar(30) NOT NULL,
  `image_url` varchar(30) DEFAULT NULL,
  `sex` char(1) DEFAULT NULL COMMENT 'M/F',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for routes
-- ----------------------------
DROP TABLE IF EXISTS `routes`;
CREATE TABLE `routes` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `date_id` int(10) unsigned NOT NULL,
  `viewspot_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for viewspot
-- ----------------------------
DROP TABLE IF EXISTS `viewspot`;
CREATE TABLE `viewspot` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `type` char(1) DEFAULT 'c',
  `country` tinyint(3) unsigned DEFAULT NULL,
  `province` tinyint(3) unsigned DEFAULT NULL,
  `city` int(10) unsigned DEFAULT NULL,
  `comments` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
