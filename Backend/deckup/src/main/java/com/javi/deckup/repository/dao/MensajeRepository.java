package com.javi.deckup.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javi.deckup.model.dto.MensajeDTO;
import com.javi.deckup.repository.entity.Mensaje;

import java.util.Collection;
import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT m FROM Mensaje m WHERE (m.usuario.id = ?1 AND m.destino.id = ?2) OR (m.usuario.id = ?2 AND m.destino.id = ?1) ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findMensajesPrivados(Long usuarioId1,Long usuarioId2);

    @Query(value = "SELECT * FROM mensajes WHERE destino = ?1 AND usuario = ?2 AND leido != 1", nativeQuery = true)
	List<Mensaje> findAllUnreadedFrom(Long uId, Long fId);

    @Query(value = "SELECT * FROM mensajes WHERE destino = ?1 AND leido != 1", nativeQuery = true)
	List<Mensaje> findAllUnreadedFrom(Long usuarioId1);
}