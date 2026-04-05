package com.optipace.task.service.Impl;

import com.optipace.task.dto.TaskRequest;
import com.optipace.task.dto.TaskResponse;
import com.optipace.task.entity.Task;
import com.optipace.task.entity.User;
import com.optipace.task.exception.BadRequestException;
import com.optipace.task.exception.ResourceNotFoundException;
import com.optipace.task.repository.TaskRepository;
import com.optipace.task.repository.UserRepository;
import com.optipace.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;



    public TaskResponse createTask(TaskRequest request, Long userId) {

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BadRequestException("Title is required");
        }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new BadRequestException("Description is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                task.setStatus(Task.TaskStatus.valueOf(request.getStatus()));
            } catch (Exception e) {
                throw new BadRequestException("Invalid status");
            }
        } else {
            task.setStatus(Task.TaskStatus.PENDING);
        }

        task.setUser(user);

        return mapToResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getTasks(Long userId, String role) {

        List<Task> tasks = "ADMIN".equals(role)
                ? taskRepository.findAll()
                : taskRepository.findByUserId(userId);

        return tasks.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long taskId, Long userId, String role) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!"ADMIN".equals(role) && !task.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        return mapToResponse(task);
    }

    public TaskResponse updateTask(Long taskId, TaskRequest request, Long userId, String role) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!"ADMIN".equals(role) && !task.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            task.setDescription(request.getDescription());
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                task.setStatus(Task.TaskStatus.valueOf(request.getStatus()));
            } catch (Exception e) {
                throw new BadRequestException("Invalid status");
            }
        }

        return mapToResponse(taskRepository.save(task));
    }

    public void deleteTask(Long taskId, Long userId, String role) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!"ADMIN".equals(role) && !task.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        taskRepository.delete(task);
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .createdBy(task.getUser().getId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

}
