package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class NewMultipleChoiceTaskDTO {

    @NotNull
    private Long courseId;

    @NotBlank
    private String statement;

    @NotNull
    @Positive
    private Integer order;

    @NotNull
    @Size(min = 3, max = 5)
    private List<MultipleChoiceOptionDTO> options;

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }

    public List<MultipleChoiceOptionDTO> getOptions() {
        return options;
    }
}
