package com.javi.deckup.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.PlayerStatus;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface PlayerStatusRepository extends JpaRepository<PlayerStatus, Long>{

}
