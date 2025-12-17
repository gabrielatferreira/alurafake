package br.com.alura.AluraFake.course.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;

import java.time.LocalDateTime;

public class InstructorCourseItemDTO {

    private final Long id;
    private final String title;
    private final Status status;
    private final LocalDateTime publishedAt;
    private final Long activitiesCount;

    public InstructorCourseItemDTO(Course course, Long activitiesCount) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.status = course.getStatus();
        this.publishedAt = course.getPublishedAt();
        this.activitiesCount = activitiesCount;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public Long getActivitiesCount() {
        return activitiesCount;
    }
}

