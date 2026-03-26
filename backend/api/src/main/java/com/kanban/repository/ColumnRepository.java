package com.kanban.repository;

import com.kanban.model.KanbanColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ColumnRepository extends JpaRepository<KanbanColumn, Long> {
    List<KanbanColumn> findAllByOrderByOrderIndexAsc();
    Optional<KanbanColumn> findByStatus(String status);
    boolean existsByStatus(String status);
}