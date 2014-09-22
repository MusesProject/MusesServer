--
-- Database: `muses`
--
CREATE DATABASE IF NOT EXISTS `muses` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `muses`;

-- --------------------------------------------------------

--
-- Table structure for table `access_request`
--

DROP TABLE IF EXISTS `access_request`;
CREATE TABLE IF NOT EXISTS `access_request` (
  `access_request_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table SIMPLE_EVENTS(event_id)',
  `action` enum('DOWNLOAD_FILE','OPEN_APP','INSTALL_APP','OPEN_FILE') NOT NULL COMMENT 'Possible value of user actions for this concrete access request',
  `asset_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table ASSETS(asset_id)',
  `user_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table USERS(user_id)',
  `decision_id` bigint(20) unsigned NOT NULL COMMENT 'Foreign key to the final decision associated to the access request, once the decision is taken. FK to table DECISIONS(decision_id)',
  `modification` datetime DEFAULT NULL COMMENT 'Time of detection of the access request',
  `threat_id` int(11) NOT NULL,
  `solved` int(11) NOT NULL,
  `user_action` int(11) NOT NULL,
  PRIMARY KEY (`access_request_id`),
  UNIQUE KEY `access-request-threat_id` (`threat_id`),
  KEY `access_request-simple_events:event_id_idx` (`decision_id`),
  KEY `access_request-assets:asset_id_idx` (`asset_id`),
  KEY `access_request-users:user_id_idx` (`user_id`),
  KEY `access_request-simple_events:event_id_idx1` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which include any access request detected by the Event Processor' AUTO_INCREMENT=81 ;

--
-- Dumping data for table `access_request`
--

INSERT INTO `access_request` (`access_request_id`, `event_id`, `action`, `asset_id`, `user_id`, `decision_id`, `modification`, `threat_id`, `solved`, `user_action`) VALUES
(80, 2, 'DOWNLOAD_FILE', 1515, 200, 545, '2014-08-10 00:00:00', 1, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `additional_protection`
--

DROP TABLE IF EXISTS `additional_protection`;
CREATE TABLE IF NOT EXISTS `additional_protection` (
  `additional_protection_id` int(20) unsigned NOT NULL,
  `name` varchar(50) NOT NULL COMMENT 'Description of the additional protection',
  `access_request_id` int(10) unsigned DEFAULT NULL COMMENT 'FK to table ACCESS_REQUEST(access_request_id)',
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
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
CREATE TABLE IF NOT EXISTS `applications` (
  `app_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type` bigint(20) DEFAULT NULL COMMENT 'FK to table APP_TYPE(app_type_id)',
  `name` varchar(30) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `version` varchar(20) DEFAULT NULL COMMENT 'The current version of the application',
  `last_update` datetime DEFAULT NULL COMMENT 'Last update of application',
  `vendor` varchar(30) DEFAULT NULL COMMENT 'Vendor of the application',
  `is_MUSES_aware` int(11) DEFAULT NULL COMMENT 'If TRUE (1) -> the application can be monitored easily (it interacts with the system through the API)',
  PRIMARY KEY (`app_id`),
  KEY `app_type_id_idx` (`type`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='As MUSES will have both black and white lists, a description of the different applications installed on a device can be found in this table.' AUTO_INCREMENT=119 ;

--
-- Dumping data for table `applications`
--

INSERT INTO `applications` (`app_id`, `type`, `name`, `description`, `version`, `last_update`, `vendor`, `is_MUSES_aware`) VALUES
(117, 1174, 'musesawaew', 'desc', '89', '2014-08-15 00:00:00', 'android', NULL),
(118, 1175, 'musesawarew', 'desc', '89', '2014-08-15 00:00:00', 'android', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `app_type`
--

DROP TABLE IF EXISTS `app_type`;
CREATE TABLE IF NOT EXISTS `app_type` (
  `app_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(30) NOT NULL COMMENT 'Type of apps, such as "MAIL", "PDF_READER", "OFFICE", ...',
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`app_type_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table that simply describes the types of available applications.' AUTO_INCREMENT=1176 ;

--
-- Dumping data for table `app_type`
--

INSERT INTO `app_type` (`app_type_id`, `type`, `description`) VALUES
(1174, '1174', 'desc'),
(1175, '1175', 'desc');

-- --------------------------------------------------------

--
-- Table structure for table `assets`
--

DROP TABLE IF EXISTS `assets`;
CREATE TABLE IF NOT EXISTS `assets` (
  `asset_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(30) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `value` double NOT NULL COMMENT 'represents the real value of the asset',
  `confidential_level` enum('PUBLIC','INTERNAL','CONFIDENTIAL','STRICTLYCONFIDENTIAL') NOT NULL,
  `location` varchar(100) NOT NULL COMMENT 'Location of the asset in the hard drive',
  PRIMARY KEY (`asset_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='This one will store all Assets data. All fields are defined in the table.' AUTO_INCREMENT=1626 ;

--
-- Dumping data for table `assets`
--

INSERT INTO `assets` (`asset_id`, `title`, `description`, `value`, `confidential_level`, `location`) VALUES
(1, 'MusesBeerCompetition.txt', 'Beer Competition', 1000, 'INTERNAL', 'Sweden'),
(1515, 'ttle', 'desc', 1, 'PUBLIC', 'sweden'),
(1516, 'title', 'desc', 1, 'PUBLIC', 'sweden'),
(1520, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1521, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1522, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1523, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1524, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1525, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1526, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1527, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1528, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1529, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1530, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1531, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1532, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1533, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1534, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1535, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1536, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1537, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1538, 'title', 'desc', 200000, 'PUBLIC', 'Sweden'),
(1541, 'test', 'test', 0, 'PUBLIC', 'test'),
(1542, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'C/documents/Unige/Muses'),
(1543, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'C/documents/Unige/Muses'),
(1544, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1545, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1546, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1547, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1548, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1549, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1550, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1551, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1552, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1553, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1554, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1555, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1556, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1557, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1558, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1559, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1560, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1561, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1562, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1563, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1564, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1565, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1566, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1567, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1568, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1569, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1570, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1571, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1572, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1573, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1574, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1575, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1576, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1577, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1578, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1579, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1580, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1581, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1582, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1583, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1584, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1585, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1586, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1587, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1588, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1589, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1590, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1591, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1592, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1593, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1594, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1595, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1596, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1597, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1598, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1599, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1600, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1601, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1602, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1603, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1604, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1605, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1606, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1607, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1608, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1609, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1610, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1611, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1612, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1613, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1614, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1615, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1616, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1617, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1618, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1619, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1620, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1621, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1622, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1623, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1624, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva'),
(1625, 'Patent', 'Asset_Unige', 0, 'PUBLIC', 'Geneva');

-- --------------------------------------------------------

--
-- Table structure for table `clue`
--

DROP TABLE IF EXISTS `clue`;
CREATE TABLE IF NOT EXISTS `clue` (
  `clue_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` longtext NOT NULL,
  PRIMARY KEY (`clue_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=54 ;

--
-- Dumping data for table `clue`
--

INSERT INTO `clue` (`clue_id`, `value`) VALUES
(1, 'Wi-FI'),
(2, 'Wi-FI'),
(3, 'Wi-FI'),
(4, 'Wi-FI'),
(5, 'Wi-FI'),
(6, 'Wi-FI'),
(7, 'Wi-FI'),
(8, 'Wi-FI'),
(9, 'Wi-FI'),
(10, 'Wi-FI'),
(11, 'Wi-FI'),
(12, 'Wi-FI'),
(13, 'Wi-FI'),
(14, 'Wi-FI'),
(15, 'Wi-FI'),
(16, 'Wi-FI'),
(17, 'Wi-FI'),
(18, 'Wi-FI'),
(19, 'Wi-FI'),
(20, 'Wi-FI'),
(21, 'Wi-FI'),
(22, 'Wi-FI'),
(23, 'Wi-FI'),
(24, 'Wi-FI'),
(25, 'Wi-FI'),
(26, 'Wi-FI'),
(27, 'Wi-FI'),
(28, 'Wi-FI'),
(29, 'Wi-FI'),
(30, 'Wi-FI'),
(31, 'Wi-FI'),
(32, 'Wi-FI'),
(33, 'Wi-FI'),
(34, 'Wi-FI'),
(35, 'Wi-FI'),
(36, 'Wi-FI'),
(37, 'Wi-FI'),
(38, 'Wi-FI'),
(39, 'Wi-FI'),
(40, 'Wi-FI'),
(41, 'Wi-FI'),
(42, 'Wi-FI'),
(43, 'Wi-FI'),
(44, 'Wi-FI'),
(45, 'Wi-FI'),
(46, 'Wi-FI'),
(47, 'Wi-FI'),
(48, 'Wi-FI'),
(49, 'Wi-FI'),
(50, 'Wi-FI'),
(51, 'Wi-FI'),
(52, 'Wi-FI'),
(53, 'Wi-FI');

-- --------------------------------------------------------

--
-- Table structure for table `corporate_policies`
--

DROP TABLE IF EXISTS `corporate_policies`;
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
-- Table structure for table `decision`
--

DROP TABLE IF EXISTS `decision`;
CREATE TABLE IF NOT EXISTS `decision` (
  `decision_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `access_request_id` bigint(20) unsigned NOT NULL,
  `risk_communication_id` int(10) unsigned NOT NULL,
  `value` enum('GRANTED','STRONGDENY','MAYBE','UPTOYOU') NOT NULL,
  `time` datetime NOT NULL COMMENT 'When the decision was made',
  PRIMARY KEY (`decision_id`),
  KEY `decision-access_request:access_request_id_idx` (`access_request_id`),
  KEY `decision-risk_communication:risk_communication_id_idx` (`risk_communication_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which stores all decision computed by the RT2AE. All fields are defined in the table.' AUTO_INCREMENT=558 ;

--
-- Dumping data for table `decision`
--

INSERT INTO `decision` (`decision_id`, `access_request_id`, `risk_communication_id`, `value`, `time`) VALUES
(545, 80, 900, 'GRANTED', '2014-08-10 00:00:00'),
(546, 80, 900, 'GRANTED', '2014-08-11 00:00:00'),
(548, 80, 900, 'GRANTED', '2014-08-12 00:00:00'),
(549, 80, 900, 'GRANTED', '2014-08-12 00:00:00'),
(550, 80, 900, 'GRANTED', '2014-08-12 00:00:00'),
(551, 80, 900, 'GRANTED', '2014-08-12 00:00:00'),
(552, 80, 900, 'GRANTED', '2014-08-12 00:00:00'),
(553, 80, 900, 'GRANTED', '2014-08-12 00:00:00'),
(554, 80, 900, 'GRANTED', '2014-08-12 00:00:00'),
(555, 80, 900, 'GRANTED', '2014-08-12 00:00:00'),
(556, 80, 900, 'GRANTED', '2014-08-12 00:00:00'),
(557, 80, 900, 'GRANTED', '2014-08-12 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `devices`
--

DROP TABLE IF EXISTS `devices`;
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table that has been created due to the importance of having a record of the different devices that are using company assets and the need of pairing a device with an owner. Like the users, the devices have also a defined trust value that may be changed by RT2AE decisions.' AUTO_INCREMENT=203 ;

--
-- Dumping data for table `devices`
--

INSERT INTO `devices` (`device_id`, `name`, `type`, `description`, `IMEI`, `MAC`, `OS_name`, `OS_version`, `trust_value`, `security_level`, `certificate`, `owner_type`) VALUES
(201, 'f', 1222, 'device', '545', '0', 'a', '0', 0, 0, NULL, NULL),
(202, 'f', 1223, 'device', '0454', '0', 'a', '0', 0, 0, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `device_config`
--

DROP TABLE IF EXISTS `device_config`;
CREATE TABLE IF NOT EXISTS `device_config` (
  `device_config_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `device_config_name` varchar(30) NOT NULL COMMENT 'Name of the configuration',
  `min_event_cache_size` int(10) unsigned NOT NULL DEFAULT '100' COMMENT 'Minimum number of events to be stored in the local cache',
  `max_request_time` int(10) unsigned NOT NULL COMMENT 'Maximum amount of milliseconds waiting for an answer from the server side',
  PRIMARY KEY (`device_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Device configuration parameters' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `device_security_state`
--

DROP TABLE IF EXISTS `device_security_state`;
CREATE TABLE IF NOT EXISTS `device_security_state` (
  `device_security_state_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`device_security_state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table will store the list of clue about the security state of the device. This table has been modified about the DeviceSecurityState' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `device_type`
--

DROP TABLE IF EXISTS `device_type`;
CREATE TABLE IF NOT EXISTS `device_type` (
  `device_type_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(30) NOT NULL COMMENT 'Types of devices, such as DESKTOP_PC, LAPTOP, TABLET, SMARTPHONE, PALM, PDA',
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`device_type_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='This table is directly related to the previous one, as it contains the information about the type of devices that can be registered in the system.' AUTO_INCREMENT=1224 ;

--
-- Dumping data for table `device_type`
--

INSERT INTO `device_type` (`device_type_id`, `type`, `description`) VALUES
(1222, '1222', 'device'),
(1223, '1223', 'device');

-- --------------------------------------------------------

--
-- Table structure for table `dictionary`
--

DROP TABLE IF EXISTS `dictionary`;
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
-- Table structure for table `domains`
--

DROP TABLE IF EXISTS `domains`;
CREATE TABLE IF NOT EXISTS `domains` (
  `domain_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT 'Name of the domain (e.g. Offers)',
  `description` varchar(100) DEFAULT NULL COMMENT 'Domain description (e.g. Company domain used to store commercial offers to be presented to concrete customers. This kind of information is strictly confidential.)',
  `sensitivity_id` int(11) NOT NULL COMMENT 'Associated sensitivity level (strictly confidential, protected, public,...) FK to sensitivity table',
  PRIMARY KEY (`domain_id`),
  KEY `sensitivity_id_idx` (`sensitivity_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which describes the different domains that might apply for different company resources. Depending on this domain, it will have a different sensitivity level.' AUTO_INCREMENT=8 ;

--
-- Dumping data for table `domains`
--

INSERT INTO `domains` (`domain_id`, `name`, `description`, `sensitivity_id`) VALUES
(7, 'domain', 'desc', 25);

-- --------------------------------------------------------

--
-- Table structure for table `event_type`
--

DROP TABLE IF EXISTS `event_type`;
CREATE TABLE IF NOT EXISTS `event_type` (
  `event_type_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_type_key` varchar(200) NOT NULL COMMENT 'Possible values are: {USER_ACTION,SENSOR_CONTEXT,USER_FEEDBACK} as simple events and {DECISION,THREAT_CLUE,ADDITIONAL_PROTECTION,SECURITY_INCIDENT,DEVICE_POLICY_UPDATE} as complex events',
  `event_level` varchar(200) NOT NULL COMMENT 'Possible values are: SIMPLE_EVENT (corresponding to events that are generated by monitoring, without server processing) and COMPLEX_EVENT (events generated from the correlation or aggregation of other simple events)',
  PRIMARY KEY (`event_type_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which describes the possible types of events' AUTO_INCREMENT=39 ;

--
-- Dumping data for table `event_type`
--

INSERT INTO `event_type` (`event_type_id`, `event_type_key`, `event_level`) VALUES
(12, 'key', 'high'),
(13, 'key', 'high'),
(20, 'key', 'high'),
(21, 'key', 'high'),
(22, 'key', 'high'),
(23, 'key', 'high'),
(24, 'key', 'high'),
(25, 'key', 'high'),
(26, 'key', 'high'),
(27, 'key', 'high'),
(28, 'key', 'high'),
(29, 'key', 'high'),
(30, 'key', 'high'),
(31, 'key', 'high'),
(32, 'key', 'high'),
(33, 'key', 'high'),
(34, 'key', 'high'),
(35, 'key', 'high'),
(36, 'key', 'high'),
(37, 'key', 'high'),
(38, 'key', 'high');

-- --------------------------------------------------------

--
-- Table structure for table `legal_aspects`
--

DROP TABLE IF EXISTS `legal_aspects`;
CREATE TABLE IF NOT EXISTS `legal_aspects` (
  `description` varchar(50) NOT NULL,
  `KRS_hard_limit` int(10) unsigned NOT NULL DEFAULT '180' COMMENT 'Duration of data in days for the Knowledge Refinement System. Default=6 months',
  `RT2AE_hard_limit` int(10) unsigned NOT NULL DEFAULT '180' COMMENT 'Duration of data in days for the RT2AE. Default=6 months',
  `EP_hard_limit` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'Duration of data in days for the Event Processor. Default=6 months',
  `data_complete_erasure` binary(1) NOT NULL DEFAULT '1' COMMENT 'If ''1'' (TRUE) data will be completely removed from the database once the duration has expired.',
  PRIMARY KEY (`description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table containing data related with user''s privacy and legality in the system';

--
-- Dumping data for table `legal_aspects`
--

INSERT INTO `legal_aspects` (`description`, `KRS_hard_limit`, `RT2AE_hard_limit`, `EP_hard_limit`, `data_complete_erasure`) VALUES
('15', 20, 1, 2, '1');

-- --------------------------------------------------------

--
-- Table structure for table `muses_config`
--

DROP TABLE IF EXISTS `muses_config`;
CREATE TABLE IF NOT EXISTS `muses_config` (
  `config_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `config_name` varchar(30) NOT NULL COMMENT 'Name of the configuration',
  `access_attempts_before_blocking` int(10) unsigned NOT NULL DEFAULT '5',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='MUSES Server configuration parameters' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `outcome`
--

DROP TABLE IF EXISTS `outcome`;
CREATE TABLE IF NOT EXISTS `outcome` (
  `outcome_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `description` longtext,
  `costbenefit` double DEFAULT NULL,
  `threat_id` bigint(20) NOT NULL,
  PRIMARY KEY (`outcome_id`),
  KEY `threat_outcome_link` (`threat_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=27 ;

--
-- Dumping data for table `outcome`
--

INSERT INTO `outcome` (`outcome_id`, `description`, `costbenefit`, `threat_id`) VALUES
(1, 'test', 0, 2),
(2, 'test', 0, 2),
(5, 'test', 0, 2),
(6, 'test', 0, 2),
(7, 'test', 0, 2),
(8, 'test', 0, 2),
(9, 'test', 0, 2),
(10, '4134102403044', 0, 2),
(11, '3430013324002', 0, 2),
(12, 'outcome', 0, 19),
(13, 'outcome', 0, 20),
(14, 'outcome', 0, 21),
(15, 'outcome', 0, 22),
(16, 'Compromised Asset', -1000000, 23),
(17, 'outcome', 0, 2),
(18, 'outcome', 0, 2),
(19, 'outcome', 0, 24),
(20, 'Compromised Asset', -1000000, 25),
(21, 'outcome', 0, 2),
(22, 'outcome', 0, 26),
(23, 'outcome', 0, 26),
(24, 'outcome', 0, 26),
(25, 'outcome', 0, 26),
(26, 'outcome', 0, 26);

-- --------------------------------------------------------

--
-- Table structure for table `refined_security_rules`
--

DROP TABLE IF EXISTS `refined_security_rules`;
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

--
-- Dumping data for table `refined_security_rules`
--

INSERT INTO `refined_security_rules` (`refined_security_rules_id`, `original_security_rule_id`, `name`, `file`, `status`, `modification`) VALUES
(1, 800, 'name', NULL, 'VALIDATED', '2014-05-15 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `risk_communication`
--

DROP TABLE IF EXISTS `risk_communication`;
CREATE TABLE IF NOT EXISTS `risk_communication` (
  `risk_communication_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL COMMENT 'Textual description of the risk communication',
  PRIMARY KEY (`risk_communication_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=901 ;

--
-- Dumping data for table `risk_communication`
--

INSERT INTO `risk_communication` (`risk_communication_id`, `description`) VALUES
(900, 'desc');

-- --------------------------------------------------------

--
-- Table structure for table `risk_information`
--

DROP TABLE IF EXISTS `risk_information`;
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
-- Table structure for table `risk_policy`
--

DROP TABLE IF EXISTS `risk_policy`;
CREATE TABLE IF NOT EXISTS `risk_policy` (
  `risk_policy_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `riskvalue` double NOT NULL,
  `description` longtext NOT NULL,
  PRIMARY KEY (`risk_policy_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=45 ;

--
-- Dumping data for table `risk_policy`
--

INSERT INTO `risk_policy` (`risk_policy_id`, `riskvalue`, `description`) VALUES
(1, 0, 'myrsikpolicy'),
(2, 0, 'myrsikpolicy'),
(3, 0, 'myrsikpolicy'),
(4, 0, 'myrsikpolicy'),
(5, 0, 'myrsikpolicy'),
(6, 0, 'myrsikpolicy'),
(7, 0, 'myrsikpolicy'),
(8, 0, 'myrsikpolicy'),
(9, 0, 'myrsikpolicy'),
(10, 0, 'myrsikpolicy'),
(11, 0, 'myrsikpolicy'),
(12, 0, 'myrsikpolicy'),
(13, 0, 'myrsikpolicy'),
(14, 0, 'myrsikpolicy'),
(15, 0, 'myrsikpolicy'),
(16, 0, 'myrsikpolicy'),
(17, 0, 'myrsikpolicy'),
(18, 0, 'myrsikpolicy'),
(19, 0, 'myrsikpolicy'),
(20, 0, 'myrsikpolicy'),
(21, 0, 'myrsikpolicy'),
(22, 0, 'myrsikpolicy'),
(23, 0, 'myrsikpolicy'),
(24, 0, 'myrsikpolicy'),
(25, 0, 'myrsikpolicy'),
(26, 0, 'myrsikpolicy'),
(27, 0, 'myrsikpolicy'),
(28, 0, 'myrsikpolicy'),
(29, 0, 'myrsikpolicy'),
(30, 0, 'myrsikpolicy'),
(31, 0, 'myrsikpolicy'),
(32, 0, 'myrsikpolicy'),
(33, 0, 'myrsikpolicy'),
(34, 0, 'myrsikpolicy'),
(35, 0, 'myrsikpolicy'),
(36, 0, 'myrsikpolicy'),
(37, 0, 'myrsikpolicy'),
(38, 0, 'myrsikpolicy'),
(39, 0, 'myrsikpolicy'),
(40, 0, 'myrsikpolicy'),
(41, 0, 'myrsikpolicy'),
(42, 0, 'myrsikpolicy'),
(43, 0, 'myrsikpolicy'),
(44, 0, 'myrsikpolicy');

-- --------------------------------------------------------

--
-- Table structure for table `risk_treatment`
--

DROP TABLE IF EXISTS `risk_treatment`;
CREATE TABLE IF NOT EXISTS `risk_treatment` (
  `risk_treatment_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(50) NOT NULL COMMENT 'Description of risk treatment',
  `risk_communication_id` int(10) unsigned NOT NULL COMMENT 'FK to table RISK_COMMUNICATION(risk_communication_id)',
  PRIMARY KEY (`risk_treatment_id`),
  KEY `risk_treatment-risk_communication:risk_communication_id_idx` (`risk_communication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table will store all risk treatment computed by the RT2AE. All fields are defined in the table.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL COMMENT 'Role description',
  `security_level` smallint(6) DEFAULT NULL COMMENT 'Associated security level',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table which describes the role of the users inside the company, for example, if he is the CSO, the CTO, an accountant, a developer...' AUTO_INCREMENT=146 ;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`role_id`, `name`, `description`, `security_level`) VALUES
(145, 'role', 'desc', 1);

-- --------------------------------------------------------

--
-- Table structure for table `security_incident`
--

DROP TABLE IF EXISTS `security_incident`;
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
-- Table structure for table `security_rules`
--

DROP TABLE IF EXISTS `security_rules`;
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

--
-- Dumping data for table `security_rules`
--

INSERT INTO `security_rules` (`security_rule_id`, `name`, `description`, `file`, `status`, `refined`, `source_id`, `modification`) VALUES
(800, 'sec', 'des', NULL, 'VALIDATED', '0', 15, '2014-11-15 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `sensitivity`
--

DROP TABLE IF EXISTS `sensitivity`;
CREATE TABLE IF NOT EXISTS `sensitivity` (
  `sensitivity_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `level` smallint(6) NOT NULL COMMENT 'Associated numeric value corresponding to different levels of sensitivity from 1 for Strictly confidential, to 3 for public',
  PRIMARY KEY (`sensitivity_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='Table for listing all the possible values representing sensitivity of corporate data' AUTO_INCREMENT=26 ;

--
-- Dumping data for table `sensitivity`
--

INSERT INTO `sensitivity` (`sensitivity_id`, `name`, `level`) VALUES
(25, 'sensitivity', 1);

-- --------------------------------------------------------

--
-- Table structure for table `simple_events`
--

DROP TABLE IF EXISTS `simple_events`;
CREATE TABLE IF NOT EXISTS `simple_events` (
  `event_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_type_id` int(10) unsigned NOT NULL COMMENT 'Type of the event. This is a reference to the EVENT_TYPES table, whose possible values are: {USER_ACTION,SENSOR_CONTEXT,USER_FEEDBACK} as simple events',
  `user_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table USERS(user_id)',
  `device_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table DEVICES(device_id)',
  `app_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table APPLICATIONS(app_id)',
  `asset_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table ASSETS(asset_id)',
  `data` varchar(5000) NOT NULL COMMENT 'Raw event content (this is the content of the whole event in JSON format)',
  `date` date NOT NULL COMMENT 'Date when the event happens',
  `time` time NOT NULL COMMENT 'Time at when the event happens',
  `duration` int(11) DEFAULT NULL COMMENT 'Duration in milliseconds',
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT=' Table which describes the set of simple or primitive events in the MUSES system. Each event is paired with:' AUTO_INCREMENT=3 ;

--
-- Dumping data for table `simple_events`
--

INSERT INTO `simple_events` (`event_id`, `event_type_id`, `user_id`, `device_id`, `app_id`, `asset_id`, `data`, `date`, `time`, `duration`, `source_id`, `EP_can_access`, `RT2AE_can_access`, `KRS_can_access`) VALUES
(2, 13, 201, 202, 118, 1516, 'Some more', '2014-08-10', '16:59:48', 5, 16, 0, 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `sources`
--

DROP TABLE IF EXISTS `sources`;
CREATE TABLE IF NOT EXISTS `sources` (
  `source_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT 'Name of the source component that originates actions, events,...',
  PRIMARY KEY (`source_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=17 ;

--
-- Dumping data for table `sources`
--

INSERT INTO `sources` (`source_id`, `name`) VALUES
(15, 'sources'),
(16, 'sources');

-- --------------------------------------------------------

--
-- Table structure for table `system_log_krs`
--

DROP TABLE IF EXISTS `system_log_krs`;
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
-- Table structure for table `term_values`
--

DROP TABLE IF EXISTS `term_values`;
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
-- Table structure for table `threat`
--

DROP TABLE IF EXISTS `threat`;
CREATE TABLE IF NOT EXISTS `threat` (
  `threat_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `description` longtext NOT NULL,
  `probability` double NOT NULL,
  `occurences` int(11) DEFAULT NULL,
  `badOutcomeCount` int(11) DEFAULT NULL,
  `ttl` int(11) DEFAULT NULL,
  PRIMARY KEY (`threat_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=27 ;

--
-- Dumping data for table `threat`
--

INSERT INTO `threat` (`threat_id`, `description`, `probability`, `occurences`, `badOutcomeCount`, `ttl`) VALUES
(1, 'threat', 0, 0, 0, 0),
(2, 'test', 0, 0, 0, 0),
(3, 'test', 0, 0, 0, 0),
(4, 'test', 0, 0, 0, 0),
(5, 'test', 0, 0, 0, 0),
(6, 'test', 0, 0, 0, 0),
(7, 'test', 0, 0, 0, 0),
(8, 'test', 0, 0, 0, 0),
(9, 'test', 0, 0, 0, 0),
(10, 'test', 0, 0, 0, 0),
(11, 'test', 0, 0, 0, 0),
(12, 'test', 0, 0, 0, 0),
(13, 'test', 0, 0, 0, 0),
(14, 'test', 0, 0, 0, 0),
(15, 'test', 0, 0, 0, 0),
(16, 'test', 0, 0, 0, 0),
(17, 'test', 0, 0, 0, 0),
(18, '1210303434300', 0, 0, 0, 0),
(19, '413331332120', 0, 0, 0, 0),
(20, '1431442133311', 0, 0, 0, 0),
(21, '3404001341020', 0, 0, 0, 0),
(22, '3314213242300', 0, 0, 0, 0),
(23, 'Threateu.musesproject.server.risktrust.User@c225219null', 0.5, 1, 0, 0),
(24, 'test1', 0, 0, 0, 0),
(25, 'Threateu.musesproject.server.risktrust.User@1c2ba649null', 0.5, 1, 0, 0),
(26, 'test2', 0, 0, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `threat_clue`
--

DROP TABLE IF EXISTS `threat_clue`;
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
-- Table structure for table `threat_type`
--

DROP TABLE IF EXISTS `threat_type`;
CREATE TABLE IF NOT EXISTS `threat_type` (
  `threat_type_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL COMMENT 'Types of threat, such as WI-FI_SNIFFING, UNSECURE_NETWORK,MALWARE,SPYWARE,..',
  `description` varchar(100) NOT NULL COMMENT 'Description of the threat',
  PRIMARY KEY (`threat_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table is directly related to the RISK_INFORMATION table, as it contains the information about the type of threat.' AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL COMMENT 'First and middle names',
  `surname` varchar(50) NOT NULL,
  `email` varchar(50) DEFAULT NULL COMMENT 'user''s e-mail',
  `username` varchar(50) NOT NULL COMMENT 'The user name used to login',
  `password` varchar(50) NOT NULL COMMENT 'The user''s password',
  `enabled` int(11) NOT NULL COMMENT 'Specify whether the user''s account is active (1) or not (0)',
  `trust_value` double unsigned DEFAULT NULL COMMENT 'The trust value of the user will be between 0 and 1',
  `role_id` int(10) unsigned NOT NULL COMMENT 'FK to table ROLE(role_id)',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  KEY `role_id_idx` (`role_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='This table contains user information, similar to a profile. It has personal data (name, email) as well as company data (user''s role inside the company). Additionally, a trust value has been included for RT2AE calculation processes.' AUTO_INCREMENT=229 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `surname`, `email`, `username`, `password`, `enabled`, `trust_value`, `role_id`) VALUES
(1, 'admin', 'admin', 'admin@muses.com', 'muses', 'muses', 1, 200, 100),
(200, 'John', 'Doe', 'joe@email.com', 'joe', 'pass', 1, 200, 100),
(201, 'Joe', 'david', 'david@email.com', 'david', 'pass', 1, 200, 101),
(206, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', 'junior', 'walterwhite', 0, 0.9999, 0),
(207, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', 'hank', 'walterwhite', 0, 0.9999, 0),
(209, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '1032332241110', 'walterwhite', 0, 0.9999, 0),
(210, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '130341233403', 'walterwhite', 0, 0.9999, 0),
(211, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '1404023114313', 'walterwhite', 0, 0.9999, 0),
(212, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '3241113212132', 'walterwhite', 0, 0.9999, 0),
(213, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '2344114311100', 'walterwhite', 0, 0.9999, 0),
(214, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '3403203020234', 'walterwhite', 0, 0.9999, 0),
(215, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '1233411420410', 'walterwhite', 0, 0.9999, 0),
(216, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '102023032313', 'walterwhite', 0, 0.9999, 0),
(217, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '1113333143424', 'walterwhite', 0, 0.9999, 0),
(218, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '212002322402', 'walterwhite', 0, 0.9999, 0),
(219, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '314432010013', 'walterwhite', 0, 0.9999, 0),
(220, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '2033310120144', 'walterwhite', 0, 0.9999, 0),
(221, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '1332143110412', 'walterwhite', 0, 0.9999, 0),
(222, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '1414442402432', 'walterwhite', 0, 0.9999, 0),
(223, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '4102241231401', 'walterwhite', 0, 0.9999, 0),
(224, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '242410024210', 'walterwhite', 0, 0.9999, 0),
(225, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '342211231012', 'walterwhite', 0, 0.9999, 0),
(226, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '2422123334033', 'walterwhite', 0, 0.9999, 0),
(227, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '3124123131143', 'walterwhite', 0, 0.9999, 0),
(228, 'Pinkman', 'Jesse', 'jesse.pinkman@muses.eu', '3413223441224', 'walterwhite', 0, 0.9999, 0);

-- --------------------------------------------------------

--
-- Table structure for table `user_authorization`
--

DROP TABLE IF EXISTS `user_authorization`;
CREATE TABLE IF NOT EXISTS `user_authorization` (
  `user_authorization_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL COMMENT 'FK to table USERS(user_id)',
  `role_id` int(10) unsigned NOT NULL COMMENT 'FK to table ROLES(role_id)',
  PRIMARY KEY (`user_authorization_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=90 ;

--
-- Dumping data for table `user_authorization`
--

INSERT INTO `user_authorization` (`user_authorization_id`, `user_id`, `role_id`) VALUES
(89, 200, 145);

-- --------------------------------------------------------

--
-- Table structure for table `user_behaviour`
--

DROP TABLE IF EXISTS `user_behaviour`;
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

--
-- Constraints for dumped tables
--

--
-- Constraints for table `additional_protection`
--
ALTER TABLE `additional_protection`
  ADD CONSTRAINT `additional_protection-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `additional_protection-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `additional_protection-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `applications`
--
ALTER TABLE `applications`
  ADD CONSTRAINT `applications-app_type:app_type_id` FOREIGN KEY (`type`) REFERENCES `app_type` (`app_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `decision`
--
ALTER TABLE `decision`
  ADD CONSTRAINT `decision-access_request:access_request_id` FOREIGN KEY (`access_request_id`) REFERENCES `access_request` (`access_request_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `decision-risk_communication:risk_communication_id` FOREIGN KEY (`risk_communication_id`) REFERENCES `risk_communication` (`risk_communication_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `devices`
--
ALTER TABLE `devices`
  ADD CONSTRAINT `devices-device_type:device_type_id` FOREIGN KEY (`type`) REFERENCES `device_type` (`device_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `domains`
--
ALTER TABLE `domains`
  ADD CONSTRAINT `domains-sensitivity:sensitivity_id` FOREIGN KEY (`sensitivity_id`) REFERENCES `sensitivity` (`sensitivity_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `outcome`
--
ALTER TABLE `outcome`
  ADD CONSTRAINT `outcome_ibfk_1` FOREIGN KEY (`threat_id`) REFERENCES `threat` (`threat_id`);

--
-- Constraints for table `refined_security_rules`
--
ALTER TABLE `refined_security_rules`
  ADD CONSTRAINT `refined_security_rules-security_rules:security_rule_id` FOREIGN KEY (`original_security_rule_id`) REFERENCES `security_rules` (`security_rule_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `risk_information`
--
ALTER TABLE `risk_information`
  ADD CONSTRAINT `risk_information-assets` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`asset_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `risk_information-simple_events` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `risk_information-threat_type:threat_type_id` FOREIGN KEY (`threat_type`) REFERENCES `threat_type` (`threat_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `risk_treatment`
--
ALTER TABLE `risk_treatment`
  ADD CONSTRAINT `risk_treatment-risk_communication:risk_communication_id` FOREIGN KEY (`risk_communication_id`) REFERENCES `risk_communication` (`risk_communication_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `security_incident`
--
ALTER TABLE `security_incident`
  ADD CONSTRAINT `security_incident-decision:decision_id` FOREIGN KEY (`decision_id`) REFERENCES `decision` (`decision_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `security_incident-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `security_incident-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `security_incident-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `security_rules`
--
ALTER TABLE `security_rules`
  ADD CONSTRAINT `security_rules-sources:source_id` FOREIGN KEY (`source_id`) REFERENCES `sources` (`source_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `simple_events`
--
ALTER TABLE `simple_events`
  ADD CONSTRAINT `simple_events-applications:app_id` FOREIGN KEY (`app_id`) REFERENCES `applications` (`app_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-assets:asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`asset_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-event_type_event:type_id` FOREIGN KEY (`event_type_id`) REFERENCES `event_type` (`event_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-sources:source_id` FOREIGN KEY (`source_id`) REFERENCES `sources` (`source_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `simple_events-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `term_values`
--
ALTER TABLE `term_values`
  ADD CONSTRAINT `term_values-dictionary:term_id` FOREIGN KEY (`term_id`) REFERENCES `dictionary` (`term_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `threat_clue`
--
ALTER TABLE `threat_clue`
  ADD CONSTRAINT `threat_clue-acess_request:access_request_id` FOREIGN KEY (`access_request_id`) REFERENCES `access_request` (`access_request_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `threat_clue-assets:asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`asset_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `threat_clue-simple_events:event_id` FOREIGN KEY (`event_id`) REFERENCES `simple_events` (`event_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `threat_clue-threat_type:threat_type_id` FOREIGN KEY (`threat_type_id`) REFERENCES `threat_type` (`threat_type_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `threat_clue-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;

--
-- Constraints for table `user_behaviour`
--
ALTER TABLE `user_behaviour`
  ADD CONSTRAINT `user_behaviour-decision:decision_id` FOREIGN KEY (`decision_id`) REFERENCES `decision` (`decision_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `user_behaviour-devices:device_id` FOREIGN KEY (`device_id`) REFERENCES `devices` (`device_id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  ADD CONSTRAINT `user_behaviour-users:user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE CASCADE;
