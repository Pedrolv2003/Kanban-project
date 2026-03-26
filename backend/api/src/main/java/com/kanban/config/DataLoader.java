package com.kanban.config;

import com.kanban.service.ColumnService;
import com.kanban.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ColumnService columnService;
    
    @Override
    public void run(String... args) throws Exception {
        // Criar usuário admin
        if (!userService.existsByEmail("admin@kanban.com")) {
            userService.createUser("admin@kanban.com", "admin123", "ADMIN");
            System.out.println("Admin criado: admin@kanban.com / admin123");
        }
        
        // Inicializar colunas padrão
        columnService.initializeDefaultColumns();
    }
}