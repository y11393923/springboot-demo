/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 50559
 Source Host           : localhost:3306
 Source Schema         : shiro_demo

 Target Server Type    : MySQL
 Target Server Version : 50559
 File Encoding         : 65001

 Date: 09/12/2018 21:11:53
 author https://blog.csdn.net/chen_2890
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `pid` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `keyword` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`pid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1005 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1001, '添加功能', 'add', '添加');
INSERT INTO `permission` VALUES (1002, '查询功能', 'select', '查询');
INSERT INTO `permission` VALUES (1003, '更新功能', 'update', '更新');
INSERT INTO `permission` VALUES (1004, '删除功能', 'delete', '删除');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `rid` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `keyword` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`rid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1004 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1001, '普通用户', 'Commom', '普通用户');
INSERT INTO `role` VALUES (1002, '一般会员', 'Member', '会员用户');
INSERT INTO `role` VALUES (1003, '超级会员', 'Vip', 'VIP用户');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1001, 1001);
INSERT INTO `role_permission` VALUES (1001, 1002);
INSERT INTO `role_permission` VALUES (1002, 1001);
INSERT INTO `role_permission` VALUES (1002, 1002);
INSERT INTO `role_permission` VALUES (1002, 1003);
INSERT INTO `role_permission` VALUES (1003, 1002);
INSERT INTO `role_permission` VALUES (1003, 1001);
INSERT INTO `role_permission` VALUES (1003, 1003);
INSERT INTO `role_permission` VALUES (1003, 1004);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `age` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'jack', 'ae7f487d56152e165afdfd87c2b819a5', 18);
INSERT INTO `user` VALUES (2, 'tom', 'cc804223edc8063d7b3d9dc94b81fba3', 19);
INSERT INTO `user` VALUES (3, 'rose', 'c89f94fdfb8ae723413296a03c0f8d3b', 20);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `role_id` int(11) NOT NULL,
  `uid` int(11) NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1001, 1);
INSERT INTO `user_role` VALUES (1001, 2);
INSERT INTO `user_role` VALUES (1002, 2);
INSERT INTO `user_role` VALUES (1001, 3);
INSERT INTO `user_role` VALUES (1002, 3);
INSERT INTO `user_role` VALUES (1003, 3);

SET FOREIGN_KEY_CHECKS = 1;
