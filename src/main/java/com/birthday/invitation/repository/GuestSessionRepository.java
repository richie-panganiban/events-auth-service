package com.birthday.invitation.repository;

import com.birthday.invitation.entity.GuestSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuestSessionRepository extends JpaRepository<GuestSession, UUID> {

    Optional<GuestSession> findBySessionToken(String sessionToken);

    Optional<GuestSession> findBySessionTokenAndIsRevokedFalse(String sessionToken);

    @Query("SELECT s FROM GuestSession s JOIN FETCH s.guest g JOIN FETCH g.event WHERE s.sessionToken = :token AND s.isRevoked = false")
    Optional<GuestSession> findBySessionTokenWithGuestAndEvent(@Param("token") String token);

    @Modifying
    @Query("UPDATE GuestSession s SET s.isRevoked = true WHERE s.guest.id = :guestId")
    void revokeAllByGuestId(@Param("guestId") UUID guestId);
}
