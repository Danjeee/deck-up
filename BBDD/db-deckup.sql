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
  `vida` INT NULL,
  `mana` INT NOT NULL,
  `imagen` VARCHAR(100) NOT NULL,
  `precio` INT NOT NULL,
  `tipo` VARCHAR(10) NOT NULL DEFAULT 'card',
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
  `prnct_up` INT NULL,
  `prcnt_dwn` INT NULL,
  `crit` INT NULL,
  `crit_dmg` INT NULL,
  `especial` VARCHAR(1) NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `deckup`.`habilidades` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) NOT NULL,
  `especial` VARCHAR(1) NULL,
  `dmg` INT NULL DEFAULT 0,
  `heal` INT NULL DEFAULT 0,
  `freeze` INT NULL,
  `freeze_name` VARCHAR(50) NULL,
  `burn` INT NULL,
  `poisn` INT NULL,
  `bleed` INT NULL,
  `prcnt` INT NULL,
  `entorno` INT NULL,
  `load_atq` INT NULL,
  `crit` INT NULL,
  `crit_mult` INT NULL,
  `leth` INT NULL,
  `esq`	INT NULL,
  `prcnt_up` INT NULL,
  `prcnt_dwn` INT NULL,
  `color` VARCHAR(7) NULL DEFAULT "#000000",
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
  `leido` TINYINT NULL DEFAULT 0,
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
  `nombre` VARCHAR(50) NOT NULL,
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
    
/*GAME (tela)*/

CREATE TABLE `deckup`.`games` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `status` VARCHAR(45) NULL,
  `player1` BIGINT NULL,
  `player2` BIGINT NULL,
  `turno` INT NULL,
  `L1_1` BIGINT NULL,
  `L1_2` BIGINT NULL,
  `L1_3` BIGINT NULL,
  `L1_4` BIGINT NULL,
  `L1_5` BIGINT NULL,
  `L2_1` BIGINT NULL,
  `L2_2` BIGINT NULL,
  `L2_3` BIGINT NULL,
  `L2_4` BIGINT NULL,
  `L2_5` BIGINT NULL,
  `p1_c` TINYINT NULL,
  `p2_c` TINYINT NULL,
  PRIMARY KEY (`id`));
  
