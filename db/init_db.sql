USE `muses`;

-- Dumping data for table `app_type`
--

LOCK TABLES `app_type` WRITE;
/*!40000 ALTER TABLE `app_type` DISABLE KEYS */;
INSERT INTO `app_type` VALUES (1174,'1174','desc'),(1175,'1175','desc');
/*!40000 ALTER TABLE `app_type` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `applications` WRITE;
/*!40000 ALTER TABLE `applications` DISABLE KEYS */;
INSERT INTO `applications` VALUES (117,1174,'musesawaew','desc','89','2014-08-15 00:00:00','android',0),(118,1175,'musesawarew','desc','89','2014-08-15 00:00:00','android',0),(119,1175,'MUSES-Server','desc','89','2014-08-15 00:00:00','java',0);
/*!40000 ALTER TABLE `applications` ENABLE KEYS */;
UNLOCK TABLES;

INSERT INTO `connection_config` VALUES (1,5000,10000,60000,1,5);

LOCK TABLES `device_type` WRITE;
/*!40000 ALTER TABLE `device_type` DISABLE KEYS */;
INSERT INTO `device_type` VALUES (1222,'1222','device'),(1223,'1223','device');
/*!40000 ALTER TABLE `device_type` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `event_type` WRITE;
/*!40000 ALTER TABLE `event_type` DISABLE KEYS */;
INSERT INTO `event_type` VALUES (1,'LOG_IN','SIMPLE_EVENT'),(2,'LOG_OUT','SIMPLE_EVENT'),(3,'START','SIMPLE_EVENT'),(4,'RESUME','SIMPLE_EVENT'),(5,'STOP','SIMPLE_EVENT'),(6,'RESTART','SIMPLE_EVENT'),(7,'ACTION_REMOTE_FILE_ACCESS','SIMPLE_EVENT'),(8,'CONTEXT_SENSOR_CONNECTIVITY','SIMPLE_EVENT'),(9,'CONTEXT_SENSOR_DEVICE_PROTECTION','SIMPLE_EVENT'),(10,'ACTION_APP_OPEN','SIMPLE_EVENT'),(11,'ACTION_SEND_MAIL','SIMPLE_EVENT'),(12,'VIRUS_FOUND','SIMPLE_EVENT'),(13,'VIRUS_CLEANED','SIMPLE_EVENT'),(14,'SECURITY_PROPERTY_CHANGED','SIMPLE_EVENT'),(15,'SAVE_ASSET','SIMPLE_EVENT'),(16,'CONTEXT_SENSOR_PACKAGE','SIMPLE_EVENT'),(17,'SECURITY_VIOLATION','COMPLEX_EVENT'),(18,'SECURITY_INCIDENT','COMPLEX_EVENT'),(19,'CONFIGURATION_CHANGE','COMPLEX_EVENT'),(20,'DECISION','COMPLEX_EVENT'),(21,'DEVICE_POLICY_SENT','COMPLEX_EVENT'),(22,'CLUE_DETECTED','COMPLEX_EVENT'),(23,'CONTEXT_SENSOR_APP','SIMPLE_EVENT'),(24,'user_entered_password_field','SIMPLE_EVENT');
/*!40000 ALTER TABLE `event_type` ENABLE KEYS */;
UNLOCK TABLES;


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

INSERT INTO `muses_config` VALUES (1,'VERBOSE',0,3);

INSERT INTO `sensor_configuration` VALUES (1,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','avast! Mobile Security'),(2,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','Mobile Security & Antivirus'),(3,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','Avira Antivirus Security'),(4,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','Norton Security & Antivirus'),(5,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','CM Security & Find My Phone'),(6,'CONTEXT_SENSOR_DEVICE_PROTECTION','enabled','true'),(7,'CONTEXT_SENSOR_LOCATION','mindistance','10'),(8,'CONTEXT_SENSOR_LOCATION','mindtime','400'),(9,'CONTEXT_SENSOR_LOCATION','radius','12.0'),(10,'CONTEXT_SENSOR_LOCATION','enabled','true'),(11,'CONTEXT_SENSOR_FILEOBSERVER','path','/SWE/'),(12,'CONTEXT_SENSOR_FILEOBSERVER','enabled','true'),(13,'CONTEXT_SENSOR_APP','enabled','true'),(14,'CONTEXT_SENSOR_CONNECTIVITY','enabled','true'),(15,'CONTEXT_SENSOR_INTERACTION','enabled','true'),(16,'CONTEXT_SENSOR_PACKAGE','enabled','true'),(17,'CONTEXT_SENSOR_SETTINGS','enabled','true'),(18,'CONTEXT_SENSOR_NOTIFICATION','enabled','true');


LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin','admin@muses.com','muses','muses',1,0.5,100,'en');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;


INSERT INTO `assets` VALUES (1,'Patent','Asset_Unige',0,'PUBLIC','Geneva',now()),
(1694, 'UNIGEPatent', 'The patent describe how to select the best Wi-Fi network', 100000, 'CONFIDENTIAL', 'unige/patent/list/UNIGEPatent', NULL),
(1695, 'S2Patent', 'The patent describe how to prevent any threat in wireless network', 200000, 'STRICTLY_CONFIDENTIAL', 's2/patent/list/S2Patent', NULL),
(1696, 'SWEDENPatent', 'The patent is about the RFID technology', 10000, 'INTERNAL', 'sweden/patent/list/SWEDENPatent', NULL);

