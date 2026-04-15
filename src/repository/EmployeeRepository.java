--- src/repository/EmployeeRepository.java (原始)
package repository;

import java.util.*;
import java.util.stream.Collectors;
import domain.entities.Employee;
import domain.enums.EmployeeStatus;

public class EmployeeRepository {
    private Map<UUID, Employee> employees = new HashMap<>();

    public Employee save(Employee employee) {
        employees.put(employee.getId(), employee);
        return employee;
    }

    public Optional<Employee> findById(UUID id) {
        return Optional.ofNullable(employees.get(id));
    }

    public Optional<Employee> findByCpf(String cpf) {
        String cleanCpf = cpf.replaceAll("\\D", "");
        for (Employee emp : employees.values()) {
            if (emp.getCpf().replaceAll("\\D", "").equals(cleanCpf)) {
                return Optional.of(emp);
            }
        }
        return Optional.empty();
    }

    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    public List<Employee> findByStatus(EmployeeStatus status) {
        return employees.values().stream()
            .filter(e -> e.getStatus() == status)
            .collect(Collectors.toList());
    }

    public List<Employee> findByName(String name) {
        String searchName = name.toLowerCase();
        return employees.values().stream()
            .filter(e -> e.getName().toLowerCase().contains(searchName))
            .collect(Collectors.toList());
    }

    public boolean existsByCpf(String cpf, UUID excludeId) {
        String cleanCpf = cpf.replaceAll("\\D", "");
        for (Employee emp : employees.values()) {
            if (excludeId != null && emp.getId().equals(excludeId)) {
                continue;
            }
            if (emp.getCpf().replaceAll("\\D", "").equals(cleanCpf)) {
                return true;
            }
        }
        return false;
    }

    public int countAll() {
        return employees.size();
    }

    public int countByStatus(EmployeeStatus status) {
        return (int) employees.values().stream()
            .filter(e -> e.getStatus() == status)
            .count();
    }
}

+++ src/repository/EmployeeRepository.java (修改后)
