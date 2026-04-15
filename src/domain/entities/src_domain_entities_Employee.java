--- src/domain/entities/Employee.java (原始)
package domain.entities;

import java.time.LocalDate;
import java.util.UUID;
import domain.enums.EmployeeStatus;

public class Employee {
    private UUID id;
    private String name;
    private String cpf;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private Address address;
    private EmployeeStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Employee() {
        this.id = UUID.randomUUID();
        this.status = EmployeeStatus.ACTIVE;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public Employee(String name, String cpf, String email, String phone, LocalDate birthDate, Address address) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.address = address;
        this.status = EmployeeStatus.ACTIVE;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDate.now();
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDate.now();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = LocalDate.now();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.updatedAt = LocalDate.now();
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
        this.updatedAt = LocalDate.now();
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public int getAge() {
        if (birthDate == null) return 0;
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    @Override
    public String toString() {
        return "ID: " + id.toString().substring(0, 8) +
               " | Nome: " + name +
               " | CPF: " + cpf +
               " | Status: " + status +
               " | Email: " + email;
    }
}

+++ src/domain/entities/Employee.java (修改后)
