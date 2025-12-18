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

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<SingleChoiceOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<SingleChoiceOptionDTO> options) {
        this.options = options;
    }
}
