--- src/repository/ContractRepository.java (原始)
package repository;

import java.util.*;
import java.util.stream.Collectors;
import domain.entities.Contract;
import domain.enums.ContractStatus;

public class ContractRepository {
    private Map<UUID, Contract> contracts = new HashMap<>();

    public Contract save(Contract contract) {
        contracts.put(contract.getId(), contract);
        return contract;
    }

    public Optional<Contract> findById(UUID id) {
        return Optional.ofNullable(contracts.get(id));
    }

    public List<Contract> findAll() {
        return new ArrayList<>(contracts.values());
    }

    public List<Contract> findByStatus(ContractStatus status) {
        return contracts.values().stream()
            .filter(c -> c.getStatus() == status)
            .collect(Collectors.toList());
    }

    public List<Contract> findByEmployeeId(UUID employeeId) {
        return contracts.values().stream()
            .filter(c -> c.getEmployee().getId().equals(employeeId))
            .collect(Collectors.toList());
    }

    public List<Contract> findActiveByEmployeeId(UUID employeeId) {
        return contracts.values().stream()
            .filter(c -> c.getEmployee().getId().equals(employeeId) && c.getStatus() == ContractStatus.ACTIVE)
            .collect(Collectors.toList());
    }

    public List<Contract> findByType(domain.enums.ContractType type) {
        return contracts.values().stream()
            .filter(c -> c.getType() == type)
            .collect(Collectors.toList());
    }

    public int countAll() {
        return contracts.size();
    }

    public int countByStatus(ContractStatus status) {
        return (int) contracts.values().stream()
            .filter(c -> c.getStatus() == status)
            .count();
    }

    public List<Contract> findExpiringSoon(int days) {
        java.time.LocalDate limitDate = java.time.LocalDate.now().plusDays(days);
        return contracts.values().stream()
            .filter(c -> c.getStatus() == ContractStatus.ACTIVE
                      && c.getEndDate() != null
                      && !c.getEndDate().isAfter(limitDate))
            .collect(Collectors.toList());
    }
}

+++ src/repository/ContractRepository.java (修改后)
