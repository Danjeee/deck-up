package com.javi.deckup.repository.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Carta;
import com.javi.deckup.repository.entity.Usuario;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface CartaRepository extends JpaRepository<Carta, Integer> {

	@Query("SELECT c FROM Carta c WHERE c.rareza.id = 1 AND c.exclusive = false ORDER BY RAND()")
	List<Carta> findCommon();
	
	@Query("SELECT c FROM Carta c WHERE c.rareza.id in (1,2) AND c.exclusive = false AND c.id NOT IN :ids ORDER BY RAND()")
	List<Carta> findCommonOrRare(@Param("ids") List<Integer> bannedIds);
	
	@Query("SELECT c FROM Carta c WHERE c.rareza.id in (2,3) AND c.exclusive = false AND c.id NOT IN :ids ORDER BY RAND()")
	List<Carta> findRareOrEpic(@Param("ids") List<Integer> bannedIds);
	
	@Query("SELECT c FROM Carta c WHERE c.rareza.id in (3,4) AND c.exclusive = false AND c.id NOT IN :ids ORDER BY RAND()")
	List<Carta> findEpicOrLeg(@Param("ids") List<Integer> bannedIds);

	@Query(value= "SELECT c.* FROM cartas c WHERE c.id = ?1 AND EXISTS (SELECT m.* FROM mazos m WHERE m.id_usuario = ?2 AND ?1 IN (m.carta1, m.carta2, m.carta3, m.carta4, m.carta5, m.carta6, m.carta7, m.carta8))",nativeQuery = true)
	Optional<Carta> findOnAnyDeck(Integer idcarta, Long id);

}
