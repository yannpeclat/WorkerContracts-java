package domain.entities;

import java.time.LocalDate;
import java.util.UUID;
import domain.enums.ContractStatus;
import domain.enums.ContractType;

public class Contract {
    private UUID id;
    private Employee employee;
    private ContractType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private double salary;
    private String currency;
    private int weeklyHours;
    private ContractTerms terms;
    private ContractStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Contract() {
        this.id = UUID.randomUUID();
        this.status = ContractStatus.ACTIVE;
        this.currency = "BRL";
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public Contract(Employee employee, ContractType type, LocalDate startDate, double salary, 
                    String currency, int weeklyHours, ContractTerms terms) {
        this.id = UUID.randomUUID();
        this.employee = employee;
        this.type = type;
        this.startDate = startDate;
        this.salary = salary;
        this.currency = currency != null ? currency : "BRL";
        this.weeklyHours = weeklyHours;
        this.terms = terms;
        this.status = ContractStatus.ACTIVE;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public UUID getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.updatedAt = LocalDate.now();
    }

    public ContractType getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        this.updatedAt = LocalDate.now();
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
        this.updatedAt = LocalDate.now();
    }

    public String getCurrency() {
        return currency;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
        this.updatedAt = LocalDate.now();
    }

    public ContractTerms getTerms() {
        return terms;
    }

    public void setTerms(ContractTerms terms) {
        this.terms = terms;
        this.updatedAt = LocalDate.now();
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
        this.updatedAt = LocalDate.now();
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return status == ContractStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "ID: " + id.toString().substring(0, 8) + 
               " | Funcionário: " + (employee != null ? employee.getName() : "N/A") +
               " | Tipo: " + type + 
               " | Status: " + status +
               " | Salário: " + currency + " " + String.format("%.2f", salary);
    }
}
