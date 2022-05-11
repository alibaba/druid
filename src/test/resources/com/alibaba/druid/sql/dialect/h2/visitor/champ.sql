CREATE TABLE `champ_app_mgr_d`  (
    `RID` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `LABEL_TYPE`  varchar(8) NULL,
    `LABEL_KEY`  varchar(32) NULL,
    `LABEL_VALUE`  varchar(32) NULL,
    `LABEL_SORT`  varchar(32) NULL,
    PRIMARY KEY (`RID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

ALTER TABLE `champ_app_mgr_d`
    MODIFY COLUMN `LABEL_KEY`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL AFTER `LABEL_TYPE`,
    MODIFY COLUMN `LABEL_VALUE`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL AFTER `LABEL_KEY`;