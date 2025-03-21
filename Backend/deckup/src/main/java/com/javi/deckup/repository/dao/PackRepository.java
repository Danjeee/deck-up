package com.javi.deckup.repository.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Paquete;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface PackRepository extends JpaRepository<Paquete, Integer>{
	
}