CREATE TABLE `deckup`.`lineas` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `carta` INT NOT NULL,
  `vida` INT NOT NULL,
  `stun` INT NULL,
  `stun_name` VARCHAR(60) NULL,
  `burn` INT NULL,
  `poisn` INT NULL,
  `bleed` INT NULL,
  `prnct_up` INT NULL DEFAULT 0,
  `prcnt_dwn` INT NULL DEFAULT 0,
  `willcrit` TINYINT NULL DEFAULT 0,
  `game` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_lineas_games_idx` (`game` ASC) VISIBLE,
  CONSTRAINT `fk_lineas_games`
    FOREIGN KEY (`game`)
    REFERENCES `deckup`.`games` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE ,
    INDEX `fk_lineas_card_idx` (`carta` ASC) VISIBLE,
  CONSTRAINT `fk_lineas_card`
    FOREIGN KEY (`carta`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
CREATE TABLE `deckup`.`player_status` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `usuario` BIGINT NOT NULL,
  `game` BIGINT NOT NULL,
  `vida` INT NOT NULL,
  `mana` INT NOT NULL,
  `carta1` INT NULL,
  `carta2` INT NULL,
  `carta3` INT NULL,
  `carta4` INT NULL,
  `carta5` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_game_status_idx` (`game` ASC) VISIBLE,
  INDEX `fk_card1_status_idx` (`carta1` ASC) VISIBLE,
  INDEX `fk_card2_status_idx` (`carta2` ASC) VISIBLE,
  INDEX `fk_card3_status_idx` (`carta3` ASC) VISIBLE,
  INDEX `fk_card4_status_idx` (`carta4` ASC) VISIBLE,
  INDEX `fk_card5_status_idx` (`carta5` ASC) VISIBLE,
  CONSTRAINT `fk_user_status`
    FOREIGN KEY (`usuario`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_game_status`
    FOREIGN KEY (`game`)
    REFERENCES `deckup`.`games` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_card1_status`
    FOREIGN KEY (`carta1`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_card2_status`
    FOREIGN KEY (`carta2`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_card3_status`
    FOREIGN KEY (`carta3`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_card4_status`
    FOREIGN KEY (`carta4`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_card5_status`
    FOREIGN KEY (`carta5`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
    /* Notif */
    CREATE TABLE `deckup`.`notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user` BIGINT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `msg` VARCHAR(255) NOT NULL,
  `currency` INT NULL,
  `card` INT NULL,
  `card_cant` INT NULL,
  `claimed` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `fk_user_notif_idx` (`user` ASC) VISIBLE,
  INDEX `fk_card_notif_idx` (`card` ASC) VISIBLE,
  CONSTRAINT `fk_user_notif`
    FOREIGN KEY (`user`)
    REFERENCES `deckup`.`usuarios` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_card_notif`
    FOREIGN KEY (`card`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
    
/*INSERCION DE DATOS*/
/*Usuarios y roles*/
INSERT INTO usuarios(username, email, password, pfp, currency, next_payment, estado, auth)VALUES 
('admin', 'a@a.com', '$2a$10$VVB7Xs4/w439uoJz5CSj/.Jasq7XsHwn9TQBngkD7YSiLHJ.B7yCm', 'admin.png', 999999999 ,now(), 1, '$2a$13$Y4MLieJRdenDKvQp9a5sae4lBqmoYovsaWGEkrlICZu8z2go0IOLW'),  # 1
('javisores', 'javisores@gmail.com','$2a$10$VVB7Xs4/w439uoJz5CSj/.Jasq7XsHwn9TQBngkD7YSiLHJ.B7yCm', 'javi.jpg', 500 , now(), 1, null); # 2

INSERT INTO roles(nombre, id_user)VALUES 
('ADMIN', 1),
('USER', 2);

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
('Puntapie', 'Golpea al rival, este pierde un turno', 1, null, 2, 'Noqueado', null), # 3
('Ignicion', 'Quema al rival', 1, null, null, null, 1), # 4
('Explosion de titan', 'Un gran golpe que tiene consecuencias', 5, 1, null, null, null); #5

INSERT INTO habilidades(nombre, descripcion, dmg, bleed, poisn, load_atq, heal) VALUES
('Destrucción impecable', 'Destruye todo a su alrededor a base de misiles', 10, 50, 50,1,0); #6

INSERT INTO habilidades(nombre, descripcion, dmg, bleed, crit, crit_mult) VALUES
('Masacre sangrienta', 'Asesinato multiple', 2, 1, 50, 100); #7

INSERT INTO habilidades(nombre, descripcion, dmg, bleed, poisn, load_atq, heal) VALUES
('Maldición del rey', 'Domina a tus rivales', 5, 1, 1,null,5), #8
('Mal imparable', 'Travesura tras travesura, no siempre salen bien...', 1, 2, 0,null,1), #9
('Curación inversa', 'Si es que... hace de todo', 3, null, null,null,3), #10
('Arrebato de ira', 'Nada mal para un principiante', 2, null, null,null,0); #11

INSERT INTO habilidades(nombre, descripcion, dmg, prcnt, heal) VALUES
('Big crush', 'El fin del universo', 1, 50, 2); #12

INSERT INTO habilidades(nombre, descripcion, dmg, freeze, freeze_name, heal, especial, bleed) VALUES
("Golpe mágico", "Acabas de ser golpeado mágicamente", 3, null, null, null, null, null), #13
("Cero", "brr brr que frio hace por aqui", 0, 1, "congelado", null, null, 1), #14
("Curación inesperada", "¡HP UP!", 0, null, null, 2, null, null), #15
("Desvanecimiento", "Desaparezco", 0, null, null, null, "D", null); #16


INSERT INTO cartas(nombre, descripcion, imagen, precio, rareza, paquete, habilidad, exclusive, vida, mana) VALUES
('AdminCard', 'Carta para los admins', 'admincard.png', 0, 6, null, 1, 1, 999, 0), # 1
('Minion', 'La unidad por defecto', 'minion.jpeg', 200, 1, 1, 2, 0, 1, 1), # 2
('Titan', 'Una enorme unidad con un gran poder pero muy poco veloz', 'titan.jpeg', 2000,3 ,1, 5, 0, 5, 5), # 3
('Eventio', 'Te damos la bienvenida', 'eventio.jpeg', 0,3 ,null, 3, 1, 4, 2), # 4
('Roboto', 'Alguna vez habias visto un robot tan... Singular?', 'roboto.jpg', 500,2 ,1, 4, 0, 4, 3), # 5
('X-7P', 'El peor enemigo del rey titán', 'x7p.jpeg', 4000,4 ,1, 6, 0, 7, 6), # 6
('Sheen', 'El fugaz asesino más brutal de los ultimos tiempos', 'sheen.jpeg', 2000,3 ,1,7, 0, 3, 3), # 7
('Skano', 'El rey demonio, una fiera sin igual', 'skano.jpeg', 10000,5 ,1, 8, 0, 5, 4), # 8
('Sekum', 'El menor de los dos, un principe demonio que no le teme a nada', 'sekum.jpeg', 4000,4 ,1, 9, 0, 4, 4), # 9
('Dallow', 'Que no te engañe su amigable aspecto, este ser es capaz de destruir planetas, pero... ¿A que es mono?', 'dallow.jpeg', 4000,4 ,1, 10, 0, 4, 4), # 10
('Haruki', 'Un samurai recién iniciado', 'haruki.jpeg', 500,2 ,1, 11, 0, 3, 4), # 11
('Dram', 'Este ser intergalactico es la preciosura mñas hermosa que un humano puede presenciar', 'dram.jpeg', 10000,5 ,1, 12, 0, 5, 5); # 12

/* Hechizos */
INSERT INTO cartas(nombre, descripcion, imagen, precio, rareza, paquete, habilidad, exclusive, mana, tipo) VALUES
('Misil magico', 'Un golpe duro que combina magia con impacto', 'misil_magico.jpeg', 200,1 ,1, 13, 0, 2, 'spell_all'), #13
('Punto cero', 'Tan frio como el universo mismo', 'punto_cero.jpeg', 2000,3 ,1, 14, 0, 3, 'spell'), #14
('Curacion inesperada', 'No es mucho pero oye, es barato', 'curacion_inesperada.jpeg', 500,2 ,1, 15, 0, 1, 'self_spell'), #15
('Evaporación', 'Eliminalos', 'evaporacion.jpeg', 200,1 ,1, 16, 0, 5, 'spell'); #16

/* Codigos */

INSERT INTO codigos(codigo, currency, card, card_cant, uses_left, expiration_date) VALUES
('GR4NDO0PENING', 2000, null, 0, 10000, null), # 1
('F1RST3V3NTCARD', 0, 4, 1, -1, '2026-03-26 12:34:56'); # 2

/* Relaciones */
INSERT INTO jugadores_cartas(id_jugador, id_carta, cant) VALUES
(1,1,20),
(1,2,1),
(1,3,1),
(1,4,1),
(1,5,1),
(1,6,1),
(1,7,1),
(1,8,1),
(1,9,1),
(1,10,1),
(1,11,1),
(1,12,1),
(1,13,1),
(1,14,1),
(1,14,1),
(1,15,1),
(1,16,1),
(2,2,1),
(2,3,1),
(2,4,1),
(2,5,1),
(2,6,1),
(2,7,1),
(2,8,1),
(2,9,1),
(2,10,1),
(2,11,1),
(2,12,1);

/*Tienda*/
INSERT INTO tienda VALUES(1,2,5,11,6,3,1,2,3);

/* Gemas */
INSERT INTO catalogo_gemas(precio, cant, nombre, imagen) VALUES
(0.99, 500, "Bolsa de gemas", "bolsa_gem.png"),
(2.99, 2000, "Caja de gemas", "caja_gem.png"),
(5.99, 5000, "Baul de gemas", "baul_gem.png"),
(9.99, 10000, "Cofre de gemas", "cofre_gem.png");

/* Amigos */
INSERT INTO amigos(usuario, amigo, accepted) VALUES
(2,1,0);