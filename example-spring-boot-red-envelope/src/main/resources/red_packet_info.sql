/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : 127.0.0.1:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 23/10/2020 18:02:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for red_packet_info
-- ----------------------------
DROP TABLE IF EXISTS `red_packet_info`;
CREATE TABLE `red_packet_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) DEFAULT NULL,
  `red_packet_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `remaining_amount` decimal(19, 2) DEFAULT NULL,
  `remaining_packet` int(11) DEFAULT NULL,
  `total_amount` decimal(19, 2) DEFAULT NULL,
  `total_packet` int(11) DEFAULT NULL,
  `uid` int(11) DEFAULT NULL,
  `update_time` datetime(0) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `inx_red_packet_id`(`red_packet_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for red_packet_record
-- ----------------------------
DROP TABLE IF EXISTS `red_packet_record`;
CREATE TABLE `red_packet_record`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` decimal(19, 2) DEFAULT NULL,
  `create_time` datetime(0) DEFAULT NULL,
  `img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `nick_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `red_packet_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `uid` int(11) DEFAULT NULL,
  `update_time` datetime(0) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 186 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
