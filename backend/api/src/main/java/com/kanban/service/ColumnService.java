package com.kanban.service;

import com.kanban.model.KanbanColumn;
import com.kanban.model.Task;
import com.kanban.repository.ColumnRepository;
import com.kanban.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ColumnService {

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<KanbanColumn> getAllColumns() {
        return columnRepository.findAllByOrderByOrderIndexAsc();
    }

    public KanbanColumn createColumn(KanbanColumn column) {
        if (columnRepository.existsByStatus(column.getStatus())) {
            throw new IllegalArgumentException("Já existe uma coluna com este status: " + column.getStatus());
        }
        return columnRepository.save(column);
    }

    @Transactional
    public void deleteColumn(String status) {
        List<String> defaultStatuses = List.of("TO_DO", "IN_PROGRESS", "DONE");
        if (defaultStatuses.contains(status)) {
            throw new IllegalArgumentException("Não é possível deletar colunas padrão");
        }
        
        List<Task> tasksInColumn = taskRepository.findByEstado(status);
        for (Task task : tasksInColumn) {
            task.setEstado("TO_DO");
            taskRepository.save(task);
        }
        
        KanbanColumn column = columnRepository.findByStatus(status)
            .orElseThrow(() -> new IllegalArgumentException("Coluna não encontrada: " + status));
        columnRepository.delete(column);
        
        List<KanbanColumn> remainingColumns = columnRepository.findAllByOrderByOrderIndexAsc();
        for (int i = 0; i < remainingColumns.size(); i++) {
            remainingColumns.get(i).setOrderIndex(i);
            columnRepository.save(remainingColumns.get(i));
        }
    }
    
    public void updateColumnOrder(List<String> statuses) {
        for (int i = 0; i < statuses.size(); i++) {
            String status = statuses.get(i);
            KanbanColumn column = columnRepository.findByStatus(status)
                .orElseThrow(() -> new IllegalArgumentException("Coluna não encontrada: " + status));
            column.setOrderIndex(i);
            columnRepository.save(column);
        }
    }
    
    public void initializeDefaultColumns() {
        if (columnRepository.count() == 0) {
            createColumn(new KanbanColumn("TO_DO", "To Do", "#3b82f6", "fas fa-list-ul", 0));
            createColumn(new KanbanColumn("IN_PROGRESS", "In Progress", "#f59e0b", "fas fa-spinner", 1));
            createColumn(new KanbanColumn("DONE", "Done", "#10b981", "fas fa-check-circle", 2));
        }
    }
}