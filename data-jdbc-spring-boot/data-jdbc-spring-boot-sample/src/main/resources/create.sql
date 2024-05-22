DROP TABLE `test_user`;
CREATE TABLE `test_user`
(
    `id`                bigint                     NOT NULL AUTO_INCREMENT,
    `version`           bigint                     NOT NULL,
    `ent_code`          varchar(255) DEFAULT NULL,
    `create_time`       datetime     DEFAULT NULL,
    `code`              varchar(255) DEFAULT NULL,
    `name`              varchar(255) DEFAULT NULL,
    `email`             varchar(255) DEFAULT NULL,
    `age`               bigint       DEFAULT NULL,
    `flag`              bit(1)       DEFAULT NULL,
    `flag_string`       bit(1)       DEFAULT NULL,
    `accounting_period` DATE                       NULL COMMENT '会计期间',
    `doc_status`        ENUM ('DRAFT', 'AUDITING') NULL COMMENT '单据状态',
    PRIMARY KEY (`id`)
);