package br.com.alura.AluraFake.course;

import java.util.List;

public class InstructorCoursesReportDTO {

    private final List<InstructorCourseItemDTO> courses;
    private final Long totalPublishedCourses;

    public InstructorCoursesReportDTO(
            List<InstructorCourseItemDTO> courses,
            Long totalPublishedCourses
    ) {
        this.courses = courses;
        this.totalPublishedCourses = totalPublishedCourses;
    }

    public List<InstructorCourseItemDTO> getCourses() {
        return courses;
    }

    public Long getTotalPublishedCourses() {
        return totalPublishedCourses;
    }
}

