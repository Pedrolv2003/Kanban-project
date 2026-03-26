package com.kanban.service;

import com.kanban.model.Task;
import com.kanban.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByEstado(status);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task, String usuario) {
        if (task.getTitulo() == null || task.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Titulo é obrigatorio");
        }
        if (task.getResponsavel() == null || task.getResponsavel().trim().isEmpty()) {
            throw new IllegalArgumentException("Responsavel é obrigatorio");
        }
        if (task.getDataInicio() == null || task.getDataFim() == null) {
            throw new IllegalArgumentException("Datas de inicio e fim são obrigatorias");
        }
        if (task.getDataFim().isBefore(task.getDataInicio())) {
            throw new IllegalArgumentException("Data de fim não pode ser anterior à data de inicio");
        }

        if (task.getEstado() == null) {
            task.setEstado("TO_DO");
        }
        
        task.setCriadoPor(usuario);
        task.setModificadoPor(usuario);
        task.setDataCriacao(LocalDateTime.now());
        task.setDataModificacao(LocalDateTime.now());

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails, String usuario) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task existingTask = optionalTask.get();

            if (taskDetails.getTitulo() != null) {
                existingTask.setTitulo(taskDetails.getTitulo());
            }
            if (taskDetails.getDescricao() != null) {
                existingTask.setDescricao(taskDetails.getDescricao());
            }
            if (taskDetails.getResponsavel() != null) {
                existingTask.setResponsavel(taskDetails.getResponsavel());
            }
            if (taskDetails.getEstado() != null) {
                existingTask.setEstado(taskDetails.getEstado());
            }
            if (taskDetails.getDataInicio() != null) {
                existingTask.setDataInicio(taskDetails.getDataInicio());
            }
            if (taskDetails.getDataFim() != null) {
                if (taskDetails.getDataFim().isBefore(existingTask.getDataInicio())) {
                    throw new IllegalArgumentException("Data de fim não pode ser anterior à data de inicio");
                }
                existingTask.setDataFim(taskDetails.getDataFim());
            }
            
            existingTask.setModificadoPor(usuario);
            existingTask.setDataModificacao(LocalDateTime.now());

            return taskRepository.save(existingTask);
        }
        return null;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}