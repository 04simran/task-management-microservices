package com.optipace.task.service;

import com.optipace.task.dto.TaskRequest;
import com.optipace.task.dto.TaskResponse;
import jakarta.validation.Valid;

import java.util.List;


public interface TaskService {


    TaskResponse createTask(@Valid TaskRequest request, Long userId);

    List<TaskResponse> getTasks(Long userId, String role);

    TaskResponse updateTask(Long id, @Valid TaskRequest request, Long userId, String role);

    void deleteTask(Long id, Long userId, String role);
}