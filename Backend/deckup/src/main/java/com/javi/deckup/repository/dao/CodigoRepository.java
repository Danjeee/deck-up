package com.javi.deckup.repository.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Codigo;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface CodigoRepository extends JpaRepository<Codigo, Integer>{

	@Query(value = "SELECT * FROM codigos WHERE codigo = ?1", nativeQuery = true)
	Optional<Codigo> findByCodigo(String code);

	@Query(value = "SELECT * FROM codigos c JOIN usuarios_codigos u ON c.id = u.id_codigo WHERE u.id_usuario = ?1 AND u.id_codigo = ?2", nativeQuery = true)
	Optional<Codigo> isUsedByUser(Long id, Integer code);

}
