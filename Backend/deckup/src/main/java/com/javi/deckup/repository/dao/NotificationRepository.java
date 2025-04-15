package com.javi.deckup.repository.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.javi.deckup.repository.entity.Notification;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface NotificationRepository extends JpaRepository<Notification, Long>{

	@Query(value = "SELECT * FROM notifications WHERE user = ?1 AND claimed != 1", nativeQuery = true)
	List<Notification> findAllUnreaded(Long idPlayer);

	@Modifying
	@Query(value = "UPDATE notifications SET claimed = 1 WHERE user = ?1", nativeQuery = true)
	void readAll(Long id);
	
}
