package com.kanban.controller;

import com.kanban.dto.ColumnRequest;
import com.kanban.dto.ColumnResponse;
import com.kanban.model.KanbanColumn;
import com.kanban.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/columns")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ColumnController {
    
    @Autowired
    private ColumnService columnService;
    
    @GetMapping
    public List<ColumnResponse> getAllColumns() {
        return columnService.getAllColumns().stream()
            .map(col -> new ColumnResponse(
                col.getStatus(),
                col.getTitle(),
                col.getColor(),
                col.getIcon(),
                col.getOrderIndex()
            ))
            .collect(Collectors.toList());
    }
    
    @PostMapping
    public ResponseEntity<?> createColumn(@RequestBody ColumnRequest request) {
        try {
            KanbanColumn column = new KanbanColumn(
                request.getStatus(),
                request.getTitle(),
                request.getColor(),
                request.getIcon(),
                request.getOrderIndex()
            );
            KanbanColumn saved = columnService.createColumn(column);
            ColumnResponse response = new ColumnResponse(
                saved.getStatus(),
                saved.getTitle(),
                saved.getColor(),
                saved.getIcon(),
                saved.getOrderIndex()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{status}")
    public ResponseEntity<?> deleteColumn(@PathVariable String status) {
        try {
            columnService.deleteColumn(status);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/order")
    public ResponseEntity<?> updateColumnOrder(@RequestBody List<String> statuses) {
        try {
            columnService.updateColumnOrder(statuses);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}