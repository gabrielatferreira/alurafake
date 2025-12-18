package br.com.alura.AluraFake.course.activity;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CourseActivityRepositoryTest {

    @Autowired
    private CourseActivityRepository courseActivityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void findByCourse__should_return_activities_from_course() {
        Course course = createCourse();

        courseActivityRepository.save(
                new CourseActivity(course, "Pergunta", Type.OPEN_TEXT, 1)
        );

        List<CourseActivity> result =
                courseActivityRepository.findByCourse(course);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatement()).isEqualTo("Pergunta");
    }

    @Test
    void existsByCourseIdAndStatement__should_return_true_when_statement_exists() {
        Course course = createCourse();

        courseActivityRepository.save(
                new CourseActivity(course, "Enunciado existente", Type.OPEN_TEXT, 1)
        );

        boolean exists =
                courseActivityRepository.existsByCourseIdAndStatement(
                        course.getId(), "Enunciado existente"
                );

        assertThat(exists).isTrue();
    }

    @Test
    void existsByCourseIdAndStatement__should_return_false_when_statement_does_not_exist() {
        Course course = createCourse();

        boolean exists =
                courseActivityRepository.existsByCourseIdAndStatement(
                        course.getId(), "Inexistente"
                );

        assertThat(exists).isFalse();
    }

    @Test
    void findMaxOrderByCourseId__should_return_highest_activity_order() {
        Course course = createCourse();

        courseActivityRepository.save(
                new CourseActivity(course, "A1", Type.OPEN_TEXT, 1)
        );
        courseActivityRepository.save(
                new CourseActivity(course, "A2", Type.OPEN_TEXT, 3)
        );
        courseActivityRepository.save(
                new CourseActivity(course, "A3", Type.OPEN_TEXT, 2)
        );

        Integer maxOrder =
                courseActivityRepository.findMaxOrderByCourseId(course.getId());

        assertThat(maxOrder).isEqualTo(3);
    }

    @Test
    void findMaxOrderByCourseId__should_return_null_when_course_has_no_activities() {
        Course course = createCourse();

        Integer maxOrder =
                courseActivityRepository.findMaxOrderByCourseId(course.getId());

        assertThat(maxOrder).isNull();
    }

    @Test
    void findByCourseIdAndActivityOrderGreaterThanEqualOrderByActivityOrderDesc__should_return_ordered_activities() {
        Course course = createCourse();

        courseActivityRepository.save(
                new CourseActivity(course, "A1", Type.OPEN_TEXT, 1)
        );
        courseActivityRepository.save(
                new CourseActivity(course, "A2", Type.OPEN_TEXT, 2)
        );
        courseActivityRepository.save(
                new CourseActivity(course, "A3", Type.OPEN_TEXT, 3)
        );

        List<CourseActivity> result =
                courseActivityRepository
                        .findByCourseIdAndActivityOrderGreaterThanEqualOrderByActivityOrderDesc(
                                course.getId(), 2
                        );

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getActivityOrder()).isEqualTo(3);
        assertThat(result.get(1).getActivityOrder()).isEqualTo(2);
    }

    private User createInstructor() {
        User instructor = new User(
                "Instrutor",
                "instrutor@alura.com.br",
                Role.INSTRUCTOR
        );
        return userRepository.save(instructor);
    }

    private Course createCourse() {
        User instructor = createInstructor();
        Course course = new Course(
                "Java",
                "Curso de Java",
                instructor
        );
        return courseRepository.save(course);
    }
}
