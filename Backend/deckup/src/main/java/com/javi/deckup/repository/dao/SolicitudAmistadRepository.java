package com.javi.deckup.repository.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.model.dto.SolicitudAmistadDTO;
import com.javi.deckup.repository.entity.SolicitudAmistad;
import com.javi.deckup.repository.entity.Usuario;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface SolicitudAmistadRepository extends JpaRepository<SolicitudAmistad, Long>{

	@Query(value = "SELECT * FROM amigos WHERE usuario = ?1 AND accepted = 1", nativeQuery = true)
	List<SolicitudAmistad> findAllAccepted(Long id);
	
	@Query(value = "SELECT * FROM amigos WHERE usuario = ?1 AND accepted = 0", nativeQuery = true)
	List<SolicitudAmistad> findAllUnaccepted(Long id);

	@Query(value = "SELECT * FROM amigos WHERE usuario = ?1 AND amigo = ?2", nativeQuery = true)
	Optional<SolicitudAmistad> findByUserAndFriend(Long id, Long id2);

}
