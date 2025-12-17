package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class SingleChoiceOptionDTO {

    @NotNull
    @NotBlank
    @Length(min = 4, max = 80)
    private String option;

    @NotNull
    private Boolean isCorrect;

    public SingleChoiceOptionDTO() {
    }

    public String getOption() {
        return option;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

}
