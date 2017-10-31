CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `message_date` datetime DEFAULT NULL,
  `received_by` varchar(255) DEFAULT NULL,
  `received_date` datetime DEFAULT NULL,
  `recep_name` varchar(255) DEFAULT NULL,
  `team_name` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `urgent` tinyint(1) NOT NULL DEFAULT '0',
  `claimed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
)

CREATE TABLE `tokens` (
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`token`)
)