package domain.entities;

import java.util.List;

public class ContractTerms {
    private List<String> benefits;
    private String bonusPolicy;
    private int vacationDays;
    private String terminationPolicy;

    public ContractTerms() {
    }

    public ContractTerms(List<String> benefits, String bonusPolicy, int vacationDays, String terminationPolicy) {
        this.benefits = benefits;
        this.bonusPolicy = bonusPolicy;
        this.vacationDays = vacationDays;
        this.terminationPolicy = terminationPolicy;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public String getBonusPolicy() {
        return bonusPolicy;
    }

    public void setBonusPolicy(String bonusPolicy) {
        this.bonusPolicy = bonusPolicy;
    }

    public int getVacationDays() {
        return vacationDays;
    }

    public void setVacationDays(int vacationDays) {
        this.vacationDays = vacationDays;
    }

    public String getTerminationPolicy() {
        return terminationPolicy;
    }

    public void setTerminationPolicy(String terminationPolicy) {
        this.terminationPolicy = terminationPolicy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Benefícios: ").append(benefits).append("\n");
        sb.append("Política de Bônus: ").append(bonusPolicy).append("\n");
        sb.append("Férias (dias): ").append(vacationDays).append("\n");
        sb.append("Política de Rescisão: ").append(terminationPolicy);
        return sb.toString();
    }
}
