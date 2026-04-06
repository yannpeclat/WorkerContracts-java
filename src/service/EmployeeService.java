package service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import domain.entities.Employee;
import domain.entities.Address;
import domain.enums.EmployeeStatus;
import domain.validators.CPFValidator;
import domain.validators.EmailValidator;
import repository.EmployeeRepository;

public class EmployeeService {
    private EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public String registerEmployee(String name, String cpf, String email, String phone,
                                   LocalDate birthDate, Address address) {
        if (name == null || name.trim().isEmpty()) {
            return "Erro: Nome é obrigatório";
        }
        if (!CPFValidator.isValidFormat(cpf)) {
            return "Erro: CPF em formato inválido";
        }
        if (repository.existsByCpf(cpf, null)) {
            return "Erro: CPF já cadastrado no sistema";
        }
        if (!EmailValidator.isValid(email)) {
            return "Erro: Email em formato inválido (deve conter @)";
        }
        if (birthDate == null) {
            return "Erro: Data de nascimento é obrigatória";
        }
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 16) {
            return "Erro: Idade mínima é 16 anos";
        }

        Employee employee = new Employee(name, cpf, email, phone, birthDate, address);
        repository.save(employee);
        return "Sucesso: Funcionário cadastrado com ID: " + employee.getId().toString().substring(0, 8);
    }

    public List<Employee> listAll() {
        return repository.findAll();
    }

    public List<Employee> listByStatus(EmployeeStatus status) {
        return repository.findByStatus(status);
    }

    public List<Employee> searchByName(String name) {
        return repository.findByName(name);
    }

    public Optional<Employee> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Employee> findByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    public String updateEmployee(UUID id, String name, String email, String phone, Address address) {
        Optional<Employee> optional = repository.findById(id);
        if (!optional.isPresent()) {
            return "Erro: Funcionário não encontrado";
        }
        Employee employee = optional.get();
        
        if (name != null && !name.trim().isEmpty()) {
            employee.setName(name);
        }
        if (email != null && !email.trim().isEmpty()) {
            if (!EmailValidator.isValid(email)) {
                return "Erro: Email em formato inválido";
            }
            employee.setEmail(email);
        }
        if (phone != null) {
            employee.setPhone(phone);
        }
        if (address != null) {
            employee.setAddress(address);
        }
        
        repository.save(employee);
        return "Sucesso: Funcionário atualizado";
    }

    public String deactivateEmployee(UUID id) {
        Optional<Employee> optional = repository.findById(id);
        if (!optional.isPresent()) {
            return "Erro: Funcionário não encontrado";
        }
        Employee employee = optional.get();
        employee.setStatus(EmployeeStatus.INACTIVE);
        repository.save(employee);
        return "Sucesso: Funcionário desativado";
    }

    public int getTotalCount() {
        return repository.countAll();
    }

    public int getActiveCount() {
        return repository.countByStatus(EmployeeStatus.ACTIVE);
    }

    public int getInactiveCount() {
        return repository.countByStatus(EmployeeStatus.INACTIVE);
    }
}
