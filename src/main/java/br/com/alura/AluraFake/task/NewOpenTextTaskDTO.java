package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public class NewOpenTextTaskDTO {

    @NotNull
    private Long courseId;

    @NotNull
    @NotBlank
    @Length(min = 4, max = 255)
    private String statement;

    @NotNull
    @Positive
    private Integer order;

    public NewOpenTextTaskDTO() {}

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }
}
