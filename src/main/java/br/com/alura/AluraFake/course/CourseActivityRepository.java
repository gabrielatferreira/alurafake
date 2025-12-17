package br.com.alura.AluraFake.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseActivityRepository extends JpaRepository<CourseActivity,Long> {

    List<CourseActivity> findByCourse(Course course);

    boolean existsByCourseIdAndStatement(Long courseId, String statement);

    // Maior ordem atual do curso
    @Query("select max(c.activityOrder) from CourseActivity c where c.course.id = :courseId")
    Integer findMaxOrderByCourseId(Long courseId);

    // Atividades com ordem >= a nova
    List<CourseActivity> findByCourseIdAndActivityOrderGreaterThanEqualOrderByActivityOrderDesc(
            Long courseId,
            Integer activityOrder
    );

    long countByCourse(Course course);
}
