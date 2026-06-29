package com.todo.task_service.controller;

import com.todo.task_service.model.Task;
import com.todo.task_service.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        String username = taskService.validateToken(token);
        if (username == null) return ResponseEntity.status(401).body("Invalid token");
        List<Task> tasks = taskService.getTasks(username);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody Task task) {
        String token = extractToken(authHeader);
        String username = taskService.validateToken(token);
        if (username == null) return ResponseEntity.status(401).body("Invalid token");
        Task created = taskService.createTask(task, username);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id,
                                        @RequestBody Task task) {
        String token = extractToken(authHeader);
        String username = taskService.validateToken(token);
        if (username == null) return ResponseEntity.status(401).body("Invalid token");
        Task updated = taskService.updateTask(id, task, username);
        if (updated == null) return ResponseEntity.status(403).body("Not allowed");
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id) {
        String token = extractToken(authHeader);
        String username = taskService.validateToken(token);
        if (username == null) return ResponseEntity.status(401).body("Invalid token");
        boolean deleted = taskService.deleteTask(id, username);
        if (!deleted) return ResponseEntity.status(403).body("Not allowed");
        return ResponseEntity.ok("Task deleted");
    }
}