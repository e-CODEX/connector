-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: testwebconnector
-- ------------------------------------------------------
-- Server version	5.7.20-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `dcon_qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `dcon_qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `dcon_qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `dcon_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_blob_triggers`
--

LOCK TABLES `dcon_qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_calendars`
--

DROP TABLE IF EXISTS `dcon_qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_calendars` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_calendars`
--

LOCK TABLES `dcon_qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `dcon_qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(200) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `dcon_qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `dcon_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_cron_triggers`
--

LOCK TABLES `dcon_qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_cron_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `dcon_qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_fired_triggers`
--

LOCK TABLES `dcon_qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_job_details`
--

DROP TABLE IF EXISTS `dcon_qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_job_details` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_job_details`
--

LOCK TABLES `dcon_qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_job_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_locks`
--

DROP TABLE IF EXISTS `dcon_qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_locks` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_locks`
--

LOCK TABLES `dcon_qrtz_locks` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `dcon_qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_paused_trigger_grps`
--

LOCK TABLES `dcon_qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `dcon_qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_scheduler_state`
--

LOCK TABLES `dcon_qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `dcon_qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `dcon_qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `dcon_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_simple_triggers`
--

LOCK TABLES `dcon_qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `dcon_qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `dcon_qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `dcon_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_simprop_triggers`
--

LOCK TABLES `dcon_qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dcon_qrtz_triggers`
--

DROP TABLE IF EXISTS `dcon_qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dcon_qrtz_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `dcon_qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `dcon_qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dcon_qrtz_triggers`
--

LOCK TABLES `dcon_qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `dcon_qrtz_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `dcon_qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domibus_connector_action`
--

DROP TABLE IF EXISTS `domibus_connector_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domibus_connector_action` (
  `ACTION` varchar(50) NOT NULL,
  `PDF_REQUIRED` smallint(6) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ACTION`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domibus_connector_action`
--

LOCK TABLES `domibus_connector_action` WRITE;
/*!40000 ALTER TABLE `domibus_connector_action` DISABLE KEYS */;
INSERT INTO `domibus_connector_action` VALUES ('DeliveryNonDeliveryToRecipient',0),('Form_A',1),('Form_B',1),('Form_C',1),('Form_D',1),('Form_E',1),('Form_F',1),('Form_G',1),('FreeFormLetter',1),('FreeFormLetterIn',1),('FreeFormLetterOut',1),('RelayREMMDAcceptanceRejection',0),('RelayREMMDFailure',0),('RetrievalNonRetrievalToRecipient',0),('SubmissionAcceptanceRejection',0),('Test_Form',0);
/*!40000 ALTER TABLE `domibus_connector_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domibus_connector_evidences`
--

DROP TABLE IF EXISTS `domibus_connector_evidences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domibus_connector_evidences` (
  `ID` bigint(20) NOT NULL,
  `MESSAGE_ID` bigint(20) NOT NULL,
  `TYPE` varchar(255) DEFAULT NULL,
  `EVIDENCE` text,
  `DELIVERED_NAT` datetime DEFAULT NULL,
  `DELIVERED_GW` datetime DEFAULT NULL,
  `UPDATED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `MESSAGE_ID` (`MESSAGE_ID`),
  CONSTRAINT `domibus_connector_evidences_ibfk_1` FOREIGN KEY (`MESSAGE_ID`) REFERENCES `domibus_connector_messages` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domibus_connector_evidences`
--

LOCK TABLES `domibus_connector_evidences` WRITE;
/*!40000 ALTER TABLE `domibus_connector_evidences` DISABLE KEYS */;
/*!40000 ALTER TABLE `domibus_connector_evidences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domibus_connector_message_info`
--

DROP TABLE IF EXISTS `domibus_connector_message_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domibus_connector_message_info` (
  `ID` bigint(20) NOT NULL,
  `MESSAGE_ID` bigint(20) NOT NULL,
  `FROM_PARTY_ID` varchar(50) DEFAULT NULL,
  `FROM_PARTY_ROLE` varchar(50) DEFAULT NULL,
  `TO_PARTY_ID` varchar(50) DEFAULT NULL,
  `TO_PARTY_ROLE` varchar(50) DEFAULT NULL,
  `ORIGINAL_SENDER` varchar(50) DEFAULT NULL,
  `FINAL_RECIPIENT` varchar(50) DEFAULT NULL,
  `SERVICE` varchar(50) DEFAULT NULL,
  `ACTION` varchar(50) DEFAULT NULL,
  `CREATED` datetime NOT NULL,
  `UPDATED` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `MESSAGE_ID` (`MESSAGE_ID`),
  KEY `FROM_PARTY_ID` (`FROM_PARTY_ID`,`FROM_PARTY_ROLE`),
  KEY `TO_PARTY_ID` (`TO_PARTY_ID`,`TO_PARTY_ROLE`),
  KEY `SERVICE` (`SERVICE`),
  KEY `ACTION` (`ACTION`),
  CONSTRAINT `domibus_connector_message_info_ibfk_1` FOREIGN KEY (`MESSAGE_ID`) REFERENCES `domibus_connector_messages` (`ID`),
  CONSTRAINT `domibus_connector_message_info_ibfk_2` FOREIGN KEY (`FROM_PARTY_ID`, `FROM_PARTY_ROLE`) REFERENCES `domibus_connector_party` (`PARTY_ID`, `ROLE`),
  CONSTRAINT `domibus_connector_message_info_ibfk_3` FOREIGN KEY (`TO_PARTY_ID`, `TO_PARTY_ROLE`) REFERENCES `domibus_connector_party` (`PARTY_ID`, `ROLE`),
  CONSTRAINT `domibus_connector_message_info_ibfk_4` FOREIGN KEY (`SERVICE`) REFERENCES `domibus_connector_service` (`SERVICE`),
  CONSTRAINT `domibus_connector_message_info_ibfk_5` FOREIGN KEY (`ACTION`) REFERENCES `domibus_connector_action` (`ACTION`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domibus_connector_message_info`
