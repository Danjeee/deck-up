package com.javi.deckup.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Usuario;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	@Query(value="SELECT * FROM usuarios WHERE email = ?1", nativeQuery = true)
	Usuario findByEmail(String email);

}
