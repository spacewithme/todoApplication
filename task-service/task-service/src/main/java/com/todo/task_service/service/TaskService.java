package com.todo.task_service.service;

import com.todo.task_service.model.Task;
import com.todo.task_service.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Value("${user.service.url}")
    private String userServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String validateToken(String token) {
        String url = userServiceUrl + "/api/users/validate?token=" + token;
        Map response = restTemplate.getForObject(url, Map.class);
        if (response != null && Boolean.TRUE.equals(response.get("valid"))) {
            return (String) response.get("username");
        }
        return null;
    }

    public List<Task> getTasks(String username) {
        return taskRepository.findByUsername(username);
    }

    public Task createTask(Task task, String username) {
        task.setUsername(username);
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask, String username) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null || !task.getUsername().equals(username)) {
            return null;
        }
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        return taskRepository.save(task);
    }

    public boolean deleteTask(Long id, String username) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null || !task.getUsername().equals(username)) {
            return false;
        }
        taskRepository.delete(task);
        return true;
    }
}