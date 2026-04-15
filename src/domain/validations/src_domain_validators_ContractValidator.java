--- src/domain/validators/ContractValidator.java (原始)
package domain.validators;

import java.time.LocalDate;
import java.util.List;
import domain.entities.Employee;
import domain.entities.Contract;
import domain.enums.EmployeeStatus;
import domain.enums.ContractStatus;

public class ContractValidator {

    public static boolean canCreateContract(Employee employee, List<Contract> allContracts) {
        if (employee == null) {
            return false;
        }
        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            return false;
        }
        for (Contract contract : allContracts) {
            if (contract.getEmployee().getId().equals(employee.getId())
                && contract.getStatus() == ContractStatus.ACTIVE) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidSalary(double salary) {
        return salary > 0;
    }

    public static boolean isValidDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return false;
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            return false;
        }
        return true;
    }
}

+++ src/domain/validators/ContractValidator.java (修改后)
