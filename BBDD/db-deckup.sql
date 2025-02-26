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
  `nextPayment` DATETIME NOT NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `deckup`.`cartas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `descripcion` VARCHAR(255) NOT NULL,
  `imagen` VARCHAR(100) NOT NULL,
  `precio` INT NOT NULL,
  `vida` INT NOT NULL,
  `dmg` INT NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `deckup`.`rarezas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  `porcentaje` INT NOT NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `deckup`.`paquetes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  `descripcion` VARCHAR(255) NOT NULL,
  `imagen` VARCHAR(100) NOT NULL,
  `precio` INT NOT NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `deckup`.`habilidades` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) NOT NULL,
  `tipo` VARCHAR(1) NOT NULL,
  `dmg` INT NOT NULL,
  PRIMARY KEY (`id`));
  
/* Relaciones */

ALTER TABLE `deckup`.`cartas` 
ADD COLUMN `rareza` INT NOT NULL AFTER `precio`,
ADD COLUMN `paquete` INT NOT NULL AFTER `rareza`,
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

CREATE TABLE `deckup`.`jugadores-cartas` (
  `id` BIGINT NOT NULL,
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
    
    CREATE TABLE `deckup`.`cartas_habilidades` (
  `id_carta` INT NOT NULL,
  `id_habilidad` INT NOT NULL,
  PRIMARY KEY (`id_carta`, `id_habilidad`),
  INDEX `fk_hab_nan1_idx` (`id_habilidad` ASC) VISIBLE,
  CONSTRAINT `fk_carta_nan2`
    FOREIGN KEY (`id_carta`)
    REFERENCES `deckup`.`cartas` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_hab_nan1`
    FOREIGN KEY (`id_habilidad`)
    REFERENCES `deckup`.`habilidades` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

/* Especial: Tienda */

CREATE TABLE `deckup`.`tienda` (
  `carta1` INT NOT NULL,
  `carta2` INT NOT NULL,
  `carta3` INT NOT NULL,
  `carta4` INT NOT NULL,
  `carta5` INT NOT NULL,
  `paq1` INT NOT NULL,
  `paq2` INT NOT NULL,
  `paq3` INT NOT NULL,
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
