package br.com.alura.AluraFake.course;

import java.io.Serializable;

public class CourseListItemDTO implements Serializable {

    private final Long id;
    private final String title;
    private final String description;
    private final Status status;

    public CourseListItemDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.status = course.getStatus();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }
}
