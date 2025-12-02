-- --------------------------------------------------------
-- init script for hm-item.seckill
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 创建数据库（如果还没创建）
CREATE DATABASE IF NOT EXISTS `hm-item`
  /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */
  /*!80016 DEFAULT ENCRYPTION='N' */;

USE `hm-item`;

-- --------------------------------------------------------
-- 创建 seckill 表，用于存储秒杀优惠券（商品）
-- 字段基本与 item 相同，但去掉 image、category、brand、spec、sold、comment_count、isAD
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `seckill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '秒杀商品id',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'SKU名称',
  `price` int NOT NULL DEFAULT '0' COMMENT '价格（分）',
  `stock` int UNSIGNED NOT NULL COMMENT '库存数量',
  `status` int DEFAULT '2' COMMENT '商品状态 1-正常，2-下架，3-删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creater` bigint DEFAULT NULL COMMENT '创建人',
  `updater` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `status` (`status`) USING BTREE,
  KEY `updated` (`update_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1
  DEFAULT CHARSET=utf8mb3 ROW_FORMAT=COMPACT
  COMMENT='秒杀优惠券表';

-- 可选：初始化一些秒杀数据（示例）
DELETE FROM `seckill`;
INSERT INTO `seckill`
  (`id`, `name`, `price`, `stock`, `status`, `create_time`, `update_time`, `creater`, `updater`)
VALUES
  (1, '20元无门槛优惠券', 2000, 100, 1, '2023-01-01 00:00:00', '2023-01-01 00:00:00', NULL, NULL);

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
