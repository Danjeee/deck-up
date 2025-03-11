package com.javi.deckup.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Tienda;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface TiendaRepository extends JpaRepository<Tienda, Integer>{

}
