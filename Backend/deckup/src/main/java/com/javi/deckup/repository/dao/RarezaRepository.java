package com.javi.deckup.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Rareza;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface RarezaRepository extends JpaRepository<Rareza, Integer>{

}
