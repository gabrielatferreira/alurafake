package br.com.alura.AluraFake.course;

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
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByInstructor__should_return_courses_of_instructor() {
        User instructor = createInstructor();

        Course course1 = new Course("Java", "Curso Java", instructor);
        Course course2 = new Course("Spring", "Curso Spring", instructor);

        courseRepository.save(course1);
        courseRepository.save(course2);

        List<Course> result = courseRepository.findByInstructor(instructor);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    void findByInstructor__should_return_empty_list_when_instructor_has_no_courses() {
        User instructor = createInstructor();

        List<Course> result = courseRepository.findByInstructor(instructor);

        assertThat(result).isEmpty();
    }

    @Test
    void countByInstructorAndStatus__should_return_correct_count() {
        User instructor = createInstructor();

        Course c1 = new Course("Java", "Curso Java", instructor);
        c1.setStatus(Status.BUILDING);

        Course c2 = new Course("Spring", "Curso Spring", instructor);
        c2.setStatus(Status.BUILDING);

        Course c3 = new Course("Docker", "Curso Docker", instructor);
        c3.setStatus(Status.PUBLISHED);

        courseRepository.save(c1);
        courseRepository.save(c2);
        courseRepository.save(c3);

        long countBuilding =
                courseRepository.countByInstructorAndStatus(
                        instructor, Status.BUILDING
                );

        long countPublished =
                courseRepository.countByInstructorAndStatus(
                        instructor, Status.PUBLISHED
                );

        assertThat(countBuilding).isEqualTo(2);
        assertThat(countPublished).isEqualTo(1);
    }

    @Test
    void countByInstructorAndStatus__should_return_zero_when_no_courses_match() {
        User instructor = createInstructor();

        long count =
                courseRepository.countByInstructorAndStatus(
                        instructor, Status.PUBLISHED
                );

        assertThat(count).isZero();
    }

    private User createInstructor() {
        User instructor = new User(
                "Instrutor",
                "instrutor@alura.com.br",
                Role.INSTRUCTOR
        );
        return userRepository.save(instructor);
    }
}

