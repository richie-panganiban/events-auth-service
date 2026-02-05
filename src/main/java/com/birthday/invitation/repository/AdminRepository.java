package com.birthday.invitation.repository;

import com.birthday.invitation.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByMagicLinkToken(String magicLinkToken);

    boolean existsByEmail(String email);
}
