package com.hrms.controller;

import com.hrms.dto.EmployeeRequest;
import com.hrms.dto.EmployeeResponse;
import com.hrms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create a new employee")
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        return new ResponseEntity<>(employeeService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all employees")
    public ResponseEntity<List<EmployeeResponse>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}