--

LOCK TABLES `domibus_connector_message_info` WRITE;
/*!40000 ALTER TABLE `domibus_connector_message_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `domibus_connector_message_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domibus_connector_messages`
--

DROP TABLE IF EXISTS `domibus_connector_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domibus_connector_messages` (
  `ID` bigint(20) NOT NULL,
  `EBMS_MESSAGE_ID` varchar(255) DEFAULT NULL,
  `NAT_MESSAGE_ID` varchar(255) DEFAULT NULL,
  `CONVERSATION_ID` varchar(255) DEFAULT NULL,
  `DIRECTION` varchar(10) DEFAULT NULL,
  `HASH_VALUE` varchar(1000) DEFAULT NULL,
  `CONFIRMED` datetime DEFAULT NULL,
  `REJECTED` datetime DEFAULT NULL,
  `DELIVERED_NAT` datetime DEFAULT NULL,
  `DELIVERED_GW` datetime DEFAULT NULL,
  `UPDATED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `EBMS_MESSAGE_ID` (`EBMS_MESSAGE_ID`),
  UNIQUE KEY `NAT_MESSAGE_ID` (`NAT_MESSAGE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domibus_connector_messages`
--

LOCK TABLES `domibus_connector_messages` WRITE;
/*!40000 ALTER TABLE `domibus_connector_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `domibus_connector_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domibus_connector_msg_error`
--

DROP TABLE IF EXISTS `domibus_connector_msg_error`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domibus_connector_msg_error` (
  `ID` bigint(20) NOT NULL,
  `MESSAGE_ID` bigint(20) NOT NULL,
  `ERROR_MESSAGE` varchar(255) NOT NULL,
  `DETAILED_TEXT` varchar(2048) DEFAULT NULL,
  `ERROR_SOURCE` varchar(255) DEFAULT NULL,
  `CREATED` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `MESSAGE_ID` (`MESSAGE_ID`),
  CONSTRAINT `domibus_connector_msg_error_ibfk_1` FOREIGN KEY (`MESSAGE_ID`) REFERENCES `domibus_connector_messages` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domibus_connector_msg_error`
--

LOCK TABLES `domibus_connector_msg_error` WRITE;
/*!40000 ALTER TABLE `domibus_connector_msg_error` DISABLE KEYS */;
/*!40000 ALTER TABLE `domibus_connector_msg_error` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domibus_connector_party`
--

DROP TABLE IF EXISTS `domibus_connector_party`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domibus_connector_party` (
  `PARTY_ID` varchar(50) NOT NULL,
  `ROLE` varchar(255) NOT NULL,
  `PARTY_ID_TYPE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`PARTY_ID`,`ROLE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domibus_connector_party`
--

LOCK TABLES `domibus_connector_party` WRITE;
/*!40000 ALTER TABLE `domibus_connector_party` DISABLE KEYS */;
INSERT INTO `domibus_connector_party` VALUES ('ARHS','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('AT','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('CTP','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('CZ','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('DE','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('EC','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('EE','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('ES','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('FR','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('GR','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('IT','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('ITIC','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('MT','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('NL','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1'),('PL','GW','urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
/*!40000 ALTER TABLE `domibus_connector_party` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domibus_connector_seq_store`
--

DROP TABLE IF EXISTS `domibus_connector_seq_store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domibus_connector_seq_store` (
  `SEQ_NAME` varchar(255) NOT NULL,
  `SEQ_VALUE` bigint(20) NOT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domibus_connector_seq_store`
--

LOCK TABLES `domibus_connector_seq_store` WRITE;
/*!40000 ALTER TABLE `domibus_connector_seq_store` DISABLE KEYS */;
INSERT INTO `domibus_connector_seq_store` VALUES ('DOMIBUS_CONNECTOR_EVIDENCES.ID',0),('DOMIBUS_CONNECTOR_MESSAGES.ID',0),('DOMIBUS_CONNECTOR_MESSAGE_INFO.ID',0),('DOMIBUS_CONNECTOR_MSG_ERROR.ID',0);
/*!40000 ALTER TABLE `domibus_connector_seq_store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domibus_connector_service`
--

DROP TABLE IF EXISTS `domibus_connector_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domibus_connector_service` (
  `SERVICE` varchar(50) NOT NULL,
  `SERVICE_TYPE` varchar(255) NOT NULL,
  PRIMARY KEY (`SERVICE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domibus_connector_service`
--

LOCK TABLES `domibus_connector_service` WRITE;
/*!40000 ALTER TABLE `domibus_connector_service` DISABLE KEYS */;
INSERT INTO `domibus_connector_service` VALUES ('Connector-TEST','urn:e-codex:services:'),('EPO','urn:e-codex:services:'),('SmallClaims','urn:e-codex:services:');
/*!40000 ALTER TABLE `domibus_connector_service` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-12-21 15:04:53
