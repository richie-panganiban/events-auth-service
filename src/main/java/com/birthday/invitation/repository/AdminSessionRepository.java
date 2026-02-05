package com.birthday.invitation.repository;

import com.birthday.invitation.entity.AdminSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminSessionRepository extends JpaRepository<AdminSession, UUID> {

    Optional<AdminSession> findBySessionToken(String sessionToken);

    @Query("SELECT s FROM AdminSession s JOIN FETCH s.admin WHERE s.sessionToken = :token AND s.isRevoked = false")
    Optional<AdminSession> findBySessionTokenAndIsRevokedFalse(@Param("token") String token);

    @Modifying
    @Query("UPDATE AdminSession s SET s.isRevoked = true WHERE s.admin.id = :adminId")
    void revokeAllByAdminId(@Param("adminId") UUID adminId);
}
