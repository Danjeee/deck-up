package com.javi.deckup.repository.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.model.dto.UsuarioDTO;
import com.javi.deckup.repository.entity.Usuario;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	@Query(value="SELECT * FROM usuarios WHERE email = ?1", nativeQuery = true)
	Optional<Usuario> findByEmail(String email);

	@Modifying
	@Query(value="UPDATE usuarios SET auth = ?2	WHERE email = ?1", nativeQuery = true)
	void addVerificationCode(String mail, String authcode);

	@Query(value="SELECT * FROM usuarios WHERE username = ?1", nativeQuery = true)
	Optional<Usuario> findByUsername(String username);

	@Modifying
	@Query(value = "UPDATE usuarios SET next_payment = ?3 AND currency = ?2 WHERE id = ?1", nativeQuery = true)
	void pay(Long id, Integer total, Timestamp np);

	@Query(value = "SELECT * FROM usuarios WHERE auth = ?1", nativeQuery = true)
	Optional<Usuario> findByAuth(String auth);

	@Query(value = "SELECT * FROM usuarios u JOIN usuarios_codigos c ON u.id = c.id_usuario WHERE c.id_codigo = ?1 ", nativeQuery = true)
	List<Usuario> findByClaimedCode(Integer id);

	@Query(value = "SELECT username FROM usuarios WHERE id = ?1", nativeQuery = true)
	String findByUsername(Long id);

	@Modifying
	@Query(value = "UPDATE usuarios SET pfp = ?2 WHERE id = ?1", nativeQuery = true)
	void changePfp(Long id, String code);

	@Modifying
	@Query(value = "UPDATE usuarios SET currency = ?2 WHERE id = ?1", nativeQuery = true)
	void savemoney(Long id, Integer currency);

	@Modifying
	@Query(value = "UPDATE usuarios SET mazo = ?2 WHERE id = ?1", nativeQuery = true)
	void setMazo(Long userid, Long mazoid);

}
