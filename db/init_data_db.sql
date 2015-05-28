-- phpMyAdmin SQL Dump
-- version 4.0.6
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Mar 19 Mai 2015 à 11:07
-- Version du serveur: 5.5.33
-- Version de PHP: 5.2.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `muses`
--
CREATE DATABASE IF NOT EXISTS `muses` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `muses`;

-- --------------------------------------------------------

--
-- Structure de la table `access_request`
--

CREATE TABLE IF NOT EXISTS `access_request` (
  `access_request_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table SIMPLE_EVENTS(event_id)',
  `action` enum('DOWNLOAD_FILE','OPEN_APP','INSTALL_APP','OPEN_FILE') DEFAULT NULL COMMENT 'Possible value of user actions for this concrete access request',
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which include any access request detected by the Event Processor' AUTO_INCREMENT=494 ;

-- --------------------------------------------------------

--
-- Structure de la table `additional_protection`
--

CREATE TABLE IF NOT EXISTS `additional_protection` (
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
  KEY `additional_protection-users:user_id_idx` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which includes any additional protection detected by the Event Processor';

-- --------------------------------------------------------

--
-- Structure de la table `applications`
--

CREATE TABLE IF NOT EXISTS `applications` (
  `app_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `version` varchar(20) DEFAULT NULL COMMENT 'The current version of the application',
  `last_update` datetime DEFAULT NULL COMMENT 'Last update of application',
  `vendor` varchar(30) DEFAULT NULL COMMENT 'Vendor of the application',
  `blacklisted` int(11) DEFAULT '0' COMMENT 'If TRUE (1) -> the application is blacklisted',
  `is_MUSES_aware` int(11) DEFAULT '0' COMMENT 'If TRUE (1) -> the application can be monitored easily (it interacts with the system through the API)',
  PRIMARY KEY (`app_id`),
  KEY `app_type_id_idx` (`type`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='As MUSES will have both black and white lists, a description of the different applications installed on a device can be found in this table.' AUTO_INCREMENT=119 ;

--
-- Contenu de la table `applications`
--

INSERT INTO `applications` VALUES (117,'musesawaew','desc','89','2014-08-15 00:00:00','android',0,0),(118,'musesawarew','desc','89','2014-08-15 00:00:00','android',0,0),(119,'MUSES-Server','desc','89','2014-08-15 00:00:00','java',0,0);

-- --------------------------------------------------------

--
-- Structure de la table `app_type`
--

CREATE TABLE IF NOT EXISTS `app_type` (
  `app_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(30) NOT NULL COMMENT 'Type of apps, such as "MAIL", "PDF_READER", "OFFICE", ...',
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`app_type_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table that simply describes the types of available applications.' AUTO_INCREMENT=1176 ;

--
-- Contenu de la table `app_type`
--

INSERT INTO `app_type` (`app_type_id`, `type`, `description`) VALUES
(1174, '1174', 'desc'),
(1175, '1175', 'desc');

-- --------------------------------------------------------

--
-- Structure de la table `assets`
--

CREATE TABLE IF NOT EXISTS `assets` (
  `asset_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(30) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `value` double NOT NULL COMMENT 'represents the real value of the asset',
  `confidential_level` enum('PUBLIC','INTERNAL','CONFIDENTIAL','STRICTLY_CONFIDENTIAL','NONE') NOT NULL,
  `location` varchar(100) NOT NULL COMMENT 'Location of the asset in the hard drive',
  `available` datetime DEFAULT NULL COMMENT 'Time where an asset would be available if MUSES was not there',
  PRIMARY KEY (`asset_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='This one will store all Assets data. All fields are defined in the table.' AUTO_INCREMENT=1697 ;

--
-- Contenu de la table `assets`
--

INSERT INTO `assets` (`asset_id`, `title`, `description`, `value`, `confidential_level`, `location`, `available`) VALUES
(1, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva', '2015-05-19 09:51:38'),
(1694, 'UNIGEPatent', 'The patent describe how to select the best Wi-Fi network', 100000, 'CONFIDENTIAL', 'unige/patent/list/UNIGEPatent', NULL),
(1695, 'S2Patent', 'The patent describe how to prevent any threat in wireless network', 200000, 'STRICTLY_CONFIDENTIAL', 's2/patent/list/S2Patent', NULL),
(1696, 'SWEDENPatent', 'The patent is about the RFID technology', 10000, 'INTERNAL', 'sweden/patent/list/SWEDENPatent', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `clue`
--

CREATE TABLE IF NOT EXISTS `clue` (
  `clue_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` longtext NOT NULL,
  PRIMARY KEY (`clue_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=84 ;

-- --------------------------------------------------------

--
-- Structure de la table `connection_config`
--

CREATE TABLE IF NOT EXISTS `connection_config` (
  `config_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `timeout` int(10) unsigned NOT NULL DEFAULT '5000',
  `poll_timeout` int(10) unsigned NOT NULL DEFAULT '10000',
  `sleep_poll_timeout` int(10) unsigned NOT NULL DEFAULT '10000',
  `polling_enabled` tinyint(1) NOT NULL COMMENT 'Specify whether the polling is enabled or not',
  `login_attempts` int(10) unsigned NOT NULL DEFAULT '5',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='MUSES Server Connection configuration parameters' AUTO_INCREMENT=2 ;

--
-- Contenu de la table `connection_config`
--

INSERT INTO `connection_config` (`config_id`, `timeout`, `poll_timeout`, `sleep_poll_timeout`, `polling_enabled`, `login_attempts`) VALUES
(1, 5000, 10000, 60000, 1, 5);

-- --------------------------------------------------------

--
-- Structure de la table `corporate_policies`
--

CREATE TABLE IF NOT EXISTS `corporate_policies` (
  `corporate_policy_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(2000) NOT NULL COMMENT 'Policy subject',
  `description` varchar(2000) NOT NULL COMMENT 'Policy textual description',
  `file` blob NOT NULL COMMENT 'Policy formalized in standard format (XACML,JSON,...), to make it machine readable',
  `date` date NOT NULL COMMENT 'Date of creation of the policy',
  PRIMARY KEY (`corporate_policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which contains the current set of corporate security policies, both containing textual descriptions and formalization files.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `decision`
--

CREATE TABLE IF NOT EXISTS `decision` (
  `decision_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `access_request_id` bigint(20) unsigned NOT NULL,
  `risk_communication_id` int(10) unsigned NOT NULL,
  `value` enum('GRANTED','STRONGDENY','MAYBE','UPTOYOU') NOT NULL,
  `information` text,
  `solving_risktreatment` int(11) DEFAULT NULL,
  `time` datetime NOT NULL COMMENT 'When the decision was made',
  PRIMARY KEY (`decision_id`),
  KEY `decision-access_request:access_request_id_idx` (`access_request_id`),
  KEY `decision-risk_communication:risk_communication_id_idx` (`risk_communication_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which stores all decision computed by the RT2AE. All fields are defined in the table.' AUTO_INCREMENT=569 ;

-- --------------------------------------------------------

--
-- Structure de la table `decision_trustvalues`
--

CREATE TABLE IF NOT EXISTS `decision_trustvalues` (
  `decision_trustvalue_id` int(11) NOT NULL AUTO_INCREMENT,
  `decision_id` bigint(20) unsigned NOT NULL,
  `usertrustvalue` double NOT NULL,
  `devicetrustvalue` double NOT NULL,
  PRIMARY KEY (`decision_trustvalue_id`),
  UNIQUE KEY `decision_id` (`decision_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `default_policies`
--

CREATE TABLE IF NOT EXISTS `default_policies` (
  `default_policy_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(2000) NOT NULL COMMENT 'Policy subject',
  `description` varchar(2000) NOT NULL COMMENT 'Policy textual description',
  `file` blob NOT NULL COMMENT 'Policy formalized in standard format (XACML,JSON,...), to make it machine readable',
  `date` date NOT NULL COMMENT 'Date of creation of the policy',
  PRIMARY KEY (`default_policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which contains the default security policies, both containing textual descriptions and formalization files.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `devices`
--

CREATE TABLE IF NOT EXISTS `devices` (
  `device_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `type` int(10) unsigned NOT NULL COMMENT 'FK to table DEVICE_TYPE(device_type_id)',
  `description` varchar(100) DEFAULT NULL,
  `IMEI` varchar(30) DEFAULT NULL COMMENT 'In the format XXXXXX YY ZZZZZZ W',
  `MAC` varchar(30) DEFAULT NULL COMMENT 'In the format FF:FF:FF:FF:FF:FF:FF:FF',
  `OS_name` varchar(30) DEFAULT NULL COMMENT 'The operating system of the device',
  `OS_version` varchar(20) DEFAULT NULL COMMENT 'The operating system of the device',
  `trust_value` double DEFAULT NULL COMMENT 'The trust value of the device will be between 0 and 1',
  `security_level` smallint(6) DEFAULT NULL COMMENT 'The security level of the device is based on the device security state',
  `certificate` blob,
  `owner_type` enum('COMPANY','USER') DEFAULT NULL,
  PRIMARY KEY (`device_id`),
  KEY `device_type_id_idx` (`type`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table that has been created due to the importance of having a record of the different devices that are using company assets and the need of pairing a device with an owner. Like the users, the devices have also a defined trust value that may be changed by RT2AE decisions.' AUTO_INCREMENT=334 ;

-- --------------------------------------------------------

--
-- Structure de la table `device_config`
--

CREATE TABLE IF NOT EXISTS `device_config` (
  `device_config_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `device_config_name` varchar(30) NOT NULL COMMENT 'Name of the configuration',
  `min_event_cache_size` int(10) unsigned NOT NULL DEFAULT '100' COMMENT 'Minimum number of events to be stored in the local cache',
  `max_request_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Maximum amount of milliseconds waiting for an answer from the server side',
  PRIMARY KEY (`device_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Device configuration parameters' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `device_security_state`
--

CREATE TABLE IF NOT EXISTS `device_security_state` (
  `device_security_state_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`device_security_state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table will store the list of clue about the security state of the device. This table has been modified about the DeviceSecurityState' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `device_type`
--

CREATE TABLE IF NOT EXISTS `device_type` (
  `device_type_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(30) NOT NULL COMMENT 'Types of devices, such as DESKTOP_PC, LAPTOP, TABLET, SMARTPHONE, PALM, PDA',
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`device_type_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='This table is directly related to the previous one, as it contains the information about the type of devices that can be registered in the system.' AUTO_INCREMENT=1224 ;

--
-- Contenu de la table `device_type`
--

INSERT INTO `device_type` (`device_type_id`, `type`, `description`) VALUES
(1222, '1222', 'device'),
(1223, '1223', 'device');

-- --------------------------------------------------------

--
-- Structure de la table `dictionary`
--

CREATE TABLE IF NOT EXISTS `dictionary` (
  `term_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `term_name` varchar(50) NOT NULL COMMENT 'Name of the term in the dictionary',
  `description` varchar(100) NOT NULL COMMENT 'Description of the term',
  `position` enum('ANTECEDENT','CONSEQUENT') NOT NULL COMMENT 'Position of the term in a rule',
  `type` varchar(30) NOT NULL COMMENT 'Type of the term',
  PRIMARY KEY (`term_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which will store all the possible terms (and values) as potential antecedents and consequents for rules. Some of these terms will be automatically extracted from other tables, such as the TYPES_OF_APPS, APPLICATIONS names, USERS names, LOCATIONS, and so on.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `domains`
--

CREATE TABLE IF NOT EXISTS `domains` (
  `domain_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT 'Name of the domain (e.g. Offers)',
  `description` varchar(100) DEFAULT NULL COMMENT 'Domain description (e.g. Company domain used to store commercial offers to be presented to concrete customers. This kind of information is strictly confidential.)',
  `sensitivity_id` int(11) NOT NULL DEFAULT '0' COMMENT 'Associated sensitivity level (strictly confidential, protected, public,...) FK to sensitivity table',
  PRIMARY KEY (`domain_id`),
  KEY `sensitivity_id_idx` (`sensitivity_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which describes the different domains that might apply for different company resources. Depending on this domain, it will have a different sensitivity level.' AUTO_INCREMENT=8 ;

-- --------------------------------------------------------

--
-- Structure de la table `event_type`
--

CREATE TABLE IF NOT EXISTS `event_type` (
  `event_type_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_type_key` varchar(200) NOT NULL COMMENT 'Possible values are: {USER_ACTION,SENSOR_CONTEXT,USER_FEEDBACK} as simple events and {DECISION,THREAT_CLUE,ADDITIONAL_PROTECTION,SECURITY_INCIDENT,DEVICE_POLICY_UPDATE} as complex events',
  `event_level` varchar(200) NOT NULL COMMENT 'Possible values are: SIMPLE_EVENT (corresponding to events that are generated by monitoring, without server processing) and COMPLEX_EVENT (events generated from the correlation or aggregation of other simple events)',
  PRIMARY KEY (`event_type_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which describes the possible types of events' AUTO_INCREMENT=25 ;

--
-- Contenu de la table `event_type`
--

INSERT INTO `event_type` (`event_type_id`, `event_type_key`, `event_level`) VALUES
(1, 'LOG_IN', 'SIMPLE_EVENT'),
(2, 'LOG_OUT', 'SIMPLE_EVENT'),
(3, 'START', 'SIMPLE_EVENT'),
(4, 'RESUME', 'SIMPLE_EVENT'),
(5, 'STOP', 'SIMPLE_EVENT'),
(6, 'RESTART', 'SIMPLE_EVENT'),
(7, 'ACTION_REMOTE_FILE_ACCESS', 'SIMPLE_EVENT'),
(8, 'CONTEXT_SENSOR_CONNECTIVITY', 'SIMPLE_EVENT'),
(9, 'CONTEXT_SENSOR_DEVICE_PROTECTION', 'SIMPLE_EVENT'),
(10, 'ACTION_APP_OPEN', 'SIMPLE_EVENT'),
(11, 'ACTION_SEND_MAIL', 'SIMPLE_EVENT'),
(12, 'VIRUS_FOUND', 'SIMPLE_EVENT'),
(13, 'VIRUS_CLEANED', 'SIMPLE_EVENT'),
(14, 'SECURITY_PROPERTY_CHANGED', 'SIMPLE_EVENT'),
(15, 'SAVE_ASSET', 'SIMPLE_EVENT'),
(16, 'CONTEXT_SENSOR_PACKAGE', 'SIMPLE_EVENT'),
(17, 'SECURITY_VIOLATION', 'COMPLEX_EVENT'),
(18, 'SECURITY_INCIDENT', 'COMPLEX_EVENT'),
(19, 'CONFIGURATION_CHANGE', 'COMPLEX_EVENT'),
(20, 'DECISION', 'COMPLEX_EVENT'),
(21, 'DEVICE_POLICY_SENT', 'COMPLEX_EVENT'),
(22, 'CLUE_DETECTED', 'COMPLEX_EVENT'),
(23, 'CONTEXT_SENSOR_APP', 'SIMPLE_EVENT'),
(24, 'user_entered_password_field', 'SIMPLE_EVENT');

-- --------------------------------------------------------

--
-- Structure de la table `legal_aspects`
--

CREATE TABLE IF NOT EXISTS `legal_aspects` (
  `description` varchar(50) NOT NULL,
  `KRS_hard_limit` int(10) unsigned NOT NULL DEFAULT '180' COMMENT 'Duration of data in days for the Knowledge Refinement System. Default=6 months',
  `RT2AE_hard_limit` int(10) unsigned NOT NULL DEFAULT '180' COMMENT 'Duration of data in days for the RT2AE. Default=6 months',
  `EP_hard_limit` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'Duration of data in days for the Event Processor. Default=6 months',
  `data_complete_erasure` binary(1) NOT NULL DEFAULT '1' COMMENT 'If ''1'' (TRUE) data will be completely removed from the database once the duration has expired.',
  PRIMARY KEY (`description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table containing data related with user''s privacy and legality in the system';

-- --------------------------------------------------------

--
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
-- Contenu de la table `list_ofpossible_risktreatments`
--

INSERT INTO `list_ofpossible_risktreatments` (`listofpossiblerisktreatment_id`, `description`, `Spanish`, `German`, `French`) VALUES
(1, 'Sending e-mail with virus\\nYour system is infected with a virus and you want to\\n send an attachment via e-mail.\\n This may cause critical system failure and puts the\\n receiver at risk. Remove the virus first.', 'Envío de e-mail con el virus \\ system nTu está infectado con un virus y desea \\ n enviar un archivo adjunto por correo electrónico. \\ N Esto puede causar un fallo del sistema crítico y pone el receptor \\ n en riesgo. Eliminar el virus por primera vez.', 'Senden von E-Mail mit einem Virus \\n Ihre System ist mit einem Virus infiziert ist und Sie\\n Nachricht einen Anhang per E-Mail wünschen.\\n Das kann kritische Systemfehler verursachen und stellt das \\n Empfänger gefährdet. Entfernen Sie zuerst das Virus', 'Envoi d''e-mail avec un virus \\n Votre système est infecté par un virus et que vous voulez \\ n envoyer une pièce jointe dans votre e-mail. \\ N Cela peut provoquer une défaillance du système et mettre celui qui reçoit le mail \\n en danger. Eliminez en premi'),
(2, 'Opening sensitive document in unsecure network\\n You are connected to an unsecure network and try\\n to open a sensitive document.\\n Information sent over this network is not encrypted\\n and might be visible to other people.\\n Switch to a secure network.', 'es', 'de', 'fr'),
(3, 'Saving confidential document\\n You want to save a confidential document on your device.\\n If you loose your\\n device, other people may be able to\\n access the document.', 'es', 'de', 'fr'),
(4, 'Your Antivirus is not running on your device\\nPlease launch your Antivirus\\n In order to protect your device', 'es', 'de', 'fr'),
(5, 'The user is connected to unsecure network, he has to switch to secure network with wpa2 encryption', 'es', 'de', 'fr'),
(6, 'There is too much risk in your context situation, the probability of a threat leading to a security incident is too high ', 'es', 'de', 'fr');

-- --------------------------------------------------------

--
-- Structure de la table `muses_config`
--

CREATE TABLE IF NOT EXISTS `muses_config` (
  `config_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `config_name` varchar(30) NOT NULL COMMENT 'Name of the configuration',
  `silent_mode` tinyint(4) NOT NULL COMMENT 'Specify whether all devices should run MUSES application in silent mode (true), or verbose (false)',
  `access_attempts_before_blocking` int(10) unsigned NOT NULL DEFAULT '5',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='MUSES Server configuration parameters' AUTO_INCREMENT=2 ;

--
-- Contenu de la table `muses_config`
--

INSERT INTO `muses_config` (`config_id`, `config_name`, `silent_mode`, `access_attempts_before_blocking`) VALUES
(1, 'VERBOSE', 0, 3);

-- --------------------------------------------------------

--
-- Structure de la table `outcome`
--

CREATE TABLE IF NOT EXISTS `outcome` (
  `outcome_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `description` longtext,
  `costbenefit` double DEFAULT NULL,
  `threat_id` bigint(20) NOT NULL,
  PRIMARY KEY (`outcome_id`),
  KEY `threat_outcome_link` (`threat_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1130 ;

-- --------------------------------------------------------

--
-- Structure de la table `patterns_krs`
--

CREATE TABLE IF NOT EXISTS `patterns_krs` (
  `log_entry_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `label` enum('GRANTED','STRONGDENY','MAYBE','UPTOYOU') NOT NULL COMMENT 'This is the decision taken for this event',
  `decision_cause` varchar(1000) DEFAULT NULL COMMENT 'Condition satisfied to detect the security violation',
  `event_type` varchar(200) NOT NULL COMMENT 'Possible values are: {USER_ACTION,SENSOR_CONTEXT,USER_FEEDBACK} as simple events and {DECISION,THREAT_CLUE,ADDITIONAL_PROTECTION,SECURITY_INCIDENT,DEVICE_POLICY_UPDATE} as complex events',
  `event_level` varchar(200) NOT NULL COMMENT 'Possible values are: SIMPLE_EVENT (corresponding to events that are generated by monitoring, without server processing) and COMPLEX_EVENT (events generated from the correlation or aggregation of other simple events)',
  `username` varchar(50) NOT NULL COMMENT 'The username is taken rather than the actual name of the user, just to identify the source of the event',
  `password_length` int(11) NOT NULL COMMENT 'The length of the user''s password',
  `letters_in_password` int(11) NOT NULL COMMENT 'How many letters does the user''s password contain',
  `numbers_in_password` int(11) NOT NULL COMMENT 'How many numbers does the user''s password contain',
  `passwd_has_capital_letters` int(11) NOT NULL COMMENT 'Specify whether the user''s password has (1) or has not (0) capital letters as well as non capital letters in their password',
  `user_trust_value` double NOT NULL COMMENT 'Trust value that the user had when the event was thrown',
  `activated_account` int(11) NOT NULL COMMENT 'Specify whether the user''s account was active (1) or not (0) when the event was thrown',
  `user_role` varchar(50) NOT NULL COMMENT 'Role of the user in the company',
  `event_detection` datetime NOT NULL COMMENT 'Time of detection of the event',
  `device_type` varchar(30) NOT NULL COMMENT 'Type of the device from which the event was sent. Possible values are: {DESKTOP_PC, LAPTOP, TABLET, SMARTPHONE}',
  `device_OS` varchar(50) NOT NULL COMMENT 'OS type and version of the user''s device',
  `device_has_antivirus` int(11) NOT NULL COMMENT 'Specify whether the user''s device has (1) or has not (0) a trusted antivirus installed',
  `device_has_certificate` int(11) NOT NULL COMMENT 'Specify whether the user''s device has (1) or has not (0) a valid certificate installed',
  `device_trust_value` double NOT NULL COMMENT 'Trust value that the device had when the event was thrown',
  `device_security_level` smallint(6) DEFAULT NULL COMMENT 'The security level of the device is based on the device security state',
  `device_owned_by` varchar(20) NOT NULL COMMENT 'Owner of the device. Possible values are: {COMPANY, EMPLOYEE}',
  `device_has_password` int(11) NOT NULL COMMENT 'Specify whether the user''s device has (1) or has not (0) the screen protected by password',
  `device_screen_timeout` int(11) NOT NULL COMMENT 'Time to lock the screen in the device, in seconds',
  `device_has_accessibility` int(11) NOT NULL COMMENT 'Specify whether the user''s device has (1) or has not (0) the accessibility enabled',
  `device_is_rooted` int(11) NOT NULL COMMENT 'Specify whether the user''s device is (1) or is not (0) rooted',
  `app_name` varchar(50) NOT NULL COMMENT 'Name and version of the application from which the event was thrown',
  `app_vendor` varchar(30) NOT NULL COMMENT 'Vendor of the application',
  `app_is_MUSES_aware` int(11) NOT NULL COMMENT 'If TRUE (1), the application can be monitored easily (it interacts with the system through the API)',
  `asset_name` varchar(30) DEFAULT NULL COMMENT 'Title of the asset that is being accessed in the event',
  `asset_value` double DEFAULT NULL COMMENT 'Represents the real value of the asset',
  `asset_confidential_level` enum('PUBLIC','INTERNAL','CONFIDENTIAL','STRICTLY_CONFIDENTIAL','NONE') DEFAULT NULL COMMENT 'The confidentiality level of the asset',
  `asset_location` varchar(100) DEFAULT NULL COMMENT 'Location of the asset in the hard drive',
  `mail_recipient_allowed` int(11) DEFAULT NULL COMMENT 'If the event is about sending an email, specify whether the recipient belongs (1) or not (0) to the company',
  `mail_contains_cc` int(11) DEFAULT NULL COMMENT 'If the event is about sending an email, specify whether the mail contains (1) or not (0) someone in CC',
  `mail_contains_bcc` int(11) DEFAULT NULL COMMENT 'If the event is about sending an email, specify whether the mail contains (1) or not (0) someone in BCC',
  `mail_has_attachment` int(11) DEFAULT NULL COMMENT 'If the event is about sending an email, specify whether the mail contains (1) or not (0) an attachment',
  PRIMARY KEY (`log_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Patterns built with the extracted information from the events' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `refined_security_rules`
--

CREATE TABLE IF NOT EXISTS `refined_security_rules` (
  `refined_security_rules_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `original_security_rule_id` bigint(20) unsigned NOT NULL COMMENT 'Initial security rule which was refined. If it has been inferred this field will be empty.',
  `name` varchar(2000) NOT NULL COMMENT 'If it is a refinement, the name will be the original name + "REFINED"',
  `file` blob COMMENT 'File in DRL format, containing the rule''s code, to make it machine readable',
  `status` enum('PROPOSED','VALIDATED','EXPIRED') NOT NULL COMMENT 'Current status of the rule. VALIDATED means that the CSO has approved this rule, so it can be inserted into the SECURITY_RULES table',
  `modification` datetime NOT NULL COMMENT 'Date of creation/modification of the rule',
  PRIMARY KEY (`refined_security_rules_id`),
  KEY `refined_security_rules-security_rules:security_rule_id_idx` (`original_security_rule_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which contains the potential set of security rules improved or inferred by the KRS.' AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Structure de la table `risk_communication`
--

CREATE TABLE IF NOT EXISTS `risk_communication` (
  `risk_communication_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL COMMENT 'Textual description of the risk communication',
  PRIMARY KEY (`risk_communication_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=911 ;

-- --------------------------------------------------------

--
-- Structure de la table `risk_information`
--

CREATE TABLE IF NOT EXISTS `risk_information` (
  `risk_information_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `threat_type` int(11) unsigned NOT NULL COMMENT 'FK to table THREAT_TYPE(threat_type_id)',
  `asset_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table ASSET(asset_id)',
  `probability` double unsigned NOT NULL COMMENT 'Probability of the threat',
  `event_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table EVENTS(event_id)',
  PRIMARY KEY (`risk_information_id`),
  KEY `threat_type_id_idx` (`threat_type`),
  KEY `risk_information-simple_events_idx` (`event_id`),
  KEY `risk_information-assets_idx` (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='It will store all data about risk meaning about threat. All fields are defined in the table.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `risk_policy`
--

CREATE TABLE IF NOT EXISTS `risk_policy` (
  `risk_policy_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `riskvalue` double NOT NULL,
  `description` longtext NOT NULL,
  PRIMARY KEY (`risk_policy_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=113 ;

-- --------------------------------------------------------

--
-- Structure de la table `risk_treatment`
--

CREATE TABLE IF NOT EXISTS `risk_treatment` (
  `risk_treatment_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL COMMENT 'Description of risk treatment',
  `risk_communication_id` int(10) unsigned NOT NULL COMMENT 'FK to table RISK_COMMUNICATION(risk_communication_id)',
  PRIMARY KEY (`risk_treatment_id`),
  KEY `risk_treatment-risk_communication:risk_communication_id_idx` (`risk_communication_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='This table will store all risk treatment computed by the RT2AE. All fields are defined in the table.' AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- Structure de la table `roles`
--

CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL COMMENT 'Role description',
  `security_level` smallint(6) DEFAULT NULL COMMENT 'Associated security level',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which describes the role of the users inside the company, for example, if he is the CSO, the CTO, an accountant, a developer...' AUTO_INCREMENT=146 ;

-- --------------------------------------------------------

--
-- Structure de la table `security_incident`
--

CREATE TABLE IF NOT EXISTS `security_incident` (
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
  KEY `security_incident-decision:decision_id_idx` (`decision_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which includes any security incident detected by the Event Processor' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `security_rules`
--

CREATE TABLE IF NOT EXISTS `security_rules` (
  `security_rule_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(2000) NOT NULL COMMENT 'Name of the security rule',
  `description` varchar(2000) NOT NULL COMMENT 'Textual description of the security rule',
  `file` blob COMMENT 'File in DRL format, containing the rule''s code, to make it machine readable',
  `status` enum('VALIDATED','DRAFT','EXPIRED') NOT NULL COMMENT 'Current status of the rule. Only validated rules will be inserted into the production working memory of the Event Processor.',
  `refined` binary(1) NOT NULL DEFAULT '0' COMMENT 'If TRUE (1), the rule has been inferred by the KRS. ',
  `source_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table SOURCES(source_id) Identification of the component owner, in other words, the originator of the last version of the rule (e.g. Event Processor if it is manual or based on expert knowledge or Knowledge Refinement System if the current version is the outcome of knowledge refinement)',
  `modification` datetime NOT NULL COMMENT 'Date of creation of the rule',
  PRIMARY KEY (`security_rule_id`),
  KEY `security_rules-sources:source_id_idx` (`source_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=801 ;

-- --------------------------------------------------------

--
-- Structure de la table `security_violation`
--

CREATE TABLE IF NOT EXISTS `security_violation` (
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
  KEY `security_violation-devices:device_id` (`device_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table to store any detected security violation' AUTO_INCREMENT=1378 ;

-- --------------------------------------------------------

--
-- Structure de la table `sensitivity`
--

CREATE TABLE IF NOT EXISTS `sensitivity` (
  `sensitivity_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `level` smallint(6) NOT NULL COMMENT 'Associated numeric value corresponding to different levels of sensitivity from 1 for Strictly confidential, to 3 for public',
  PRIMARY KEY (`sensitivity_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table for listing all the possible values representing sensitivity of corporate data' AUTO_INCREMENT=26 ;

-- --------------------------------------------------------

--
-- Structure de la table `sensor_configuration`
--

CREATE TABLE IF NOT EXISTS `sensor_configuration` (
  `id` int(11) NOT NULL,
  `sensor_type` varchar(45) NOT NULL,
  `keyproperty` varchar(45) NOT NULL,
  `valueproperty` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table contains sensor configuration properties';

--
-- Contenu de la table `sensor_configuration`
--

INSERT INTO `sensor_configuration` (`id`, `sensor_type`, `keyproperty`, `valueproperty`) VALUES
(1, 'CONTEXT_SENSOR_DEVICE_PROTECTION', 'trustedav', 'avast! Mobile Security'),
(2, 'CONTEXT_SENSOR_DEVICE_PROTECTION', 'trustedav', 'Mobile Security & Antivirus'),
(3, 'CONTEXT_SENSOR_DEVICE_PROTECTION', 'trustedav', 'Avira Antivirus Security'),
(4, 'CONTEXT_SENSOR_DEVICE_PROTECTION', 'trustedav', 'Norton Security & Antivirus'),
(5, 'CONTEXT_SENSOR_DEVICE_PROTECTION', 'trustedav', 'CM Security & Find My Phone'),
(6, 'CONTEXT_SENSOR_DEVICE_PROTECTION', 'enabled', 'true'),
(7, 'CONTEXT_SENSOR_LOCATION', 'mindistance', '10'),
(8, 'CONTEXT_SENSOR_LOCATION', 'mindtime', '400'),
(9, 'CONTEXT_SENSOR_LOCATION', 'radius', '12.0'),
(10, 'CONTEXT_SENSOR_LOCATION', 'enabled', 'true'),
(11, 'CONTEXT_SENSOR_FILEOBSERVER', 'path', '/SWE/'),
(12, 'CONTEXT_SENSOR_FILEOBSERVER', 'enabled', 'true'),
(13, 'CONTEXT_SENSOR_APP', 'enabled', 'true'),
(14, 'CONTEXT_SENSOR_CONNECTIVITY', 'enabled', 'true'),
(15, 'CONTEXT_SENSOR_INTERACTION', 'enabled', 'true'),
(16, 'CONTEXT_SENSOR_PACKAGE', 'enabled', 'true'),
(17, 'CONTEXT_SENSOR_SETTINGS', 'enabled', 'true'),
(18, 'CONTEXT_SENSOR_NOTIFICATION', 'enabled', 'true');

-- --------------------------------------------------------

--
-- Structure de la table `simple_events`
--

CREATE TABLE IF NOT EXISTS `simple_events` (
  `event_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_type_id` int(10) unsigned NOT NULL COMMENT 'Type of the event. This is a reference to the EVENT_TYPES table, whose possible values are: {USER_ACTION,SENSOR_CONTEXT,USER_FEEDBACK} as simple events',
  `user_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table USERS(user_id)',
  `device_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table DEVICES(device_id)',
  `app_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table APPLICATIONS(app_id)',
  `asset_id` bigint(20) unsigned DEFAULT NULL COMMENT 'FK to table ASSETS(asset_id)',
  `data` mediumtext NOT NULL COMMENT 'Raw event content (this is the content of the whole event in JSON format)',
  `date` date NOT NULL COMMENT 'Date when the event happens',
  `time` time NOT NULL COMMENT 'Time at when the event happens',
  `duration` int(11) DEFAULT '0' COMMENT 'Duration in milliseconds',
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
  KEY `users_id_idx` (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT=' Table which describes the set of simple or primitive events in the MUSES system. Each event is paired with:' AUTO_INCREMENT=16958 ;

-- --------------------------------------------------------

--
-- Structure de la table `sources`
--

CREATE TABLE IF NOT EXISTS `sources` (
  `source_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT 'Name of the source component that originates actions, events,...',
  PRIMARY KEY (`source_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=17 ;

-- --------------------------------------------------------

--
-- Structure de la table `system_log_krs`
--

CREATE TABLE IF NOT EXISTS `system_log_krs` (
  `log_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `previous_event_id` bigint(20) unsigned NOT NULL COMMENT 'Previous event. FK to EVENTS(event_id)',
  `current_event_id` bigint(20) unsigned NOT NULL COMMENT 'Current event. FK to EVENTS(event_id)',
  `decision_id` bigint(20) unsigned NOT NULL COMMENT 'Corresponding decision to that event. FK to DECISION(decision_id)',
  `user_behaviour_id` bigint(20) unsigned NOT NULL COMMENT 'Corresponding user''s behaviour for the event. FK to USER_BEHAVIOUR(user_behaviour_id)',
  `security_incident_id` bigint(20) unsigned NOT NULL COMMENT 'Corresponding security incident for the event. FK to SECURITY_INCIDENT(security_incident_id)',
  `device_security_state` bigint(20) unsigned NOT NULL COMMENT 'Corresponding device security state for the event. FK to DEVICE_SECURITY_STATE(device_security_state_id)',
  `risk_treatment` int(10) unsigned NOT NULL COMMENT 'Corresponding risk treatment for the event. FK to RISK_TREATMENT(risk_treatment_id)',
  `start_time` datetime NOT NULL COMMENT 'When the sequence started',
  `finish_time` datetime NOT NULL COMMENT 'When the sequence finished',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table containing the useful information in the form of a log of the system working, in order to further being able to simulate that system workflow in an evaluation process for new (inferred or refined) rules.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `term_values`
--

CREATE TABLE IF NOT EXISTS `term_values` (
  `value_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `term_id` bigint(20) unsigned NOT NULL COMMENT 'FK to the term which takes this value: table DICTIONARY(term_id)',
  `value` varchar(50) NOT NULL COMMENT 'Value of the term',
  `description` varchar(100) DEFAULT NULL COMMENT 'Description of the value',
  PRIMARY KEY (`value_id`),
  KEY `term_values-dictionary:term_id_idx` (`term_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table containing all the possible values for every term. These values will be extracted from other tables as in the DICTIONARY' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `threat`
--

CREATE TABLE IF NOT EXISTS `threat` (
  `threat_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `description` longtext NOT NULL,
  `probability` double NOT NULL,
  `occurences` int(11) DEFAULT '0',
  `badOutcomeCount` int(11) DEFAULT '0',
  `ttl` int(11) DEFAULT '0',
  PRIMARY KEY (`threat_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1119 ;

-- --------------------------------------------------------

--
-- Structure de la table `threat_clue`
--

CREATE TABLE IF NOT EXISTS `threat_clue` (
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
  KEY `threat_clue-threat_type:threat_type_id_idx` (`threat_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which include any threat clue detected by the Event Processor' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `threat_type`
--

CREATE TABLE IF NOT EXISTS `threat_type` (
  `threat_type_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL COMMENT 'Types of threat, such as WI-FI_SNIFFING, UNSECURE_NETWORK,MALWARE,SPYWARE,..',
  `description` varchar(100) NOT NULL COMMENT 'Description of the threat',
  PRIMARY KEY (`threat_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table is directly related to the RISK_INFORMATION table, as it contains the information about the type of threat.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='This table contains user information, similar to a profile. It has personal data (name, email) as well as company data (user''s role inside the company). Additionally, a trust value has been included for RT2AE calculation processes.' AUTO_INCREMENT=308 ;

--
-- Contenu de la table `users`
--

INSERT INTO `users` (`user_id`, `name`, `surname`, `email`, `username`, `password`, `enabled`, `trust_value`, `role_id`, `language`) VALUES
(1, 'admin', 'admin', 'admin@muses.com', 'muses', 'muses', 1, 0.5, 100, 'en');

-- --------------------------------------------------------

--
-- Structure de la table `user_authorization`
--

CREATE TABLE IF NOT EXISTS `user_authorization` (
  `user_authorization_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table USERS(user_id)',
  `role_id` int(10) unsigned NOT NULL COMMENT 'FK to table ROLES(role_id)',
  PRIMARY KEY (`user_authorization_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=90 ;

-- --------------------------------------------------------

--
-- Structure de la table `user_behaviour`
--

CREATE TABLE IF NOT EXISTS `user_behaviour` (
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
  KEY `user_behaviour-decision:decision_id_idx` (`decision_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table which will store all user behaviour data. All fields are defined in the table.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `zone`
--

CREATE TABLE IF NOT EXISTS `zone` (
  `zone_id` int(11) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `long` double DEFAULT NULL,
  `lat` double DEFAULT NULL,
  `radius` float DEFAULT NULL COMMENT 'Zone radius in meters',
  PRIMARY KEY (`zone_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='List of zones referring to special security requirements to apply to the company';

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `additional_protection`
--
ALTER TABLE `additional_protection`
  ADD CONSTRAINT `additional_protection-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `additional_protection-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `additional_protection-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `applications`
--
ALTER TABLE `applications`
  ADD CONSTRAINT `applications-app_type:app_type_id` FOREIGN KEY (`type`) REFERENCES `app_type` (`app_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `decision`
--
ALTER TABLE `decision`
  ADD CONSTRAINT `decision-access_request:access_request_id` FOREIGN KEY (`access_request_id`) REFERENCES `access_request` (`access_request_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `decision-risk_communication:risk_communication_id` FOREIGN KEY (`risk_communication_id`) REFERENCES `risk_communication` (`risk_communication_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `decision_trustvalues`
--
ALTER TABLE `decision_trustvalues`
  ADD CONSTRAINT `decision_trustvalues_ibfk_1` FOREIGN KEY (`decision_id`) REFERENCES `decision` (`decision_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Contraintes pour la table `devices`
--
ALTER TABLE `devices`
  ADD CONSTRAINT `devices-device_type:device_type_id` FOREIGN KEY (`type`) REFERENCES `device_type` (`device_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `domains`
--
ALTER TABLE `domains`
  ADD CONSTRAINT `domains-sensitivity:sensitivity_id` FOREIGN KEY (`sensitivity_id`) REFERENCES `sensitivity` (`sensitivity_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `outcome`
--
ALTER TABLE `outcome`
  ADD CONSTRAINT `outcome_ibfk_1` FOREIGN KEY (`threat_id`) REFERENCES `threat` (`threat_id`);

--
-- Contraintes pour la table `refined_security_rules`
--
ALTER TABLE `refined_security_rules`
  ADD CONSTRAINT `refined_security_rules-security_rules:security_rule_id` FOREIGN KEY (`original_security_rule_id`) REFERENCES `security_rules` (`security_rule_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `risk_information`
--
ALTER TABLE `risk_information`
  ADD CONSTRAINT `risk_information-assets` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`asset_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `risk_information-simple_events` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `risk_information-threat_type:threat_type_id` FOREIGN KEY (`threat_type`) REFERENCES `threat_type` (`threat_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `risk_treatment`
--
ALTER TABLE `risk_treatment`
  ADD CONSTRAINT `risk_treatment-risk_communication:risk_communication_id` FOREIGN KEY (`risk_communication_id`) REFERENCES `risk_communication` (`risk_communication_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `security_incident`
--
ALTER TABLE `security_incident`
  ADD CONSTRAINT `security_incident-decision:decision_id` FOREIGN KEY (`decision_id`) REFERENCES `decision` (`decision_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `security_incident-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `security_incident-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `security_incident-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `security_rules`
--
ALTER TABLE `security_rules`
  ADD CONSTRAINT `security_rules-sources:source_id` FOREIGN KEY (`source_id`) REFERENCES `sources` (`source_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `security_violation`
--
ALTER TABLE `security_violation`
  ADD CONSTRAINT `security_violation-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `security_violation-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `security_violation-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `simple_events`
--
ALTER TABLE `simple_events`
  ADD CONSTRAINT `simple_events-applications:app_id` FOREIGN KEY (`app_id`) REFERENCES `applications` (`app_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-assets:asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`asset_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-event_type_event:type_id` FOREIGN KEY (`event_type_id`) REFERENCES `event_type` (`event_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-sources:source_id` FOREIGN KEY (`source_id`) REFERENCES `sources` (`source_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `term_values`
--
ALTER TABLE `term_values`
  ADD CONSTRAINT `term_values-dictionary:term_id` FOREIGN KEY (`term_id`) REFERENCES `dictionary` (`term_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `threat_clue`
--
ALTER TABLE `threat_clue`
  ADD CONSTRAINT `threat_clue-acess_request:access_request_id` FOREIGN KEY (`access_request_id`) REFERENCES `access_request` (`access_request_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `threat_clue-assets:asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`asset_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `threat_clue-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `threat_clue-threat_type:threat_type_id` FOREIGN KEY (`threat_type_id`) REFERENCES `threat_type` (`threat_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `threat_clue-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Contraintes pour la table `user_behaviour`
--
ALTER TABLE `user_behaviour`
  ADD CONSTRAINT `user_behaviour-decision:decision_id` FOREIGN KEY (`decision_id`) REFERENCES `decision` (`decision_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `user_behaviour-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `user_behaviour-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
         