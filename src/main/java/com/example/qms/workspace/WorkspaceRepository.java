package com.example.qms.workspace;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findAllByUserId(long userId);
    int countByUserId(long userId);

    @Query("SELECT userId FROM Workspace WHERE id = :id")
    Optional<Long> findUserIdById(Long id);
}