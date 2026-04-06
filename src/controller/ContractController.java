package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import domain.entities.Contract;
import domain.entities.ContractTerms;
import domain.enums.ContractStatus;
import domain.enums.ContractType;
import service.ContractService;
import repository.EmployeeRepository;

public class ContractController {
    private ContractService service;
    private EmployeeRepository employeeRepository;
    private Scanner scanner;

    public ContractController(ContractService service, EmployeeRepository employeeRepository, Scanner scanner) {
        this.service = service;
        this.employeeRepository = employeeRepository;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== GERENCIAR CONTRATOS ===");
            System.out.println("1. Criar contrato");
            System.out.println("2. Listar contratos");
            System.out.println("3. Buscar contrato");
            System.out.println("4. Atualizar contrato");
            System.out.println("5. Encerrar contrato");
            System.out.println("6. Voltar");
            System.out.print("Escolha: ");

            String option = scanner.nextLine();
            switch (option) {
                case "1": create(); break;
                case "2": list(); break;
                case "3": search(); break;
                case "4": update(); break;
                case "5": terminate(); break;
                case "6": return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private void create() {
        System.out.println("\n=== CRIAR CONTRATO ===");
        
        System.out.print("ID do funcionário: ");
        UUID employeeId;
        try {
            employeeId = UUID.fromString(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido!");
            return;
        }
        
        var empOpt = employeeRepository.findById(employeeId);
        if (!empOpt.isPresent()) {
            System.out.println("Funcionário não encontrado!");
            return;
        }
        System.out.println("Funcionário: " + empOpt.get().getName());
        
        System.out.println("Tipo de contrato:");
        System.out.println("1. CLT");
        System.out.println("2. PJ");
        System.out.println("3. TEMPORÁRIO");
        System.out.println("4. ESTÁGIO");
        System.out.print("Escolha: ");
        String typeChoice = scanner.nextLine();
        ContractType type;
        switch (typeChoice) {
            case "1": type = ContractType.CLT; break;
            case "2": type = ContractType.PJ; break;
            case "3": type = ContractType.TEMPORARIO; break;
            case "4": type = ContractType.ESTAGIO; break;
            default: System.out.println("Tipo inválido!"); return;
        }
        
        System.out.print("Data de início (DD/MM/AAAA): ");
        LocalDate startDate = parseDate(scanner.nextLine());
        if (startDate == null) {
            System.out.println("Data inválida!");
            return;
        }
        
        System.out.print("Data de fim (opcional, DD/MM/AAAA ou ENTER para vazio): ");
        String endDateStr = scanner.nextLine();
        LocalDate endDate = null;
        if (!endDateStr.trim().isEmpty()) {
            endDate = parseDate(endDateStr);
            if (endDate == null) {
                System.out.println("Data de fim inválida!");
                return;
            }
        }
        
        System.out.print("Salário: ");
        double salary;
        try {
            salary = Double.parseDouble(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Salário inválido!");
            return;
        }
        
        System.out.print("Moeda (padrão BRL): ");
        String currency = scanner.nextLine();
        if (currency.trim().isEmpty()) currency = "BRL";
        
        System.out.print("Carga horária semanal (horas): ");
        int weeklyHours;
        try {
            weeklyHours = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Carga horária inválida!");
            return;
        }
        
        System.out.println("=== TERMOS DO CONTRATO ===");
        System.out.print("Benefícios (separados por vírgula): ");
        String benefitsStr = scanner.nextLine();
        List<String> benefits = Arrays.asList(benefitsStr.split("\\s*,\\s*"));
        
        System.out.print("Política de bônus: ");
        String bonusPolicy = scanner.nextLine();
        
        System.out.print("Dias de férias: ");
        int vacationDays;
        try {
            vacationDays = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            vacationDays = 30;
        }
        
        System.out.print("Política de rescisão: ");
        String terminationPolicy = scanner.nextLine();
        
        ContractTerms terms = new ContractTerms(benefits, bonusPolicy, vacationDays, terminationPolicy);
        
        String result = service.createContract(employeeId, type, startDate, endDate, salary, currency, weeklyHours, terms);
        System.out.println(result);
    }

    private void list() {
        System.out.println("\n=== LISTAR CONTRATOS ===");
        System.out.println("1. Todos");
        System.out.println("2. Ativos");
        System.out.println("3. Encerrados");
        System.out.println("4. Por tipo");
        System.out.print("Escolha: ");
        String choice = scanner.nextLine();
        
        List<Contract> contracts = new ArrayList<>();
        switch (choice) {
            case "1": contracts = service.listAll(); break;
            case "2": contracts = service.listByStatus(ContractStatus.ACTIVE); break;
            case "3": contracts = service.listByStatus(ContractStatus.TERMINATED); break;
            case "4":
                System.out.println("Tipos: 1-CLT, 2-PJ, 3-TEMPORARIO, 4-ESTAGIO");
                System.out.print("Escolha: ");
                String t = scanner.nextLine();
                ContractType type;
                switch (t) {
                    case "1": type = ContractType.CLT; break;
                    case "2": type = ContractType.PJ; break;
                    case "3": type = ContractType.TEMPORARIO; break;
                    case "4": type = ContractType.ESTAGIO; break;
                    default: System.out.println("Tipo inválido!"); return;
                }
                contracts = service.listAll().stream().filter(c -> c.getType() == type).toList();
                break;
            default: System.out.println("Opção inválida!"); return;
        }
        
        if (contracts.isEmpty()) {
            System.out.println("Nenhum contrato encontrado.");
        } else {
            for (Contract c : contracts) {
                System.out.println(c);
            }
        }
    }

    private void search() {
        System.out.println("\n=== BUSCAR CONTRATO ===");
        System.out.print("ID do contrato: ");
        UUID id;
        try {
            id = UUID.fromString(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido!");
            return;
        }
        
        Optional<Contract> result = service.findById(id);
        if (result.isPresent()) {
            Contract c = result.get();
            System.out.println("\n=== DADOS DO CONTRATO ===");
            System.out.println("ID: " + c.getId());
            System.out.println("Funcionário: " + c.getEmployee().getName());
            System.out.println("Tipo: " + c.getType());
            System.out.println("Status: " + c.getStatus());
            System.out.println("Início: " + c.getStartDate());
            System.out.println("Fim: " + c.getEndDate());
            System.out.println("Salário: " + c.getCurrency() + " " + c.getSalary());
            System.out.println("Carga Horária: " + c.getWeeklyHours() + "h/semana");
            System.out.println("\nTermos:");
            System.out.println(c.getTerms());
        } else {
            System.out.println("Contrato não encontrado!");
        }
    }

    private void update() {
        System.out.println("\n=== ATUALIZAR CONTRATO ===");
        System.out.print("ID do contrato: ");
        UUID id;
        try {
            id = UUID.fromString(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido!");
            return;
        }
        
        Optional<Contract> existing = service.findById(id);
        if (!existing.isPresent()) {
            System.out.println("Contrato não encontrado!");
            return;
        }
        
        Contract c = existing.get();
        System.out.println("Dados atuais: Salário=" + c.getSalary() + ", Carga=" + c.getWeeklyHours() + "h");
        System.out.println("(Pressione ENTER para manter valor atual)");
        
        System.out.print("Novo salário: ");
        String salStr = scanner.nextLine();
        Double salary = null;
        if (!salStr.trim().isEmpty()) {
            try { salary = Double.parseDouble(salStr); } catch (Exception e) { System.out.println("Valor inválido, mantendo atual."); }
        }
        
        System.out.print("Nova carga horária: ");
        String hoursStr = scanner.nextLine();
        Integer weeklyHours = null;
        if (!hoursStr.trim().isEmpty()) {
            try { weeklyHours = Integer.parseInt(hoursStr); } catch (Exception e) { System.out.println("Valor inválido, mantendo atual."); }
        }
        
        String result = service.updateContract(id, salary, weeklyHours, null);
        System.out.println(result);
    }

    private void terminate() {
        System.out.println("\n=== ENCERRAR CONTRATO ===");
        System.out.print("ID do contrato: ");
        UUID id;
        try {
            id = UUID.fromString(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido!");
            return;
        }
        
        Optional<Contract> existing = service.findById(id);
        if (!existing.isPresent()) {
            System.out.println("Contrato não encontrado!");
            return;
        }
        
        System.out.println("Contrato: " + existing.get());
        System.out.print("Data de término (DD/MM/AAAA ou ENTER para hoje): ");
        String dateStr = scanner.nextLine();
        LocalDate endDate = null;
        if (!dateStr.trim().isEmpty()) {
            endDate = parseDate(dateStr);
            if (endDate == null) {
                System.out.println("Data inválida!");
                return;
            }
        }
        
        String result = service.terminateContract(id, endDate);
        System.out.println(result);
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
