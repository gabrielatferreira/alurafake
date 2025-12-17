package br.com.alura.AluraFake.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseActivityRepository extends JpaRepository<CourseActivity,Long> {

    List<CourseActivity> findByCourse(Course course);

    boolean existsByCourseIdAndStatement(Long courseId, String statement);
}
