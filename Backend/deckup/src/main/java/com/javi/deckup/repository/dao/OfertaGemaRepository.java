package com.javi.deckup.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.OfertaGema;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface OfertaGemaRepository extends JpaRepository<OfertaGema, Integer>{

}
