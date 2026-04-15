--- src/application/Main.java (原始)
package application;

import java.util.Scanner;
import repository.EmployeeRepository;
import repository.ContractRepository;
import service.EmployeeService;
import service.ContractService;
import controller.EmployeeController;
import controller.ContractController;
import domain.entities.Contract;
import domain.enums.ContractStatus;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        EmployeeRepository employeeRepo = new EmployeeRepository();
        ContractRepository contractRepo = new ContractRepository();

        EmployeeService employeeService = new EmployeeService(employeeRepo);
        ContractService contractService = new ContractService(contractRepo, employeeRepo);

        EmployeeController employeeController = new EmployeeController(employeeService, scanner);
        ContractController contractController = new ContractController(contractService, employeeRepo, scanner);

        System.out.println("===========================================");
        System.out.println("   SISTEMA DE GESTAO DE CONTRATOS");
        System.out.println("===========================================");

        while (true) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Gerenciar Funcionarios");
            System.out.println("2. Gerenciar Contratos");
            System.out.println("3. Dashboard (Visao Geral)");
            System.out.println("4. Relatorios");
            System.out.println("5. Sair");
            System.out.print("Escolha: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    employeeController.showMenu();
                    break;
                case "2":
                    contractController.showMenu();
                    break;
                case "3":
                    showDashboard(employeeService, contractService);
                    break;
                case "4":
                    showReports(employeeService, contractService, scanner);
                    break;
                case "5":
                    System.out.println("Saindo do sistema...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opcao invalida!");
            }
        }
    }

    private static void showDashboard(EmployeeService empService, ContractService contService) {
        System.out.println("\n=== DASHBOARD ===");
        System.out.println("--- Funcionarios ---");
        System.out.println("Total: " + empService.getTotalCount());
        System.out.println("Ativos: " + empService.getActiveCount());
        System.out.println("Inativos: " + empService.getInactiveCount());
        System.out.println("\n--- Contratos ---");
        System.out.println("Total: " + contService.getTotalCount());
        System.out.println("Ativos: " + contService.getActiveCount());
        System.out.println("Expirando em 30 dias: " + contService.findExpiringSoon(30).size());
    }

    private static void showReports(EmployeeService empService, ContractService contService, Scanner scanner) {
        System.out.println("\n=== RELATORIOS ===");
        System.out.println("1. Funcionarios ativos");
        System.out.println("2. Contratos ativos");
        System.out.println("3. Historico por funcionario");
        System.out.println("4. Folha de pagamento (simples)");
        System.out.println("5. Voltar");
        System.out.print("Escolha: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                System.out.println("\n=== FUNCIONARIOS ATIVOS ===");
                empService.listAll().stream()
                    .filter(e -> e.getStatus().toString().equals("ACTIVE"))
                    .forEach(System.out::println);
                break;
            case "2":
                System.out.println("\n=== CONTRATOS ATIVOS ===");
                contService.listAll().stream()
                    .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
                    .forEach(System.out::println);
                break;
            case "3":
                System.out.print("ID do funcionario: ");
                try {
                    java.util.UUID id = java.util.UUID.fromString(scanner.nextLine());
                    var contracts = contService.listByEmployee(id);
                    if (contracts.isEmpty()) {
                        System.out.println("Nenhum contrato encontrado.");
                    } else {
                        for (var c : contracts) {
                            System.out.println(c);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ID invalido!");
                }
                break;
            case "4":
                System.out.println("\n=== FOLHA DE PAGAMENTO ===");
                double total = 0;
                int count = 0;
                for (var c : contService.listAll()) {
                    if (c.getStatus() == ContractStatus.ACTIVE) {
                        System.out.println(c.getEmployee().getName() + ": " + c.getCurrency() + " " + c.getSalary());
                        total += c.getSalary();
                        count++;
                    }
                }
                System.out.println("\nTotal de contratos ativos: " + count);
                System.out.println("Valor total da folha: " + total);
                break;
            case "5":
                return;
            default:
                System.out.println("Opcao invalida!");
        }
    }
}

+++ src/application/Main.java (修改后)
