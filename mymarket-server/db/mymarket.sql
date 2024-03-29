-- APPLICATION TABLES
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `score`;
CREATE TABLE `score` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(255) NOT NULL,
  `score` int(11) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user`) REFERENCES `user`(`uid`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `state`;
CREATE TABLE `state` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `acronym` char(2) NOT NULL,
  `latitude` varchar(255) NOT NULL,
  `longitude` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `latitude` varchar(255) NOT NULL,
  `longitude` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`state`) REFERENCES state(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `place`;
CREATE TABLE `place` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `city` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `latitude` varchar(255) NOT NULL,
  `longitude` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`city`) REFERENCES city(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `market`;
CREATE TABLE `market` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `place` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `latitude` varchar(255) NOT NULL,
  `longitude` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`place`) REFERENCES place(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `product_barcode`;
CREATE TABLE `product_barcode` (
  `barcode` varchar(255) NOT NULL,
  `product` int(11) NOT NULL,
  PRIMARY KEY (`barcode`),
  FOREIGN KEY (`product`) REFERENCES product(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `market_product`;
CREATE TABLE `market_product` (
  `market` int(11) NOT NULL,
  `product` int(11) NOT NULL,
  `price` float NOT NULL,
  `offer` boolean NOT NULL DEFAULT false,
  `last_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`market`,`product`),
  FOREIGN KEY (`market`) REFERENCES market(`id`) ON UPDATE CASCADE,
  FOREIGN KEY (`product`) REFERENCES product(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `shopping_list`;
CREATE TABLE `shopping_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user`) REFERENCES `user`(`uid`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `shopping_list_product`;
CREATE TABLE `shopping_list_product` (
  `list` int(11) NOT NULL,
  `product` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`list`, `product`),
  FOREIGN KEY (`list`) REFERENCES shopping_list(`id`) ON UPDATE CASCADE,
  FOREIGN KEY (`product`) REFERENCES product(`id`) ON UPDATE CASCADE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `notification`;
CREATE TABLE IF NOT EXISTS `notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `kind` char(1) NOT NULL DEFAULT 'U' COMMENT 'S - Sempre (sempre que o usuário ligar o APP) U - Única (só será mostrada uma vez para o usuário) F - Mensagem Fatal',
  `app_version` varchar(255),
  `message` varchar(255) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` char(1) NOT NULL DEFAULT 'H' COMMENT 'H - Habilitada; D - Desabilitada',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1  DEFAULT CHARSET=latin1 ;

-- END OF APPLICATION TABLES

-- ACCOUNTING TABLES

DROP TABLE IF EXISTS `login_accounting`;
CREATE TABLE `login_accounting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'N' COMMENT 'Status = N - Novo, V - Visualizado, R - Rejeitado',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `market_product_accounting`;
CREATE TABLE `market_product_accounting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `market` int(11) NOT NULL,
  `product` int(11) NOT NULL,
  `price` float NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'N' COMMENT 'Status = N - Novo, V - Visualizado, R - Rejeitado',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`market`) REFERENCES market(`id`) ON UPDATE CASCADE,
  FOREIGN KEY (`product`) REFERENCES product(`id`) ON UPDATE CASCADE
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `search_accounting`;
CREATE TABLE `search_accounting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_barcode` varchar(255) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'N' COMMENT 'Status = N - Novo, V - Visualizado, R - Rejeitado',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- END OF ACCOUNTING TABLES

-- SUGGESTION TABLES

DROP TABLE IF EXISTS `market_suggestion`;
CREATE TABLE `market_suggestion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,  
  `name` varchar(255) NOT NULL,
  `place` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'N' COMMENT 'Status = N - Novo, A - Adicionado, R - Rejeitado',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `product_suggestion`;
CREATE TABLE `product_suggestion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `barcode` varchar(255) NOT NULL,
  `market` int(11),
  `price` float,
  `offer` boolean NOT NULL DEFAULT false,
  `status` char(1) NOT NULL DEFAULT 'N' COMMENT 'status = N - Novo, A -Adicionado, R - Rejeitado',  
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`market`) REFERENCES market(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `product_name_suggestion`;
CREATE TABLE `product_name_suggestion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product` int(11) NOT NULL,  
  `suggested_name` varchar(255) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'N' COMMENT 'status = N - Novo, A -Adicionado, R - Rejeitado',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`product`) REFERENCES product(`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ENF OF SUGGESTION TABLES

-- TRIGGERS

CREATE TRIGGER `TRG_ProductAccounting_Insert` AFTER INSERT ON `market_product`
FOR EACH ROW BEGIN         
	INSERT INTO market_product_accounting(market, product, price) VALUES (NEW.market, NEW.product,  NEW.price);
END

CREATE TRIGGER `TRG_ProductAccounting_Update` AFTER UPDATE ON `market_product`
FOR EACH ROW BEGIN         
	INSERT INTO market_product_accounting(market, product, price) VALUES (NEW.market, NEW.product,  NEW.price);
END

