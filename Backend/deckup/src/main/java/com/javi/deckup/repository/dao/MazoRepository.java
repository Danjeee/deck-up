package com.javi.deckup.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Mazo;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface MazoRepository extends JpaRepository<Mazo, Long>{
	
	@Query(value = "SELECT * FROM mazos WHERE id_usuario = ?1", nativeQuery = true)
	List<Mazo> findAllByUser(Long id);

}
