/*
SQLyog Ultimate v9.20 
MySQL - 5.1.41 : Database - xwiki
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`xwiki` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `xwiki`;

/*Table structure for table `activitystream_events` */

DROP TABLE IF EXISTS `activitystream_events`;

CREATE TABLE `activitystream_events` (
  `ase_eventid` varchar(48) NOT NULL,
  `ase_requestid` varchar(2000) DEFAULT NULL,
  `ase_stream` varchar(255) DEFAULT NULL,
  `ase_date` datetime DEFAULT NULL,
  `ase_priority` int(11) DEFAULT NULL,
  `ase_type` varchar(255) DEFAULT NULL,
  `ase_application` varchar(255) DEFAULT NULL,
  `ase_user` varchar(255) DEFAULT NULL,
  `ase_wiki` varchar(255) DEFAULT NULL,
  `ase_space` varchar(255) DEFAULT NULL,
  `ase_page` varchar(255) DEFAULT NULL,
  `ase_hidden` bit(1) DEFAULT NULL,
  `ase_url` varchar(2000) DEFAULT NULL,
  `ase_title` varchar(2000) DEFAULT NULL,
  `ase_body` varchar(2000) DEFAULT NULL,
  `ase_version` varchar(30) DEFAULT NULL,
  `ase_param1` varchar(2000) DEFAULT NULL,
  `ase_param2` varchar(2000) DEFAULT NULL,
  `ase_param3` varchar(2000) DEFAULT NULL,
  `ase_param4` varchar(2000) DEFAULT NULL,
  `ase_param5` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`ase_eventid`),
  KEY `EVENT_TYPE` (`ase_type`),
  KEY `EVENT_PRIORITY` (`ase_priority`),
  KEY `EVENT_WIKI` (`ase_wiki`),
  KEY `EVENT_DATE` (`ase_date`),
  KEY `EVENT_PAGE` (`ase_page`),
  KEY `EVENT_USER` (`ase_user`),
  KEY `EVENT_SPACE` (`ase_space`),
  KEY `EVENT_STREAM` (`ase_stream`),
  KEY `EVENT_APP` (`ase_application`),
  KEY `EVENT_HIDDEN` (`ase_hidden`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `feeds_aggregatorgroup` */

DROP TABLE IF EXISTS `feeds_aggregatorgroup`;

CREATE TABLE `feeds_aggregatorgroup` (
  `agg_id` int(11) NOT NULL,
  `agg_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`agg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `feeds_aggregatorurl` */

DROP TABLE IF EXISTS `feeds_aggregatorurl`;

CREATE TABLE `feeds_aggregatorurl` (
  `agg_id` bigint(20) NOT NULL,
  `agg_name` longtext,
  `agg_url` longtext,
  `agg_date` datetime DEFAULT NULL,
  `agg_nb` int(11) DEFAULT NULL,
  PRIMARY KEY (`agg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `feeds_aggregatorurlgroups` */

DROP TABLE IF EXISTS `feeds_aggregatorurlgroups`;

CREATE TABLE `feeds_aggregatorurlgroups` (
  `agl_id` bigint(20) NOT NULL,
  `agl_value` varchar(255) DEFAULT NULL,
  `agl_number` int(11) NOT NULL,
  PRIMARY KEY (`agl_id`,`agl_number`),
  KEY `FK7B845B068E772CC` (`agl_id`),
  CONSTRAINT `FK7B845B068E772CC` FOREIGN KEY (`agl_id`) REFERENCES `feeds_aggregatorurl` (`agg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `feeds_feedentry` */

DROP TABLE IF EXISTS `feeds_feedentry`;

CREATE TABLE `feeds_feedentry` (
  `fee_id` bigint(20) NOT NULL,
  `fee_title` longtext,
  `fee_author` longtext,
  `fee_feedurl` longtext,
  `fee_feedname` longtext,
  `fee_url` longtext,
  `fee_category` longtext,
  `fee_content` longtext,
  `fee_fullcontent` longtext,
  `fee_xml` longtext,
  `fee_date` datetime DEFAULT NULL,
  `fee_flag` int(11) DEFAULT '0',
  `fee_read` int(11) DEFAULT '0',
  PRIMARY KEY (`fee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `feeds_feedentrytags` */

DROP TABLE IF EXISTS `feeds_feedentrytags`;

CREATE TABLE `feeds_feedentrytags` (
  `fet_id` bigint(20) NOT NULL,
  `fet_value` varchar(255) DEFAULT NULL,
  `fet_number` int(11) NOT NULL,
  PRIMARY KEY (`fet_id`,`fet_number`),
  KEY `FKBE0037834855150F` (`fet_id`),
  CONSTRAINT `FKBE0037834855150F` FOREIGN KEY (`fet_id`) REFERENCES `feeds_feedentry` (`fee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `feeds_keyword` */

DROP TABLE IF EXISTS `feeds_keyword`;

CREATE TABLE `feeds_keyword` (
  `key_id` int(11) NOT NULL,
  `key_name` varchar(255) DEFAULT NULL,
  `key_group` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`key_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikiattachment` */

DROP TABLE IF EXISTS `xwikiattachment`;

CREATE TABLE `xwikiattachment` (
  `XWA_ID` bigint(20) NOT NULL,
  `XWA_DOC_ID` bigint(20) DEFAULT NULL,
  `XWA_FILENAME` varchar(255) NOT NULL,
  `XWA_SIZE` int(11) DEFAULT NULL,
  `XWA_DATE` datetime NOT NULL,
  `XWA_AUTHOR` varchar(255) DEFAULT NULL,
  `XWA_VERSION` varchar(255) NOT NULL,
  `XWA_COMMENT` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`XWA_ID`),
  KEY `ATT_DATE` (`XWA_DATE`),
  KEY `ATT_DOC_ID` (`XWA_DOC_ID`),
  KEY `ATT_AUTHOR` (`XWA_AUTHOR`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikiattachment_archive` */

DROP TABLE IF EXISTS `xwikiattachment_archive`;

CREATE TABLE `xwikiattachment_archive` (
  `XWA_ID` bigint(20) NOT NULL,
  `XWA_ARCHIVE` longblob,
  PRIMARY KEY (`XWA_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikiattachment_content` */

DROP TABLE IF EXISTS `xwikiattachment_content`;

CREATE TABLE `xwikiattachment_content` (
  `XWA_ID` bigint(20) NOT NULL,
  `XWA_CONTENT` longblob NOT NULL,
  PRIMARY KEY (`XWA_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikiattrecyclebin` */

DROP TABLE IF EXISTS `xwikiattrecyclebin`;

CREATE TABLE `xwikiattrecyclebin` (
  `XDA_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `XDA_DOCID` bigint(20) NOT NULL,
  `XDA_FILENAME` varchar(255) NOT NULL,
  `XDA_DATE` datetime NOT NULL,
  `XDA_DOC_NAME` varchar(255) DEFAULT NULL,
  `XDA_DELETER` varchar(255) DEFAULT NULL,
  `XDA_XML` longtext NOT NULL,
  PRIMARY KEY (`XDA_ID`),
  UNIQUE KEY `XDA_DOCID` (`XDA_DOCID`,`XDA_FILENAME`,`XDA_DATE`),
  KEY `XDA_DOC_NAME` (`XDA_DOC_NAME`),
  KEY `XDA_FILENAME` (`XDA_FILENAME`),
  KEY `XDA_DELETER` (`XDA_DELETER`),
  KEY `XDA_DATE` (`XDA_DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikicomments` */

DROP TABLE IF EXISTS `xwikicomments`;

CREATE TABLE `xwikicomments` (
  `XWC_ID` bigint(20) NOT NULL,
  `XWC_AUTHOR` varchar(255) DEFAULT NULL,
  `XWC_HIGHLIGHT` longtext,
  `XWC_COMMENT` longtext,
  `XWP_REPLYTO` int(11) DEFAULT NULL,
  `XWP_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`XWC_ID`),
  KEY `COMMENT_DATE` (`XWP_DATE`),
  KEY `COMMENT_AUTHOR` (`XWC_AUTHOR`),
  KEY `COMMENT_REPLYTO` (`XWP_REPLYTO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikidates` */

DROP TABLE IF EXISTS `xwikidates`;

CREATE TABLE `xwikidates` (
  `XWS_ID` bigint(20) NOT NULL,
  `XWS_NAME` varchar(255) NOT NULL,
  `XWS_VALUE` datetime DEFAULT NULL,
  PRIMARY KEY (`XWS_ID`,`XWS_NAME`),
  KEY `FKDEAEAB5D3433FD87` (`XWS_ID`,`XWS_NAME`),
  KEY `XWDATE_NAME` (`XWS_NAME`),
  KEY `XWDATE_VALUE` (`XWS_VALUE`),
  CONSTRAINT `FKDEAEAB5D3433FD87` FOREIGN KEY (`XWS_ID`, `XWS_NAME`) REFERENCES `xwikiproperties` (`XWP_ID`, `XWP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikidbversion` */

DROP TABLE IF EXISTS `xwikidbversion`;

CREATE TABLE `xwikidbversion` (
  `XWV_VERSION` int(11) NOT NULL,
  PRIMARY KEY (`XWV_VERSION`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikidoc` */

DROP TABLE IF EXISTS `xwikidoc`;

CREATE TABLE `xwikidoc` (
  `XWD_ID` bigint(20) NOT NULL,
  `XWD_FULLNAME` varchar(255) NOT NULL,
  `XWD_NAME` varchar(255) NOT NULL,
  `XWD_TITLE` varchar(255) NOT NULL,
  `XWD_LANGUAGE` varchar(5) DEFAULT NULL,
  `XWD_DEFAULT_LANGUAGE` varchar(5) DEFAULT NULL,
  `XWD_TRANSLATION` int(11) NOT NULL,
  `XWD_DATE` datetime NOT NULL,
  `XWD_CONTENT_UPDATE_DATE` datetime NOT NULL,
  `XWD_CREATION_DATE` datetime NOT NULL,
  `XWD_AUTHOR` varchar(255) NOT NULL,
  `XWD_CONTENT_AUTHOR` varchar(255) NOT NULL,
  `XWD_CREATOR` varchar(255) NOT NULL,
  `XWD_WEB` varchar(255) NOT NULL,
  `XWD_CONTENT` longtext NOT NULL,
  `XWD_VERSION` varchar(255) NOT NULL,
  `XWD_CUSTOM_CLASS` varchar(255) NOT NULL,
  `XWD_PARENT` varchar(511) NOT NULL,
  `XWD_CLASS_XML` longtext,
  `XWD_ELEMENTS` int(11) NOT NULL,
  `XWD_DEFAULT_TEMPLATE` varchar(255) NOT NULL,
  `XWD_VALIDATION_SCRIPT` varchar(255) NOT NULL,
  `XWD_COMMENT` varchar(1023) NOT NULL,
  `XWD_MINOREDIT` bit(1) NOT NULL,
  `XWD_SYNTAX_ID` varchar(50) DEFAULT NULL,
  `XWD_HIDDEN` bit(1) NOT NULL,
  PRIMARY KEY (`XWD_ID`),
  KEY `DOC_NAME` (`XWD_NAME`),
  KEY `DOC_CREATION_DATE` (`XWD_CREATION_DATE`),
  KEY `DOC_CONTENT_UPDATE_DATE` (`XWD_CONTENT_UPDATE_DATE`),
  KEY `DOC_CREATOR` (`XWD_CREATOR`),
  KEY `DOC_TITLE` (`XWD_TITLE`),
  KEY `DOC_SPACE` (`XWD_WEB`),
  KEY `DOC_MINOREDIT` (`XWD_MINOREDIT`),
  KEY `DOC_DEFAULT_LANGUAGE` (`XWD_DEFAULT_LANGUAGE`),
  KEY `DOC_AUTHOR` (`XWD_AUTHOR`),
  KEY `DOC_CONTENT_AUTHOR` (`XWD_CONTENT_AUTHOR`),
  KEY `DOC_FULLNAME` (`XWD_FULLNAME`),
  KEY `DOC_LANGUAGE` (`XWD_LANGUAGE`),
  KEY `DOC_DATE` (`XWD_DATE`),
  KEY `DOC_HIDDEN` (`XWD_HIDDEN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikidoubles` */

DROP TABLE IF EXISTS `xwikidoubles`;

CREATE TABLE `xwikidoubles` (
  `XWD_ID` bigint(20) NOT NULL,
  `XWD_NAME` varchar(255) NOT NULL,
  `XWD_VALUE` double DEFAULT NULL,
  PRIMARY KEY (`XWD_ID`,`XWD_NAME`),
  KEY `FK5A1CD9A1A947AA5` (`XWD_ID`,`XWD_NAME`),
  KEY `XWDOUBLE_NAME` (`XWD_NAME`),
  KEY `XWDOUBLE_VALUE` (`XWD_VALUE`),
  CONSTRAINT `FK5A1CD9A1A947AA5` FOREIGN KEY (`XWD_ID`, `XWD_NAME`) REFERENCES `xwikiproperties` (`XWP_ID`, `XWP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikifloats` */

DROP TABLE IF EXISTS `xwikifloats`;

CREATE TABLE `xwikifloats` (
  `XWF_ID` bigint(20) NOT NULL,
  `XWF_NAME` varchar(255) NOT NULL,
  `XWF_VALUE` float DEFAULT NULL,
  PRIMARY KEY (`XWF_ID`,`XWF_NAME`),
  KEY `FKFB291FBF1DFF14A1` (`XWF_ID`,`XWF_NAME`),
  KEY `XWFLOAT_VALUE` (`XWF_VALUE`),
  KEY `XWFLOAT_NAME` (`XWF_NAME`),
  CONSTRAINT `FKFB291FBF1DFF14A1` FOREIGN KEY (`XWF_ID`, `XWF_NAME`) REFERENCES `xwikiproperties` (`XWP_ID`, `XWP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikiintegers` */

DROP TABLE IF EXISTS `xwikiintegers`;

CREATE TABLE `xwikiintegers` (
  `XWI_ID` bigint(20) NOT NULL,
  `XWI_NAME` varchar(255) NOT NULL,
  `XWI_VALUE` int(11) DEFAULT NULL,
  PRIMARY KEY (`XWI_ID`,`XWI_NAME`),
  KEY `FK7F8AB31D231EFB9B` (`XWI_ID`,`XWI_NAME`),
  KEY `XWINT_NAME` (`XWI_NAME`),
  KEY `XWINT_VALUE` (`XWI_VALUE`),
  CONSTRAINT `FK7F8AB31D231EFB9B` FOREIGN KEY (`XWI_ID`, `XWI_NAME`) REFERENCES `xwikiproperties` (`XWP_ID`, `XWP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikilargestrings` */

DROP TABLE IF EXISTS `xwikilargestrings`;

CREATE TABLE `xwikilargestrings` (
  `XWL_ID` bigint(20) NOT NULL,
  `XWL_NAME` varchar(255) NOT NULL,
  `XWL_VALUE` longtext,
  PRIMARY KEY (`XWL_ID`,`XWL_NAME`),
  KEY `FK6661970F283EE295` (`XWL_ID`,`XWL_NAME`),
  KEY `XWLS_NAME` (`XWL_NAME`),
  CONSTRAINT `FK6661970F283EE295` FOREIGN KEY (`XWL_ID`, `XWL_NAME`) REFERENCES `xwikiproperties` (`XWP_ID`, `XWP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikilinks` */

DROP TABLE IF EXISTS `xwikilinks`;

CREATE TABLE `xwikilinks` (
  `XWL_DOC_ID` bigint(20) NOT NULL,
  `XWL_LINK` varchar(255) NOT NULL,
  `XWL_FULLNAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`XWL_DOC_ID`,`XWL_LINK`),
  KEY `XWLNK_LINK` (`XWL_LINK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikilistitems` */

DROP TABLE IF EXISTS `xwikilistitems`;

CREATE TABLE `xwikilistitems` (
  `XWL_ID` bigint(20) NOT NULL,
  `XWL_NAME` varchar(255) NOT NULL,
  `XWL_VALUE` varchar(255) DEFAULT NULL,
  `XWL_NUMBER` int(11) NOT NULL,
  PRIMARY KEY (`XWL_ID`,`XWL_NAME`,`XWL_NUMBER`),
  KEY `FKC0862BA3FB72A11` (`XWL_ID`,`XWL_NAME`),
  KEY `XWLI_VALUE` (`XWL_VALUE`),
  KEY `XWLI_NAME` (`XWL_NAME`),
  CONSTRAINT `FKC0862BA3FB72A11` FOREIGN KEY (`XWL_ID`, `XWL_NAME`) REFERENCES `xwikilists` (`XWL_ID`, `XWL_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikilists` */

DROP TABLE IF EXISTS `xwikilists`;

CREATE TABLE `xwikilists` (
  `XWL_ID` bigint(20) NOT NULL,
  `XWL_NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`XWL_ID`,`XWL_NAME`),
  KEY `FKDF23086D283EE295` (`XWL_ID`,`XWL_NAME`),
  KEY `XWLIST_NAME` (`XWL_NAME`),
  CONSTRAINT `FKDF23086D283EE295` FOREIGN KEY (`XWL_ID`, `XWL_NAME`) REFERENCES `xwikiproperties` (`XWP_ID`, `XWP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikilock` */

DROP TABLE IF EXISTS `xwikilock`;

CREATE TABLE `xwikilock` (
  `XWL_DOC_ID` bigint(20) NOT NULL,
  `XWL_AUTHOR` varchar(255) DEFAULT NULL,
  `XWL_DATE` datetime NOT NULL,
  PRIMARY KEY (`XWL_DOC_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikilongs` */

DROP TABLE IF EXISTS `xwikilongs`;

CREATE TABLE `xwikilongs` (
  `XWL_ID` bigint(20) NOT NULL,
  `XWL_NAME` varchar(255) NOT NULL,
  `XWL_VALUE` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`XWL_ID`,`XWL_NAME`),
  KEY `FKDF25AE4F283EE295` (`XWL_ID`,`XWL_NAME`),
  KEY `XWLONG_NAME` (`XWL_NAME`),
  KEY `XWLONG_VALUE` (`XWL_VALUE`),
  CONSTRAINT `FKDF25AE4F283EE295` FOREIGN KEY (`XWL_ID`, `XWL_NAME`) REFERENCES `xwikiproperties` (`XWP_ID`, `XWP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikiobjects` */

DROP TABLE IF EXISTS `xwikiobjects`;

CREATE TABLE `xwikiobjects` (
  `XWO_ID` bigint(20) NOT NULL,
  `XWO_NUMBER` int(11) DEFAULT NULL,
  `XWO_NAME` varchar(255) NOT NULL,
  `XWO_CLASSNAME` varchar(255) NOT NULL,
  `XWO_GUID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`XWO_ID`),
  KEY `OBJ_NAME` (`XWO_NAME`),
  KEY `OBJ_CLASSNAME` (`XWO_CLASSNAME`),
  KEY `OBJ_NUMBER` (`XWO_NUMBER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikipreferences` */

DROP TABLE IF EXISTS `xwikipreferences`;

CREATE TABLE `xwikipreferences` (
  `XWP_ID` bigint(20) NOT NULL,
  `XWP_LANGUAGE` varchar(255) DEFAULT NULL,
  `XWP_DEFAULT_LANGUAGE` varchar(255) DEFAULT NULL,
  `XWP_MULTI_LINGUAL` int(11) DEFAULT NULL,
  `XWP_AUTHENTICATE_EDIT` int(11) DEFAULT NULL,
  `XWP_AUTHENTICATE_VIEW` int(11) DEFAULT NULL,
  `XWP_AUTH_ACTIVE_CHECK` int(11) DEFAULT NULL,
  `XWP_BACKLINKS` int(11) DEFAULT NULL,
  `XWP_SKIN` varchar(255) DEFAULT NULL,
  `XWP_STYLESHEET` varchar(255) DEFAULT NULL,
  `XWP_STYLESHEETS` varchar(255) DEFAULT NULL,
  `XWP_EDITOR` varchar(255) DEFAULT NULL,
  `XWP_WEBCOPYRIGHT` varchar(255) DEFAULT NULL,
  `XWP_TITLE` varchar(255) DEFAULT NULL,
  `XWP_VERSION` varchar(255) DEFAULT NULL,
  `XWP_META` longtext,
  `XWP_USE_EMAIL_VERIFICATION` int(11) DEFAULT NULL,
  `XWP_SMTP_SERVER` varchar(255) DEFAULT NULL,
  `XWP_ADMIN_EMAIL` varchar(255) DEFAULT NULL,
  `XWP_VALIDATION_EMAIL_CONTENT` longtext,
  `XWP_CONFIRMATION_EMAIL_CONTENT` longtext,
  `XWP_INVITATION_EMAIL_CONTENT` longtext,
  `XWP_LEFT_PANELS` varchar(2000) DEFAULT NULL,
  `XWP_RIGHT_PANELS` varchar(2000) DEFAULT NULL,
  `XWP_SHOW_LEFT_PANELS` int(11) DEFAULT NULL,
  `XWP_SHOW_RIGHT_PANELS` int(11) DEFAULT NULL,
  `XWP_LANGUAGES` varchar(255) DEFAULT NULL,
  `XWP_REGISTRATION_ANONYMOUS` varchar(255) DEFAULT NULL,
  `XWP_REGISTRATION_REGISTERED` varchar(255) DEFAULT NULL,
  `XWP_EDIT_ANONYMOUS` varchar(255) DEFAULT NULL,
  `XWP_EDIT_REGISTERED` varchar(255) DEFAULT NULL,
  `XWP_COMMENT_ANONYMOUS` varchar(255) DEFAULT NULL,
  `XWP_COMMENT_REGISTERED` varchar(255) DEFAULT NULL,
  `XWP_DOCUMENT_BUNDLES` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`XWP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikiproperties` */

DROP TABLE IF EXISTS `xwikiproperties`;

CREATE TABLE `xwikiproperties` (
  `XWP_ID` bigint(20) NOT NULL,
  `XWP_NAME` varchar(255) NOT NULL,
  `XWP_CLASSTYPE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`XWP_ID`,`XWP_NAME`),
  KEY `PROP_NAME` (`XWP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikircs` */

DROP TABLE IF EXISTS `xwikircs`;

CREATE TABLE `xwikircs` (
  `XWR_DOCID` bigint(20) NOT NULL,
  `XWR_VERSION1` int(11) NOT NULL,
  `XWR_VERSION2` int(11) NOT NULL,
  `XWR_DATE` datetime NOT NULL,
  `XWR_COMMENT` varchar(255) NOT NULL,
  `XWR_AUTHOR` varchar(255) NOT NULL,
  `XWR_ISDIFF` bit(1) DEFAULT NULL,
  `XWR_PATCH` longtext,
  PRIMARY KEY (`XWR_DOCID`,`XWR_VERSION1`,`XWR_VERSION2`),
  KEY `REV_DATE` (`XWR_DATE`),
  KEY `REV_AUTHOR` (`XWR_AUTHOR`),
  KEY `REV_ISDIFF` (`XWR_ISDIFF`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikirecyclebin` */

DROP TABLE IF EXISTS `xwikirecyclebin`;

CREATE TABLE `xwikirecyclebin` (
  `XDD_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `XDD_FULLNAME` varchar(255) NOT NULL,
  `XDD_LANGUAGE` varchar(5) NOT NULL,
  `XDD_DATE` datetime NOT NULL,
  `XDD_DELETER` varchar(255) DEFAULT NULL,
  `XDD_XML` longtext NOT NULL,
  PRIMARY KEY (`XDD_ID`),
  UNIQUE KEY `XDD_FULLNAME` (`XDD_FULLNAME`,`XDD_LANGUAGE`,`XDD_DATE`),
  KEY `XDD_LANGUAGE` (`XDD_LANGUAGE`),
  KEY `XDD_DELETER` (`XDD_DELETER`),
  KEY `XDD_DATE` (`XDD_DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikistatsdoc` */

DROP TABLE IF EXISTS `xwikistatsdoc`;

CREATE TABLE `xwikistatsdoc` (
  `XWS_ID` bigint(20) NOT NULL,
  `XWS_NUMBER` int(11) DEFAULT NULL,
  `XWS_NAME` varchar(255) NOT NULL,
  `XWS_CLASSNAME` varchar(255) DEFAULT NULL,
  `XWS_ACTION` varchar(255) NOT NULL,
  `XWS_PAGE_VIEWS` int(11) DEFAULT NULL,
  `XWS_UNIQUE_VISITORS` int(11) DEFAULT NULL,
  `XWS_PERIOD` int(11) DEFAULT NULL,
  `XWS_VISITS` int(11) DEFAULT NULL,
  PRIMARY KEY (`XWS_ID`),
  KEY `XWDS_VISITS` (`XWS_VISITS`),
  KEY `XWDS_NAME` (`XWS_NAME`),
  KEY `XWDS_ACTION` (`XWS_ACTION`),
  KEY `XWDS_UNIQUE_VISITORS` (`XWS_UNIQUE_VISITORS`),
  KEY `XWDS_PERIOD` (`XWS_PERIOD`),
  KEY `XWDS_PAGE_VIEWS` (`XWS_PAGE_VIEWS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikistatsreferer` */

DROP TABLE IF EXISTS `xwikistatsreferer`;

CREATE TABLE `xwikistatsreferer` (
  `XWR_ID` bigint(20) NOT NULL,
  `XWR_NUMBER` int(11) DEFAULT NULL,
  `XWR_NAME` varchar(255) NOT NULL,
  `XWR_CLASSNAME` varchar(255) DEFAULT NULL,
  `XWR_REFERER` varchar(8192) NOT NULL,
  `XWR_PAGE_VIEWS` int(11) DEFAULT NULL,
  `XWR_PERIOD` int(11) DEFAULT NULL,
  PRIMARY KEY (`XWR_ID`),
  KEY `XWRS_NAME` (`XWR_NAME`),
  KEY `XWRS_PERIOD` (`XWR_PERIOD`),
  KEY `XWRS_PAGE_VIEWS` (`XWR_PAGE_VIEWS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikistatsvisit` */

DROP TABLE IF EXISTS `xwikistatsvisit`;

CREATE TABLE `xwikistatsvisit` (
  `XWV_ID` bigint(20) NOT NULL,
  `XWV_NUMBER` int(11) DEFAULT NULL,
  `XWV_NAME` varchar(255) NOT NULL,
  `XWV_CLASSNAME` varchar(255) DEFAULT NULL,
  `XWV_IP` varchar(255) NOT NULL,
  `XWV_USER_AGENT` varchar(8192) NOT NULL,
  `XWV_COOKIE` varchar(8192) NOT NULL,
  `XWV_UNIQUE_ID` varchar(255) NOT NULL,
  `XWV_PAGE_VIEWS` int(11) DEFAULT NULL,
  `XWV_PAGE_SAVES` int(11) DEFAULT NULL,
  `XWV_DOWNLOADS` int(11) DEFAULT NULL,
  `XWV_START_DATE` datetime DEFAULT NULL,
  `XWV_END_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`XWV_ID`),
  KEY `XWVS_END_DATE` (`XWV_END_DATE`),
  KEY `XWVS_UNIQUE_ID` (`XWV_UNIQUE_ID`),
  KEY `XWVS_PAGE_VIEWS` (`XWV_PAGE_VIEWS`),
  KEY `XWVS_START_DATE` (`XWV_START_DATE`),
  KEY `XWVS_NAME` (`XWV_NAME`),
  KEY `XWVS_PAGE_SAVES` (`XWV_PAGE_SAVES`),
  KEY `XWVS_DOWNLOADS` (`XWV_DOWNLOADS`),
  KEY `XWVS_IP` (`XWV_IP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `xwikistrings` */

DROP TABLE IF EXISTS `xwikistrings`;

CREATE TABLE `xwikistrings` (
  `XWS_ID` bigint(20) NOT NULL,
  `XWS_NAME` varchar(255) NOT NULL,
  `XWS_VALUE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`XWS_ID`,`XWS_NAME`),
  KEY `FK2780715A3433FD87` (`XWS_ID`,`XWS_NAME`),
  KEY `XWSTR_NAME` (`XWS_NAME`),
  KEY `XWSTR_VALUE` (`XWS_VALUE`),
  CONSTRAINT `FK2780715A3433FD87` FOREIGN KEY (`XWS_ID`, `XWS_NAME`) REFERENCES `xwikiproperties` (`XWP_ID`, `XWP_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
