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
