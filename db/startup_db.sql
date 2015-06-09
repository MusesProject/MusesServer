CREATE DATABASE  IF NOT EXISTS `muses` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `muses`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: localhost    Database: muses
-- ------------------------------------------------------
-- Server version	5.6.21

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
-- Table structure for table `access_request`
--

DROP TABLE IF EXISTS `access_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `access_request` (
  `access_request_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table SIMPLE_EVENTS(event_id)',
  `action` varchar(200) DEFAULT NULL COMMENT 'Value of user action for this concrete access request',
  `asset_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table ASSETS(asset_id)',
  `user_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table USERS(user_id)',
  `decision_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Foreign key to the final decision associated to the access request, once the decision is taken. FK to table DECISIONS(decision_id)',
  `modification` datetime DEFAULT NULL COMMENT 'Time of detection of the access request',
  `threat_id` int(11) NOT NULL DEFAULT '0',
  `solved` int(11) DEFAULT '0',
  `user_action` int(11) DEFAULT '0',
  PRIMARY KEY (`access_request_id`),
  KEY `access_request-simple_events:event_id_idx` (`decision_id`),
  KEY `access_request-assets:asset_id_idx` (`asset_id`),
  KEY `access_request-users:user_id_idx` (`user_id`),
  KEY `access_request-simple_events:event_id_idx1` (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=494 DEFAULT CHARSET=utf8 COMMENT='Table which include any access request detected by the Event Processor';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `additional_protection`
--

DROP TABLE IF EXISTS `additional_protection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `additional_protection` (
  `additional_protection_id` int(20) unsigned NOT NULL,
  `name` varchar(50) NOT NULL COMMENT 'Description of the additional protection',
  `access_request_id` int(10) unsigned DEFAULT '0' COMMENT 'FK to table ACCESS_REQUEST(access_request_id)',
  `event_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table EVENTS(event_id)',
  `device_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table DEVICES(device_id)',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table USERS(user_id)',
  `modification` datetime DEFAULT NULL COMMENT 'Time of detection of the additional protection',
  PRIMARY KEY (`additional_protection_id`),
  KEY `additional_protection-access_request:access_request_id_idx` (`access_request_id`),
  KEY `additional_protection-simple_events:event_id_idx` (`event_id`),
  KEY `additional_protection-devices:device_id_idx` (`device_id`),
  KEY `additional_protection-users:user_id_idx` (`user_id`),
  CONSTRAINT `additional_protection-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `additional_protection-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `additional_protection-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which includes any additional protection detected by the Event Processor';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `app_type`
--

DROP TABLE IF EXISTS `app_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_type` (
  `app_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(30) NOT NULL COMMENT 'Type of apps, such as "MAIL", "PDF_READER", "OFFICE", ...',
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`app_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1176 DEFAULT CHARSET=utf8 COMMENT='Table that simply describes the types of available applications.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `applications` (
  `app_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `version` varchar(20) DEFAULT NULL COMMENT 'The current version of the application',
  `last_update` datetime DEFAULT NULL COMMENT 'Last update of application',
  `vendor` varchar(30) DEFAULT NULL COMMENT 'Vendor of the application',
  `blacklisted` int(11) DEFAULT '0' COMMENT 'If TRUE (1) -> the application is blacklisted',
  `is_MUSES_aware` int(11) DEFAULT '0' COMMENT 'If TRUE (1) -> the application can be monitored easily (it interacts with the system through the API)',
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=utf8 COMMENT='As MUSES will have both black and white lists, a description of the different applications installed on a device can be found in this table.';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `assets`
--

DROP TABLE IF EXISTS `assets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assets` (
  `asset_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(30) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `value` double NOT NULL COMMENT 'represents the real value of the asset',
  `confidential_level` enum('PUBLIC','INTERNAL','CONFIDENTIAL','STRICTLY_CONFIDENTIAL','NONE') NOT NULL,
  `location` varchar(100) NOT NULL COMMENT 'Location of the asset in the hard drive',
  PRIMARY KEY (`asset_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1694 DEFAULT CHARSET=utf8 COMMENT='This one will store all Assets data. All fields are defined in the table.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clue`
--

DROP TABLE IF EXISTS `clue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clue` (
  `clue_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` longtext NOT NULL,
  PRIMARY KEY (`clue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `connection_config`
--

DROP TABLE IF EXISTS `connection_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `connection_config` (
  `config_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `timeout` int(10) unsigned NOT NULL DEFAULT '5000',
  `poll_timeout` int(10) unsigned NOT NULL DEFAULT '10000',
  `sleep_poll_timeout` int(10) unsigned NOT NULL DEFAULT '10000',
  `polling_enabled` tinyint(1) NOT NULL COMMENT 'Specify whether the polling is enabled or not',
  `login_attempts` int(10) unsigned NOT NULL DEFAULT '5',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='MUSES Server Connection configuration parameters';
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `corporate_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `corporate_policies` (
  `corporate_policy_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(2000) NOT NULL COMMENT 'Policy subject',
  `description` varchar(2000) NOT NULL COMMENT 'Policy textual description',
  `file` blob NOT NULL COMMENT 'Policy formalized in standard format (XACML,JSON,...), to make it machine readable',
  `date` date NOT NULL COMMENT 'Date of creation of the policy',
  PRIMARY KEY (`corporate_policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which contains the current set of corporate security policies, both containing textual descriptions and formalization files.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `decision`
--

DROP TABLE IF EXISTS `decision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `decision` (
  `decision_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `access_request_id` bigint(20) unsigned NOT NULL,
  `risk_communication_id` int(10) DEFAULT NULL,
  `value` enum('GRANTED','STRONGDENY','MAYBE','UPTOYOU') NOT NULL,
  `information` text,
  `solving_risktreatment` int(11) DEFAULT NULL,
  `time` datetime NOT NULL COMMENT 'When the decision was made',
  PRIMARY KEY (`decision_id`),
  KEY `decision-access_request:access_request_id_idx` (`access_request_id`),
  KEY `decision-risk_communication:risk_communication_id_idx` (`risk_communication_id`),
  CONSTRAINT `decision-access_request:access_request_id` FOREIGN KEY (`access_request_id`) REFERENCES `access_request` (`access_request_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=569 DEFAULT CHARSET=utf8 COMMENT='Table which stores all decision computed by the RT2AE. All fields are defined in the table.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `decision_trustvalues`
--

DROP TABLE IF EXISTS `decision_trustvalues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `decision_trustvalues` (
  `decision_trustvalue_id` int(11) NOT NULL AUTO_INCREMENT,
  `decision_id` bigint(20) unsigned NOT NULL,
  `usertrustvalue` double NOT NULL,
  `devicetrustvalue` double NOT NULL,
  PRIMARY KEY (`decision_trustvalue_id`),
  UNIQUE KEY `decision_id` (`decision_id`),
  CONSTRAINT `decision_trustvalues_ibfk_1` FOREIGN KEY (`decision_id`) REFERENCES `decision` (`decision_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `default_policies`
--

DROP TABLE IF EXISTS `default_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `default_policies` (
  `default_policy_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(2000) NOT NULL COMMENT 'Policy subject',
  `description` varchar(2000) NOT NULL COMMENT 'Policy textual description',
  `file` blob  COMMENT 'Policy formalized in standard format (XACML,JSON,...), to make it machine readable',
  `date` date NOT NULL COMMENT 'Date of creation of the policy',
  PRIMARY KEY (`default_policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which contains the default security policies, both containing textual descriptions and formalization files.';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `devices`
--

DROP TABLE IF EXISTS `devices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `devices` (
  `device_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `model` varchar(30) NOT NULL COMMENT 'Device model, sent from the client',
  `description` varchar(100) DEFAULT NULL,
  `IMEI` varchar(30) DEFAULT NULL COMMENT 'In the format XXXXXX YY ZZZZZZ W',
  `OS_name` varchar(30) DEFAULT NULL COMMENT 'The operating system of the device',
  `OS_version` varchar(20) DEFAULT NULL COMMENT 'The operating system of the device',
  `trust_value` double DEFAULT NULL COMMENT 'The trust value of the device will be between 0 and 1',
  `certificate` blob,
  `owner_type` enum('COMPANY','USER') DEFAULT NULL,
  PRIMARY KEY (`device_id`)
) ENGINE=InnoDB AUTO_INCREMENT=334 DEFAULT CHARSET=utf8 COMMENT='Table that has been created due to the importance of having a record of the different devices that are using company assets and the need of pairing a device with an owner. Like the users, the devices have also a defined trust value that may be changed by RT2AE decisions.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `domains`
--

DROP TABLE IF EXISTS `domains`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domains` (
  `domain_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT 'Name of the domain (e.g. Offers)',
  `description` varchar(100) DEFAULT NULL COMMENT 'Domain description (e.g. Company domain used to store commercial offers to be presented to concrete customers. This kind of information is strictly confidential.)',
  `sensitivity_id` enum('PUBLIC','INTERNAL','CONFIDENTIAL','STRICTLY_CONFIDENTIAL','NONE') NOT NULL DEFAULT 'CONFIDENTIAL' COMMENT 'Associated sensitivity level (strictly confidential, protected, public,...)',
  PRIMARY KEY (`domain_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='Table which describes the different domains that might apply for different company resources. Depending on this domain, it will have a different sensitivity level.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event_type`
--

DROP TABLE IF EXISTS `event_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_type` (
  `event_type_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_type_key` varchar(200) NOT NULL COMMENT 'Possible values are: {USER_ACTION,SENSOR_CONTEXT,USER_FEEDBACK} as simple events and {DECISION,THREAT_CLUE,ADDITIONAL_PROTECTION,SECURITY_INCIDENT,DEVICE_POLICY_UPDATE} as complex events',
  `event_level` varchar(200) NOT NULL COMMENT 'Possible values are: SIMPLE_EVENT (corresponding to events that are generated by monitoring, without server processing) and COMPLEX_EVENT (events generated from the correlation or aggregation of other simple events)',
  PRIMARY KEY (`event_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='Table which describes the possible types of events';
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `legal_aspects`
--

DROP TABLE IF EXISTS `legal_aspects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `legal_aspects` (
  `description` varchar(50) NOT NULL,
  `KRS_hard_limit` int(10) unsigned NOT NULL DEFAULT '180' COMMENT 'Duration of data in days for the Knowledge Refinement System. Default=6 months',
  `RT2AE_hard_limit` int(10) unsigned NOT NULL DEFAULT '180' COMMENT 'Duration of data in days for the RT2AE. Default=6 months',
  `EP_hard_limit` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'Duration of data in days for the Event Processor. Default=6 months',
  `data_complete_erasure` binary(1) NOT NULL DEFAULT '1' COMMENT 'If ''1'' (TRUE) data will be completely removed from the database once the duration has expired.',
  PRIMARY KEY (`description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table containing data related with user''s privacy and legality in the system';
/*!40101 SET character_set_client = @saved_cs_client */;



-- Structure de la table `list_ofpossible_risktreatments`
--

CREATE TABLE IF NOT EXISTS `list_ofpossible_risktreatments` (
  `listofpossiblerisktreatment_id` int(30) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `Spanish` varchar(255) NOT NULL,
  `German` varchar(255) NOT NULL,
  `French` varchar(255) NOT NULL,
  PRIMARY KEY (`listofpossiblerisktreatment_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

--
-- Table structure for table `muses_config`
--

DROP TABLE IF EXISTS `muses_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `muses_config` (
  `config_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `config_name` varchar(30) NOT NULL COMMENT 'Name of the configuration',
  `silent_mode` tinyint(4) NOT NULL COMMENT 'Specify whether all devices should run MUSES application in silent mode (true), or verbose (false)',
  `access_attempts_before_blocking` int(10) unsigned NOT NULL DEFAULT '5',
  `date` datetime DEFAULT NULL COMMENT 'Time from which configuration applies',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='MUSES Server configuration parameters';
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `outcome`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `outcome` (
  `outcome_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `description` longtext,
  `costbenefit` double DEFAULT NULL,
  `threat_id` bigint(20) NOT NULL,
  PRIMARY KEY (`outcome_id`),
  KEY `threat_outcome_link` (`threat_id`),
  CONSTRAINT `outcome_ibfk_1` FOREIGN KEY (`threat_id`) REFERENCES `threat` (`threat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1130 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patterns_krs`
--

DROP TABLE IF EXISTS `patterns_krs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patterns_krs` (
  `log_entry_id` bigint(20) unsigned  AUTO_INCREMENT,
  `label` enum('GRANTED','STRONGDENY','MAYBE','UPTOYOU')  COMMENT 'This is the decision taken for this event',
  `decision_cause` varchar(1000) COMMENT 'Condition satisfied to detect the security violation',
  `silent_mode` int(11)  COMMENT 'Muses being in silent mode (1) or in verbose mode (0)',
  `event_type` varchar(200)  COMMENT 'Possible values are: {USER_ACTION,SENSOR_CONTEXT,USER_FEEDBACK} as simple events and {DECISION,THREAT_CLUE,ADDITIONAL_PROTECTION,SECURITY_INCIDENT,DEVICE_POLICY_UPDATE} as complex events',
  `event_level` varchar(200)  COMMENT 'Possible values are: SIMPLE_EVENT (corresponding to events that are generated by monitoring, without server processing) and COMPLEX_EVENT (events generated from the correlation or aggregation of other simple events)',
  `username` varchar(50)  COMMENT 'The username is taken rather than the actual name of the user, just to identify the source of the event',
  `password_length` int(11)  COMMENT 'The length of the user''s password',
  `letters_in_password` int(11)  COMMENT 'How many letters does the user''s password contain',
  `numbers_in_password` int(11)  COMMENT 'How many numbers does the user''s password contain',
  `passwd_has_capital_letters` int(11)  COMMENT 'Specify whether the user''s password has (1) or has not (0) capital letters as well as non capital letters in their password',
  `user_trust_value` double  COMMENT 'Trust value that the user had when the event was thrown',
  `activated_account` int(11)  COMMENT 'Specify whether the user''s account was active (1) or not (0) when the event was thrown',
  `user_role` varchar(50)  COMMENT 'Role of the user in the company',
  `event_detection` datetime  COMMENT 'Time of detection of the event',
  `device_type` varchar(30)  COMMENT 'Type of the device from which the event was sent. Possible values are: {DESKTOP_PC, LAPTOP, TABLET, SMARTPHONE}',
  `device_OS` varchar(50)  COMMENT 'OS type and version of the user''s device',
  `device_has_antivirus` int(11)  COMMENT 'Specify whether the user''s device has (1) or has not (0) a trusted antivirus installed',
  `device_has_certificate` int(11)  COMMENT 'Specify whether the user''s device has (1) or has not (0) a valid certificate installed',
  `device_trust_value` double  COMMENT 'Trust value that the device had when the event was thrown',
  `device_owned_by` varchar(20)  COMMENT 'Owner of the device. Possible values are: {COMPANY, EMPLOYEE}',
  `device_has_password` int(11)  COMMENT 'Specify whether the user''s device has (1) or has not (0) the screen protected by password',
  `device_screen_timeout` int(11)  COMMENT 'Time to lock the screen in the device, in seconds',
  `device_has_accessibility` int(11)  COMMENT 'Specify whether the user''s device has (1) or has not (0) the accessibility enabled',
  `device_is_rooted` int(11)  COMMENT 'Specify whether the user''s device is (1) or is not (0) rooted',
  `app_name` varchar(50)  COMMENT 'Name and version of the application from which the event was thrown',
  `app_vendor` varchar(30)  COMMENT 'Vendor of the application',
  `app_is_MUSES_aware` int(11)  COMMENT 'If TRUE (1), the application can be monitored easily (it interacts with the system through the API)',
  `asset_name` varchar(30) COMMENT 'Title of the asset that is being accessed in the event',
  `asset_value` double COMMENT 'Represents the real value of the asset',
  `asset_confidential_level` enum('PUBLIC','INTERNAL','CONFIDENTIAL','STRICTLY_CONFIDENTIAL','NONE') COMMENT 'The confidentiality level of the asset',
  `asset_location` varchar(100)  COMMENT 'Location of the asset in the hard drive',
  `mail_recipient_allowed` int(11) COMMENT 'If the event is about sending an email, specify whether the recipient belongs (1) or not (0) to the company',
  `mail_contains_cc_allowed` int(11) COMMENT 'If the event is about sending an email, specify whether the mail contains (1) or not (0) someone in CC',
  `mail_contains_bcc_allowed` int(11) COMMENT 'If the event is about sending an email, specify whether the mail contains (1) or not (0) someone in BCC',
  `mail_has_attachment` int(11) COMMENT 'If the event is about sending an email, specify whether the mail contains (1) or not (0) an attachment',
  `wifiencryption` varchar(30) COMMENT 'When sending an asset, information about encryption of the wifi network',
  `wifienabled` int(11) COMMENT 'When sending an asset, information about wifi connection being enabled',
  `wificonnected` int(11) COMMENT 'When sending an asset, information about wifi being connected',
  `bluetoothconnected` int(11) COMMENT 'When sending an asset, information about bluetooth being connected',
  PRIMARY KEY (`log_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Patterns built with the extracted information from the events';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `refined_security_rules`
--

DROP TABLE IF EXISTS `refined_security_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refined_security_rules` (
  `refined_security_rules_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `original_security_rule_id` bigint(20) unsigned NOT NULL COMMENT 'Initial security rule which was refined. If it has been inferred this field will be empty.',
  `name` varchar(2000) NOT NULL COMMENT 'If it is a refinement, the name will be the original name + "REFINED"',
  `file` blob COMMENT 'File in DRL format, containing the rule''s code, to make it machine readable',
  `status` enum('PROPOSED','VALIDATED','EXPIRED') NOT NULL COMMENT 'Current status of the rule. VALIDATED means that the CSO has approved this rule, so it can be inserted into the SECURITY_RULES table',
  `modification` datetime NOT NULL COMMENT 'Date of creation/modification of the rule',
  PRIMARY KEY (`refined_security_rules_id`),
  KEY `refined_security_rules-security_rules:security_rule_id_idx` (`original_security_rule_id`),
  CONSTRAINT `refined_security_rules-security_rules:security_rule_id` FOREIGN KEY (`original_security_rule_id`) REFERENCES `security_rules` (`security_rule_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='Table which contains the potential set of security rules improved or inferred by the KRS.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `risk_communication`
--

DROP TABLE IF EXISTS `risk_communication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `risk_communication` (
  `risk_communication_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL COMMENT 'Textual description of the risk communication',
  PRIMARY KEY (`risk_communication_id`)
) ENGINE=InnoDB AUTO_INCREMENT=911 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `risk_policy`
--

DROP TABLE IF EXISTS `risk_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `risk_policy` (
  `risk_policy_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `riskvalue` double NOT NULL,
  `description` longtext NOT NULL,
  PRIMARY KEY (`risk_policy_id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `risk_treatment`
--

DROP TABLE IF EXISTS `risk_treatment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `risk_treatment` (
  `risk_treatment_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL COMMENT 'Description of risk treatment',
  `risk_communication_id` int(10) unsigned NOT NULL COMMENT 'FK to table RISK_COMMUNICATION(risk_communication_id)',
  PRIMARY KEY (`risk_treatment_id`),
  KEY `risk_treatment-risk_communication:risk_communication_id_idx` (`risk_communication_id`),
  CONSTRAINT `risk_treatment-risk_communication:risk_communication_id` FOREIGN KEY (`risk_communication_id`) REFERENCES `risk_communication` (`risk_communication_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='This table will store all risk treatment computed by the RT2AE. All fields are defined in the table.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `role_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL COMMENT 'Role description',
  `security_level` smallint(6) DEFAULT NULL COMMENT 'Associated security level',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8 COMMENT='Table which describes the role of the users inside the company, for example, if he is the CSO, the CTO, an accountant, a developer...';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `security_incident`
--

DROP TABLE IF EXISTS `security_incident`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `security_incident` (
  `security_incident_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT 'Description of the security incident',
  `decision_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table DECISION(decision_id)',
  `event_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table EVENTS(event_id)',
  `device_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table DEVICES(device_id)',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table USERS(user_id)',
  `modification` datetime DEFAULT NULL COMMENT 'Time of detection of the additional protection',
  PRIMARY KEY (`security_incident_id`),
  KEY `security_incident-simple_events:event_id_idx` (`event_id`),
  KEY `security_incident-devices:device_id_idx` (`device_id`),
  KEY `security_incident-users:user_id_idx` (`user_id`),
  KEY `security_incident-decision:decision_id_idx` (`decision_id`),
  CONSTRAINT `security_incident-decision:decision_id` FOREIGN KEY (`decision_id`) REFERENCES `decision` (`decision_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `security_incident-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `security_incident-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `security_incident-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which includes any security incident detected by the Event Processor';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `security_rules`
--

DROP TABLE IF EXISTS `security_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `security_rules` (
  `security_rule_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(2000) NOT NULL COMMENT 'Name of the security rule',
  `description` varchar(2000) NOT NULL COMMENT 'Textual description of the security rule',
  `file` blob COMMENT 'File in DRL format, containing the rule''s code, to make it machine readable',
  `status` enum('VALIDATED','DRAFT','EXPIRED') NOT NULL COMMENT 'Current status of the rule. Only validated rules will be inserted into the production working memory of the Event Processor.',
  `refined` binary(1) NOT NULL DEFAULT '0' COMMENT 'If TRUE (1), the rule has been inferred by the KRS. ',
  `source_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table SOURCES(source_id) Identification of the component owner, in other words, the originator of the last version of the rule (e.g. Event Processor if it is manual or based on expert knowledge or Knowledge Refinement System if the current version is the outcome of knowledge refinement)',
  `modification` datetime NOT NULL COMMENT 'Date of creation of the rule',
  PRIMARY KEY (`security_rule_id`),
  KEY `security_rules-sources:source_id_idx` (`source_id`),
  CONSTRAINT `security_rules-sources:source_id` FOREIGN KEY (`source_id`) REFERENCES `sources` (`source_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=801 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `security_violation`
--

DROP TABLE IF EXISTS `security_violation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `security_violation` (
  `security_violation_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `message` varchar(1000) NOT NULL COMMENT 'Description of the detected security violation',
  `conditionText` varchar(1000) NOT NULL COMMENT 'Condition satisfied to detect the current security violation',
  `modeText` varchar(1000) NOT NULL COMMENT 'Mode associated to the way to provide a decision',
  `user_id` bigint(20) unsigned NOT NULL,
  `decision_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Foreign key to the final decision associated to the security violation, once the decision is taken. FK to table DECISIONS(decision_id)',
  `event_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table EVENTS(event_id)',
  `device_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table DEVICES(device_id)',
  `detection` datetime DEFAULT NULL COMMENT 'Time of detection of the security violation',
  PRIMARY KEY (`security_violation_id`),
  KEY `security-violation-users:user_id_idx` (`user_id`),
  KEY `security-violation-decisions:decision_id_idx` (`decision_id`),
  KEY `security-violation-simple_events:event_id_idx` (`event_id`),
  KEY `security_violation-devices:device_id` (`device_id`),
  CONSTRAINT `security_violation-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `security_violation-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `security_violation-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1378 DEFAULT CHARSET=utf8 COMMENT='Table to store any detected security violation';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sensor_configuration`
--

DROP TABLE IF EXISTS `sensor_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sensor_configuration` (
  `id` int(11) NOT NULL,
  `sensor_type` varchar(45) NOT NULL,
  `keyproperty` varchar(45) NOT NULL,
  `valueproperty` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table contains sensor configuration properties';
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `simple_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `simple_events` (
  `event_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_type_id` int(10) unsigned NOT NULL COMMENT 'Type of the event. This is a reference to the EVENT_TYPES table, whose possible values are: {USER_ACTION,SENSOR_CONTEXT,USER_FEEDBACK} as simple events',
  `user_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table USERS(user_id)',
  `device_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table DEVICES(device_id)',
  `app_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table APPLICATIONS(app_id)',
  `asset_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table ASSETS(asset_id)',
  `data` mediumtext NOT NULL COMMENT 'Raw event content (this is the content of the whole event in JSON format)',
  `date` date NOT NULL COMMENT 'Date when the event happens',
  `time` time NOT NULL COMMENT 'Time at when the event happens',
  `source_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table SOURCES(source_id)',
  `EP_can_access` int(11) DEFAULT '1' COMMENT 'If TRUE (1) the Event Processor can access these data',
  `RT2AE_can_access` int(11) DEFAULT '1' COMMENT 'If TRUE (1) the RT2AE can access these data',
  `KRS_can_access` int(11) DEFAULT '1' COMMENT 'If TRUE (1) the Knowledge Refinement System can access these data',
  PRIMARY KEY (`event_id`),
  KEY `event_type_id_idx` (`event_type_id`),
  KEY `device_id_idx` (`device_id`),
  KEY `app_id_idx` (`app_id`),
  KEY `asset_id_idx` (`asset_id`),
  KEY `source_id_idx` (`source_id`),
  KEY `users_id_idx` (`user_id`),
  CONSTRAINT `simple_events-applications:app_id` FOREIGN KEY (`app_id`) REFERENCES `applications` (`app_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `simple_events-assets:asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`asset_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `simple_events-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `simple_events-event_type_event:type_id` FOREIGN KEY (`event_type_id`) REFERENCES `event_type` (`event_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `simple_events-sources:source_id` FOREIGN KEY (`source_id`) REFERENCES `sources` (`source_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `simple_events-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16958 DEFAULT CHARSET=utf8 COMMENT=' Table which describes the set of simple or primitive events in the MUSES system. Each event is paired with:';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sources`
--

DROP TABLE IF EXISTS `sources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sources` (
  `source_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT 'Name of the source component that originates actions, events,...',
  PRIMARY KEY (`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_log_krs`
--

DROP TABLE IF EXISTS `system_log_krs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_log_krs` (
  `log_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `previous_event_id` bigint(20) unsigned NOT NULL COMMENT 'Previous event. FK to EVENTS(event_id)',
  `current_event_id` bigint(20) unsigned NOT NULL COMMENT 'Current event. FK to EVENTS(event_id)',
  `decision_id` bigint(20) unsigned NOT NULL COMMENT 'Corresponding decision to that event. FK to DECISION(decision_id)',
  `user_behaviour_id` bigint(20) unsigned NOT NULL COMMENT 'Corresponding user''s behaviour for the event. FK to USER_BEHAVIOUR(user_behaviour_id)',
  `security_incident_id` bigint(20) unsigned NOT NULL COMMENT 'Corresponding security incident for the event. FK to SECURITY_INCIDENT(security_incident_id)',
  `device_security_state` bigint(20) unsigned NOT NULL COMMENT 'Corresponding device security state for the event. FK to DEVICE_SECURITY_STATE(device_security_state_id)',
  `risk_treatment`  varchar(1000) COMMENT 'Corresponding risk treatment for the event.',
  `start_time` datetime NOT NULL COMMENT 'When the sequence started',
  `finish_time` datetime NOT NULL COMMENT 'When the sequence finished',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table containing the useful information in the form of a log of the system working, in order to further being able to simulate that system workflow in an evaluation process for new (inferred or refined) rules.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `threat`
--

DROP TABLE IF EXISTS `threat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `threat` (
  `threat_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `description` longtext NOT NULL,
  `probability` double NOT NULL,
  `occurences` int(11) DEFAULT '0',
  `badOutcomeCount` int(11) DEFAULT '0',
  `ttl` int(11) DEFAULT '0',
  PRIMARY KEY (`threat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1119 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `threat_clue`
--

DROP TABLE IF EXISTS `threat_clue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `threat_clue` (
  `threat_clue_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `access_request_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table ACCESS_REQUEST(access_request_id)',
  `event_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table EVENTS(event_id)',
  `threat_type_id` int(11) unsigned NOT NULL COMMENT 'FK to table THREAT_TYPE(threat_type_id)',
  `asset_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table ASSETS(asset_id)',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table USERS(user_id)',
  `modification` datetime NOT NULL COMMENT 'Time of detection of the threat clue',
  PRIMARY KEY (`threat_clue_id`),
  KEY `threat_clue-acess_request:access_request_id_idx` (`access_request_id`),
  KEY `threat_clue-simple_events:event_id_idx` (`event_id`),
  KEY `threat_clue-assets:asset_id_idx` (`asset_id`),
  KEY `threat_clue-users:user_id_idx` (`user_id`),
  KEY `threat_clue-threat_type:threat_type_id_idx` (`threat_type_id`),
  CONSTRAINT `threat_clue-acess_request:access_request_id` FOREIGN KEY (`access_request_id`) REFERENCES `access_request` (`access_request_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `threat_clue-assets:asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`asset_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `threat_clue-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `threat_clue-threat_type:threat_type_id` FOREIGN KEY (`threat_type_id`) REFERENCES `threat_type` (`threat_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `threat_clue-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which include any threat clue detected by the Event Processor';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `threat_type`
--

DROP TABLE IF EXISTS `threat_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `threat_type` (
  `threat_type_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL COMMENT 'Types of threat, such as WI-FI_SNIFFING, UNSECURE_NETWORK,MALWARE,SPYWARE,..',
  `description` varchar(100) NOT NULL COMMENT 'Description of the threat',
  PRIMARY KEY (`threat_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table is directly related to the RISK_INFORMATION table, as it contains the information about the type of threat.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_behaviour`
--

DROP TABLE IF EXISTS `user_behaviour`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_behaviour` (
  `user_behaviour_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `device_id` bigint(20) unsigned NOT NULL,
  `action` varchar(50) NOT NULL COMMENT 'The action made by the user',
  `time` datetime NOT NULL COMMENT 'Date of the recording',
  `decision_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table DECISION(decision_id)',
  `additional_info` varchar(50) DEFAULT NULL COMMENT 'Useful additional information',
  PRIMARY KEY (`user_behaviour_id`),
  KEY `user_behaviour-users:user_id_idx` (`user_id`),
  KEY `user_behaviour-devices:device_id_idx` (`device_id`),
  KEY `user_behaviour-decision:decision_id_idx` (`decision_id`),
  CONSTRAINT `user_behaviour-decision:decision_id` FOREIGN KEY (`decision_id`) REFERENCES `decision` (`decision_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `user_behaviour-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `user_behaviour-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which will store all user behaviour data. All fields are defined in the table.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL COMMENT 'First and middle names',
  `surname` varchar(50) NOT NULL,
  `email` varchar(50) DEFAULT NULL COMMENT 'user''s e-mail',
  `username` varchar(50) NOT NULL COMMENT 'The user name used to login',
  `password` varchar(50) NOT NULL COMMENT 'The user''s password',
  `enabled` int(11) NOT NULL DEFAULT '0' COMMENT 'Specify whether the user''s account is active (1) or not (0)',
  `trust_value` double unsigned DEFAULT NULL COMMENT 'The trust value of the user will be between 0 and 1',
  `role_id` int(10) unsigned NOT NULL COMMENT 'FK to table ROLE(role_id)',
  `language` varchar(45) DEFAULT 'en',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  KEY `role_id_idx` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=307 DEFAULT CHARSET=utf8 COMMENT='This table contains user information, similar to a profile. It has personal data (name, email) as well as company data (user''s role inside the company). Additionally, a trust value has been included for RT2AE calculation processes.';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `zone`
--

DROP TABLE IF EXISTS `zone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zone` (
  `zone_id` int(11) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `long` double DEFAULT NULL,
  `lat` double DEFAULT NULL,
  `radius` float DEFAULT NULL COMMENT 'Zone radius in meters',
  PRIMARY KEY (`zone_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='List of zones referring to special security requirements to apply to the company';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;

CREATE TABLE `message` (
  `idmessage` INT NOT NULL,
  `key` VARCHAR(2000) NULL,
  `language` VARCHAR(45) NULL,
  `translation` VARCHAR(5000) NULL,
  PRIMARY KEY (`idmessage`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Internationalization of messages associated to corporate policies';

ALTER TABLE `assets` CHANGE `confidential_level` `confidential_level` 
enum('PUBLIC','INTERNAL','CONFIDENTIAL','STRICTLY_CONFIDENTIAL','NONE') NOT NULL;

ALTER TABLE `assets` ADD COLUMN `available` datetime COMMENT 'Time where an asset would be available if MUSES was not there';
