CREATE TABLE `champ_app_mgr_d`  (
    `RID` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `LABEL_TYPE`  varchar(8) NULL,
    `LABEL_KEY`  varchar(32) NULL,
    `LABEL_VALUE`  varchar(32) NULL,
    `LABEL_SORT`  varchar(32) NULL,
    PRIMARY KEY (`RID`) USING BTREE,
    INDEX `idx_wokb_tmpl_detl_d_1`(`LABEL_KEY`, `LABEL_VALUE`, `LABEL_SORT`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

ALTER TABLE `champ_app_mgr_d`
    MODIFY COLUMN `LABEL_KEY` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL AFTER `LABEL_TYPE`,
    MODIFY COLUMN `LABEL_VALUE` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL AFTER `LABEL_KEY`;

-- type could null
ALTER TABLE champ_app_mgr_d MODIFY LABEL_TYPE VARCHAR(255) NULL;
ALTER TABLE champ_app_mgr_d ADD UNIQUE INDEX UNIQUE_TYPE_KEY(LABEL_TYPE, LABEL_KEY);