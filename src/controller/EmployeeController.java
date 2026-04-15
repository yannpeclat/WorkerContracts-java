--- src/controller/EmployeeController.java (原始)
package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import domain.entities.Employee;
import domain.entities.Address;
import domain.enums.EmployeeStatus;
import service.EmployeeService;

public class EmployeeController {
    private EmployeeService service;
    private Scanner scanner;

    public EmployeeController(EmployeeService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== GERENCIAR FUNCIONÁRIOS ===");
            System.out.println("1. Cadastrar funcionário");
            System.out.println("2. Listar funcionários");
            System.out.println("3. Buscar funcionário por ID/CPF");
            System.out.println("4. Atualizar funcionário");
            System.out.println("5. Desativar funcionário");
            System.out.println("6. Voltar");
            System.out.print("Escolha: ");

            String option = scanner.nextLine();
            switch (option) {
                case "1": register(); break;
                case "2": list(); break;
                case "3": search(); break;
                case "4": update(); break;
                case "5": deactivate(); break;
                case "6": return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private void register() {
        System.out.println("\n=== CADASTRO DE FUNCIONÁRIO ===");

        System.out.print("Nome completo: ");
        String name = scanner.nextLine();

        System.out.print("CPF (apenas números ou XXX.XXX.XXX-XX): ");
        String cpf = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Telefone: ");
        String phone = scanner.nextLine();

        System.out.print("Data de nascimento (DD/MM/AAAA): ");
        LocalDate birthDate = parseDate(scanner.nextLine());
        if (birthDate == null) {
            System.out.println("Data inválida!");
            return;
        }

        System.out.print("Rua: ");
        String street = scanner.nextLine();
        System.out.print("Cidade: ");
        String city = scanner.nextLine();
        System.out.print("Estado: ");
        String state = scanner.nextLine();
        System.out.print("CEP: ");
        String zipCode = scanner.nextLine();
        System.out.print("País: ");
        String country = scanner.nextLine();

        Address address = new Address(street, city, state, zipCode, country);

        String result = service.registerEmployee(name, cpf, email, phone, birthDate, address);
        System.out.println(result);
    }

    private void list() {
        System.out.println("\n=== LISTAR FUNCIONÁRIOS ===");
        System.out.println("1. Todos");
        System.out.println("2. Ativos");
        System.out.println("3. Inativos");
        System.out.println("4. Por nome");
        System.out.print("Escolha: ");
        String choice = scanner.nextLine();

        List<Employee> employees = new ArrayList<>();
        switch (choice) {
            case "1": employees = service.listAll(); break;
            case "2": employees = service.listByStatus(EmployeeStatus.ACTIVE); break;
            case "3": employees = service.listByStatus(EmployeeStatus.INACTIVE); break;
            case "4":
                System.out.print("Digite o nome: ");
                employees = service.searchByName(scanner.nextLine());
                break;
            default: System.out.println("Opção inválida!"); return;
        }

        if (employees.isEmpty()) {
            System.out.println("Nenhum funcionário encontrado.");
        } else {
            for (Employee emp : employees) {
                System.out.println(emp);
            }
        }
    }

    private void search() {
        System.out.println("\n=== BUSCAR FUNCIONÁRIO ===");
        System.out.println("Buscar por: 1-ID / 2-CPF");
        System.out.print("Escolha: ");
        String choice = scanner.nextLine();

        Optional<Employee> result = Optional.empty();
        if ("1".equals(choice)) {
            System.out.print("ID: ");
            try {
                UUID id = UUID.fromString(scanner.nextLine());
                result = service.findById(id);
            } catch (Exception e) {
                System.out.println("ID inválido!");
                return;
            }
        } else if ("2".equals(choice)) {
            System.out.print("CPF: ");
            result = service.findByCpf(scanner.nextLine());
        } else {
            System.out.println("Opção inválida!");
            return;
        }

        if (result.isPresent()) {
            Employee emp = result.get();
            System.out.println("\n=== DADOS DO FUNCIONÁRIO ===");
            System.out.println("ID: " + emp.getId());
            System.out.println("Nome: " + emp.getName());
            System.out.println("CPF: " + emp.getCpf());
            System.out.println("Email: " + emp.getEmail());
            System.out.println("Telefone: " + emp.getPhone());
            System.out.println("Data Nascimento: " + emp.getBirthDate());
            System.out.println("Endereço: " + emp.getAddress());
            System.out.println("Status: " + emp.getStatus());
            System.out.println("Idade: " + emp.getAge() + " anos");
        } else {
            System.out.println("Funcionário não encontrado!");
        }
    }

    private void update() {
        System.out.println("\n=== ATUALIZAR FUNCIONÁRIO ===");
        System.out.print("ID do funcionário: ");
        UUID id;
        try {
            id = UUID.fromString(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido!");
            return;
        }

        Optional<Employee> existing = service.findById(id);
        if (!existing.isPresent()) {
            System.out.println("Funcionário não encontrado!");
            return;
        }

        Employee emp = existing.get();
        System.out.println("Dados atuais: " + emp.getName() + ", " + emp.getEmail() + ", " + emp.getPhone());
        System.out.println("(Pressione ENTER para manter valor atual)");

        System.out.print("Novo nome: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) name = null;

        System.out.print("Novo email: ");
        String email = scanner.nextLine();
        if (email.trim().isEmpty()) email = null;

        System.out.print("Novo telefone: ");
        String phone = scanner.nextLine();
        if (phone.trim().isEmpty()) phone = null;

        System.out.print("Atualizar endereço? (S/N): ");
        String updateAddr = scanner.nextLine();
        Address address = null;
        if (updateAddr.equalsIgnoreCase("S")) {
            System.out.print("Rua: ");
            String street = scanner.nextLine();
            System.out.print("Cidade: ");
            String city = scanner.nextLine();
            System.out.print("Estado: ");
            String state = scanner.nextLine();
            System.out.print("CEP: ");
            String zipCode = scanner.nextLine();
            System.out.print("País: ");
            String country = scanner.nextLine();
            address = new Address(street, city, state, zipCode, country);
        }

        String result = service.updateEmployee(id, name, email, phone, address);
        System.out.println(result);
    }

    private void deactivate() {
        System.out.println("\n=== DESATIVAR FUNCIONÁRIO ===");
        System.out.print("ID do funcionário: ");
        UUID id;
        try {
            id = UUID.fromString(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido!");
            return;
        }

        Optional<Employee> existing = service.findById(id);
        if (!existing.isPresent()) {
            System.out.println("Funcionário não encontrado!");
            return;
        }

        System.out.println("Funcionário: " + existing.get().getName());
        System.out.print("Confirma desativação? (S/N): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("S")) {
            String result = service.deactivateEmployee(id);
            System.out.println(result);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private LocalDate parseDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}

+++ src/controller/EmployeeController.java (修改后)
