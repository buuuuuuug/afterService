-- ----------------------------
-- Table structure for aftersales_service
-- ----------------------------
DROP TABLE IF EXISTS `aftersales_service`;
CREATE TABLE `aftersales_service` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(11) unsigned DEFAULT NULL,
  `gmt_create` datetime(2) DEFAULT NULL,
  `gmt_modified` datetime(2) DEFAULT NULL,
  `is_deleted` tinyint(1) unsigned DEFAULT '0',
  `goods_type` int(8) DEFAULT NULL,
  `apply_time` datetime(2) DEFAULT NULL,
  `apply_reason` varchar(255) DEFAULT NULL,
  `end_time` datetime(2) DEFAULT NULL,
  `type` int(8) DEFAULT NULL,
  `is_applied` tinyint(1) unsigned DEFAULT '0',
  `status` tinyint(2) unsigned DEFAULT '0',
  `number` int(8) unsigned DEFAULT '1',
  `order_item_id` bigint(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
);