--- src/domain/validators/CPFValidator.java (原始)
package domain.validators;

import java.util.UUID;
import java.util.regex.Pattern;

public class CPFValidator {
    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11}");

    public static boolean isValidFormat(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        String cleanCPF = cpf.replaceAll("\\D", "");
        if (cleanCPF.length() != 11) {
            return false;
        }
        return CPF_PATTERN.matcher(cpf).matches() || cleanCPF.matches("\\d{11}");
    }

    public static String cleanCPF(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    public static boolean isUnique(String cpf, java.util.List<domain.entities.Employee> employees, UUID excludeId) {
        String cleanCPF = cleanCPF(cpf);
        for (domain.entities.Employee emp : employees) {
            if (emp.getId().equals(excludeId)) {
                continue;
            }
            if (cleanCPF(emp.getCpf()).equals(cleanCPF)) {
                return false;
            }
        }
        return true;
    }
}

+++ src/domain/validators/CPFValidator.java (修改后)
