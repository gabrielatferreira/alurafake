package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class NewSingleChoiceTaskDTO {

    @NotNull
    private Long courseId;

    @NotNull
    @NotBlank
    @Length(min = 10, max = 500)
    private String statement;

    @NotNull
    @Positive
    private Integer order;

    @NotNull
    @Valid
    private List<SingleChoiceOptionDTO> options;

    public NewSingleChoiceTaskDTO() {}

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }

    public List<SingleChoiceOptionDTO> getOptions() {
        return options;
    }
}
