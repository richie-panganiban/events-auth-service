package com.birthday.invitation.repository;

import com.birthday.invitation.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {

    Optional<Guest> findByMagicLinkToken(String magicLinkToken);

    @Query("SELECT g FROM Guest g JOIN FETCH g.event WHERE g.magicLinkToken = :token")
    Optional<Guest> findByMagicLinkTokenWithEvent(@Param("token") String token);

    List<Guest> findByEventIdOrderByCreatedAtDesc(UUID eventId);

    @Query("SELECT g FROM Guest g WHERE g.event.id = :eventId AND g.isActive = true ORDER BY g.createdAt DESC")
    List<Guest> findActiveByEventId(@Param("eventId") UUID eventId);
}
