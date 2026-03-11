CREATE TABLE `mysql_sequence_number`
(
    `id`          int unsigned NOT NULL AUTO_INCREMENT,
    `ent_code`    varchar(240) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '没有实际作用',
    `group_id`    varchar(240) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `seq_num`     bigint                                                        DEFAULT NULL,
    `reverse`     int                                                           DEFAULT NULL,
    `create_time` datetime(3)                                                      DEFAULT NULL,
    `update_time` datetime(3)                                                      DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `index_0` (`group_id`,`seq_num`) USING BTREE,
    KEY           `index_1` (`group_id`) USING BTREE,
    KEY           `index_3` (`group_id`,`reverse`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=61483 ROW_FORMAT=DYNAMIC COMMENT='序列号生成序列表';