package com.optipace.task.controller;

import com.optipace.task.dto.TaskRequest;
import com.optipace.task.dto.TaskResponse;
import com.optipace.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.optipace.task.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/v1/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        return new ResponseEntity<>(
                taskService.createTask(request, user.getUserId()),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/v1/list")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TaskResponse>> getTasks(
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                taskService.getTasks(user.getUserId(), user.getRole())
        );
    }


    @PutMapping("/v1/update/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable("id") Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                taskService.updateTask(id, request, user.getUserId(), user.getRole())
        );
    }

    @DeleteMapping("/v1/delete/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteTask(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails user) {

        taskService.deleteTask(id, user.getUserId(), user.getRole());
        return ResponseEntity.ok().build();}
}