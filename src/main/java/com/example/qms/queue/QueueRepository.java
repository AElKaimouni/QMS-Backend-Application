package com.example.qms.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QueueRepository extends JpaRepository<Queue, UUID> {
    // Custom query methods can be added here if needed
}