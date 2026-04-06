package service;

import java.time.LocalDate;
import java.util.*;
import domain.entities.Contract;
import domain.entities.ContractTerms;
import domain.enums.ContractStatus;
import domain.enums.ContractType;
import domain.validators.ContractValidator;
import repository.ContractRepository;
import repository.EmployeeRepository;

public class ContractService {
    private ContractRepository repository;
    private EmployeeRepository employeeRepository;

    public ContractService(ContractRepository repository, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
    }

    public String createContract(UUID employeeId, ContractType type, LocalDate startDate,
                                  LocalDate endDate, double salary, String currency, int weeklyHours,
                                  ContractTerms terms) {
        var empOpt = employeeRepository.findById(employeeId);
        if (!empOpt.isPresent()) {
            return "Erro: Funcionário não encontrado";
        }
        var employee = empOpt.get();
        
        if (!ContractValidator.canCreateContract(employee, repository.findAll())) {
            return "Erro: Funcionário já possui contrato ativo ou está inativo";
        }
        if (!ContractValidator.isValidSalary(salary)) {
            return "Erro: Salário deve ser maior que zero";
        }
        if (!ContractValidator.isValidDates(startDate, endDate)) {
            return "Erro: Data de início inválida ou data fim anterior ao início";
        }

        Contract contract = new Contract(employee, type, startDate, salary, currency, weeklyHours, terms);
        contract.setEndDate(endDate);
        repository.save(contract);
        return "Sucesso: Contrato criado com ID: " + contract.getId().toString().substring(0, 8);
    }

    public List<Contract> listAll() {
        return repository.findAll();
    }

    public List<Contract> listByStatus(ContractStatus status) {
        return repository.findByStatus(status);
    }

    public List<Contract> listByEmployee(UUID employeeId) {
        return repository.findByEmployeeId(employeeId);
    }

    public Optional<Contract> findById(UUID id) {
        return repository.findById(id);
    }

    public String updateContract(UUID id, Double salary, Integer weeklyHours, ContractTerms terms) {
        Optional<Contract> optional = repository.findById(id);
        if (!optional.isPresent()) {
            return "Erro: Contrato não encontrado";
        }
        Contract contract = optional.get();
        
        if (salary != null && salary > 0) {
            contract.setSalary(salary);
        }
        if (weeklyHours != null && weeklyHours > 0) {
            contract.setWeeklyHours(weeklyHours);
        }
        if (terms != null) {
            contract.setTerms(terms);
        }
        
        repository.save(contract);
        return "Sucesso: Contrato atualizado";
    }

    public String terminateContract(UUID id, LocalDate endDate) {
        Optional<Contract> optional = repository.findById(id);
        if (!optional.isPresent()) {
            return "Erro: Contrato não encontrado";
        }
        Contract contract = optional.get();
        if (contract.getStatus() == ContractStatus.TERMINATED) {
            return "Erro: Contrato já está encerrado";
        }
        
        contract.setStatus(ContractStatus.TERMINATED);
        contract.setEndDate(endDate != null ? endDate : LocalDate.now());
        repository.save(contract);
        return "Sucesso: Contrato encerrado";
    }

    public int getTotalCount() {
        return repository.countAll();
    }

    public int getActiveCount() {
        return repository.countByStatus(ContractStatus.ACTIVE);
    }

    public List<Contract> findExpiringSoon(int days) {
        return repository.findExpiringSoon(days);
    }
}
