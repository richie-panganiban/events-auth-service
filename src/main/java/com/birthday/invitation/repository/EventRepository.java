package com.birthday.invitation.repository;

import com.birthday.invitation.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByAdminIdOrderByEventDateDesc(UUID adminId);
}
