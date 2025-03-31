DROP DATABASE IF EXISTS `deckup`;
CREATE DATABASE `deckup`;
USE deckup;

/* Creación de tablas */

CREATE TABLE `deckup`.`usuarios` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `pfp` VARCHAR(100) NOT NULL DEFAULT "user.png",
  `currency` INT NOT NULL,
  `next_payment` DATETIME NOT NULL,
  `estado` TINYINT NOT NULL DEFAULT 1,
  `auth` VARCHAR(255) NULL,
  `mazo` BIGINT NULL,
  `notis` TINYINT NOT NULL DEFAULT 1,
  `last_login` TIMESTAMP NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `deckup`.`cartas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `descripcion` VARCHAR(255) NOT NULL,
  `imagen` VARCHAR(100) NOT NULL,
  `precio` INT NOT NULL,
  `habilidad` INT NOT NULL DEFAULT 1,
  `exclusive` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`));

CREATE TABLE `deckup`.`rarezas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  `porcentaje` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `deckup`.`paquetes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  `descripcion` VARCHAR(255) NOT NULL,
  `imagen` VARCHAR(100) NOT NULL,
  `precio` INT NOT NULL,
  `cant` INT NOT NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `deckup`.`entornos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `descripcion` VARCHAR(255) NOT NULL,
  `dmg` INT NULL,
  `heal` INT NULL,
  `prnct_up` DECIMAL(3,2) NULL,
  `prcnt_dwn` DECIMAL(3,2) NULL,
  `crit` DECIMAL(3,2) NULL,
  `crit_dmg` DECIMAL(3,2) NULL,
  `especial` VARCHAR(1) NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `deckup`.`habilidades` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) NOT NULL,
  `especial` VARCHAR(1) NULL,
  `dmg` INT NULL,
  `heal` INT NULL,
  `freeze` INT NULL,
  `freeze_name` VARCHAR(50) NULL,
  `burn` INT NULL,
  `poisn` INT NULL,
  `bleed` INT NULL,
  `prcnt` DECIMAL(3,2) NULL,
  `entorno` INT NULL,
  `load_atq` INT NULL,
  `crit` DECIMAL (3,2) NULL,
  `crit_mult` DECIMAL (3,2) NULL,
  `leth` DECIMAL (3,2) NULL,
  `esq`	DECIMAL (3,2) NULL,
  `prcnt_up` DECIMAL (3,2) NULL,
  `prcnt_dwn` DECIMAL (3,2) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_hab_ent_idx` (`entorno` ASC) VISIBLE,
  CONSTRAINT `fk_hab_ent`
    FOREIGN KEY (`entorno`)
    REFERENCES `deckup`.`entornos` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
  
  CREATE TABLE `deckup`.`roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `id_user` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_users_roles_idx` (`id_user` ASC) VISIBLE,
  CONSTRAINT `fk_users_roles`
    FOREIGN KEY (`id_user`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
    CREATE TABLE `deckup`.`codigos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `codigo` VARCHAR(45) NOT NULL,
  `currency` INT NULL DEFAULT 0,
  `card` INT NULL,
  `card_cant` INT NULL DEFAULT 0,
  `uses_left` INT NULL DEFAULT -1,
  `expiration_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_card-code_idx` (`card` ASC) VISIBLE,
  CONSTRAINT `fk_card-code`
    FOREIGN KEY (`card`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
CREATE TABLE `deckup`.`catalogo_gemas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `precio` DECIMAL(10,2) NOT NULL,
  `cant` INT NOT NULL,
  `nombre` VARCHAR(45) NOT NULL,
  `imagen` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`));
  
CREATE TABLE `deckup`.`mensajes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `contenido` VARCHAR(255) NOT NULL,
  `usuario` BIGINT NOT NULL,
  `destino` BIGINT NOT NULL,
  `fecha_envio` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user_msgs_idx` (`usuario` ASC) VISIBLE,
  INDEX `fk_destino_msgs_idx` (`destino` ASC) VISIBLE,
  CONSTRAINT `fk_user_msgs`
    FOREIGN KEY (`usuario`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_destino_msgs`
    FOREIGN KEY (`destino`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

  
/* Relaciones */

ALTER TABLE `deckup`.`cartas` 
ADD COLUMN `rareza` INT NOT NULL AFTER `precio`,
ADD COLUMN `paquete` INT NULL AFTER `rareza`,
ADD INDEX `fk_cartas-paq_idx` (`paquete` ASC) VISIBLE,
ADD INDEX `fk_cartas-rar_idx` (`rareza` ASC) VISIBLE;
;
ALTER TABLE `deckup`.`cartas` 
ADD CONSTRAINT `fk_cartas-paq`
  FOREIGN KEY (`paquete`)
  REFERENCES `deckup`.`paquetes` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT `fk_cartas-rar`
  FOREIGN KEY (`rareza`)
  REFERENCES `deckup`.`rarezas` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

/* Relaciones N a N (Tablas + fk) */

CREATE TABLE `deckup`.`jugadores_cartas` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `id_jugador` BIGINT NOT NULL,
  `id_carta` INT NOT NULL,
  `cant` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_cartas_nan1_idx` (`id_carta` ASC) VISIBLE,
  CONSTRAINT `fk_jugadores_nan1`
    FOREIGN KEY (`id_jugador`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cartas_nan1`
    FOREIGN KEY (`id_carta`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
    
    CREATE TABLE `deckup`.`usuarios_codigos` (
  `id_usuario` BIGINT NOT NULL AUTO_INCREMENT,
  `id_codigo` INT NOT NULL,
  PRIMARY KEY (`id_usuario`, `id_codigo`),
  INDEX `fk_codes_idx` (`id_codigo` ASC) VISIBLE,
  CONSTRAINT `fk_useer-codes`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_codes`
    FOREIGN KEY (`id_codigo`)
    REFERENCES `deckup`.`codigos` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

/* Especial: Tienda */

CREATE TABLE `deckup`.`tienda` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `carta1` INT NOT NULL,
  `carta2` INT NOT NULL,
  `carta3` INT NOT NULL,
  `carta4` INT NOT NULL,
  `carta5` INT NOT NULL,
  `paq1` INT NOT NULL,
  `paq2` INT NULL,
  `paq3` INT NULL,
  INDEX `fk_carta1_idx` (`carta1` ASC) VISIBLE,
  INDEX `fk_carta2_idx` (`carta2` ASC) VISIBLE,
  INDEX `fk_carta3_idx` (`carta3` ASC) VISIBLE,
  INDEX `fk_carta4_idx` (`carta4` ASC) VISIBLE,
  INDEX `fk_carta5_idx` (`carta5` ASC) VISIBLE,
  INDEX `fk_paq1_idx` (`paq1` ASC) VISIBLE,
  INDEX `fk_paq2_idx` (`paq2` ASC) VISIBLE,
  INDEX `fk_paq3_idx` (`paq3` ASC) VISIBLE,
  CONSTRAINT `fk_carta1`
    FOREIGN KEY (`carta1`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_carta2`
    FOREIGN KEY (`carta2`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_carta3`
    FOREIGN KEY (`carta3`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_carta4`
    FOREIGN KEY (`carta4`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_carta5`
    FOREIGN KEY (`carta5`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_paq1`
    FOREIGN KEY (`paq1`)
    REFERENCES `deckup`.`paquetes` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_paq2`
    FOREIGN KEY (`paq2`)
    REFERENCES `deckup`.`paquetes` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_paq3`
    FOREIGN KEY (`paq3`)
    REFERENCES `deckup`.`paquetes` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
/* Especial: Paypal */
CREATE TABLE `deckup`.`payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `orderid` VARCHAR(255) NOT NULL,
  `status` VARCHAR(50) NOT NULL,
  `id_user` BIGINT NOT NULL,
  `claimed` TINYINT NOT NULL,
  `cant` INT NOT NULL,
  `time_created` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_userpayments_idx` (`id_user` ASC) VISIBLE,
  CONSTRAINT `fk_userpayments`
    FOREIGN KEY (`id_user`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
/* Especial: Mazos */

CREATE TABLE `deckup`.`mazos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `carta1` INT NOT NULL,
  `carta2` INT NOT NULL,
  `carta3` INT NOT NULL,
  `carta4` INT NOT NULL,
  `carta5` INT NOT NULL,
  `carta6` INT NOT NULL,
  `carta7` INT NOT NULL,
  `carta8` INT NOT NULL,
  `id_usuario` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_mazo_c1_idx` (`carta1` ASC) VISIBLE,
  INDEX `fk_mazo_c2_idx` (`carta2` ASC) VISIBLE,
  INDEX `fk_mazo_c3_idx` (`carta3` ASC) VISIBLE,
  INDEX `fk_mazo_c4_idx` (`carta4` ASC) VISIBLE,
  INDEX `fk_mazo_c5_idx` (`carta5` ASC) VISIBLE,
  INDEX `fk_mazo_c6_idx` (`carta6` ASC) VISIBLE,
  INDEX `fk_mazo_c7_idx` (`carta7` ASC) VISIBLE,
  INDEX `fk_mazo_c8_idx` (`carta8` ASC) VISIBLE,
  INDEX `fk_mazo_usr_idx` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_mazo_c1`
    FOREIGN KEY (`carta1`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_mazo_c2`
    FOREIGN KEY (`carta2`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_mazo_c3`
    FOREIGN KEY (`carta3`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_mazo_c4`
    FOREIGN KEY (`carta4`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_mazo_c5`
    FOREIGN KEY (`carta5`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_mazo_c6`
    FOREIGN KEY (`carta6`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_mazo_c7`
    FOREIGN KEY (`carta7`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_mazo_c8`
    FOREIGN KEY (`carta8`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_mazo_usr`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

/* Especial: Amigos*/
    
CREATE TABLE `deckup`.`amigos` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `usuario` BIGINT NOT NULL,
  `amigo` BIGINT NOT NULL,
  `accepted` TINYINT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `fk_amigos_1_idx` (`usuario` ASC) VISIBLE,
  INDEX `fk_amigos_2_idx` (`amigo` ASC) VISIBLE,
  CONSTRAINT `fk_amigos_1`
    FOREIGN KEY (`usuario`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_amigos_2`
    FOREIGN KEY (`amigo`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
/*INSERCION DE DATOS*/
/*Usuarios y roles*/
INSERT INTO usuarios(username, email, password, pfp, currency, next_payment, estado, auth)VALUES 
('admin', 'a@a.com', '$2a$10$VVB7Xs4/w439uoJz5CSj/.Jasq7XsHwn9TQBngkD7YSiLHJ.B7yCm', 'admin.png', 999999999 ,now(), 1, '$2a$13$Y4MLieJRdenDKvQp9a5sae4lBqmoYovsaWGEkrlICZu8z2go0IOLW'),  # 1
('javisores', 'javisores@gmail.com','$2a$10$VVB7Xs4/w439uoJz5CSj/.Jasq7XsHwn9TQBngkD7YSiLHJ.B7yCm', 'javi.jpg', 500 , now(), 1, null); # 2

INSERT INTO roles(nombre, id_user)VALUES 
('ROLE_ADMIN', 1),
('ROLE_USER', 2);

/*Habilidades, rarezas, paquetes y cartas*/

INSERT INTO paquetes(nombre, descripcion, imagen, precio, cant) VALUES
('Básico', 'Paquete normal', 'paquete1.png', 500, 6), # 1
('Avanzado', 'Paquete avanzado', 'paquete2.png', 1500, 6), # 1
('Mágico', 'Paquete mágico', 'paquete3.png', 2500, 8); # 1

INSERT INTO rarezas(nombre, porcentaje) VALUES
('Comun', 40), # 1
('Rara', 20), # 2 
('Epica', 10), # 3
('Legendaria', 3), # 4
('Mitica', 0.5), # 5
('???', 0.01); # 6

INSERT INTO habilidades(nombre, descripcion, dmg, load_atq, freeze, freeze_name, burn) VALUES
('Admin', 'Oponente.setVida(0)',  999999, null, null, null, null), # 1
('Golpe', 'Golpea al rival', 1, null, null, null, null), # 2
('Puntapie', 'Golpea al rival, este pierde un turno', 1, null, 1, 'Noqueado', null), # 3
('Ignicion', 'Quema al rival', 0, null, null, null, 1), # 4
('Explosion de titan', 'Un gran golpe que tiene consecuencias', 5, 1, null, null, null); #5


INSERT INTO cartas(nombre, descripcion, imagen, precio, rareza, paquete, habilidad, exclusive) VALUES
('AdminCard', 'Carta para los admins', 'admincard.png', 0, 6, null, 1, 1), # 1
('Minion', 'La unidad por defecto', 'minion.jpeg', 200, 1, 1, 2, 0), # 2
('Titan', 'Una enorme unidad con un gran poder pero muy poco veloz', 'titan.jpeg', 2000,3 ,1, 5, 0), # 3
('Eventio', 'Te damos la bienvenida', 'eventio.jpeg', 0,3 ,null, 3, 1), # 4
('Roboto', 'Alguna vez habias visto un robot tan... Singular?', 'roboto.jpg', 500,2 ,1, 4, 0); # 5

/* Codigos */

INSERT INTO codigos(codigo, currency, card, card_cant, uses_left, expiration_date) VALUES
('GR4NDO0PENING', 2000, null, 0, 10000, null), # 1
('F1RST3V3NTCARD', 0, 4, 1, -1, '2026-03-26 12:34:56'); # 2

/* Relaciones */
INSERT INTO jugadores_cartas(id_jugador, id_carta, cant) VALUES
(1,1,20),
(1,2,1),
(2,2,1),
(2,3,1);

/*Tienda*/
INSERT INTO tienda VALUES(1,2,2,5,3,3,1,2,3);

/* Gemas */
INSERT INTO catalogo_gemas(precio, cant, nombre, imagen) VALUES
(0.99, 500, "Bolsa de gemas", "bolsa_gem.png"),
(2.99, 2000, "Caja de gemas", "caja_gem.png"),
(5.99, 5000, "Baul de gemas", "baul_gem.png"),
(9.99, 10000, "Cofre de gemas", "cofre_gem.png");

/* Amigos */
INSERT INTO amigos(usuario, amigo, accepted) VALUES
(1,2,0),
(2,1,0);