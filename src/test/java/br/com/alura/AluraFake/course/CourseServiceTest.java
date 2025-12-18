package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.activity.CourseActivity;
import br.com.alura.AluraFake.course.activity.CourseActivityRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseActivityRepository courseActivityRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;

    @BeforeEach
    void setup() {
        User instructor = new User(
                "Instrutor",
                "instrutor@alura.com.br",
                Role.INSTRUCTOR
        );

        course = new Course("Java", "Curso Java", instructor);
        course.setStatus(Status.BUILDING);
    }

    @Test
    void publish__should_throw_404_when_course_not_found() {
        when(courseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.publish(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Curso não encontrado");
    }

    @Test
    void publish__should_throw_when_course_is_not_building() {
        course.setStatus(Status.PUBLISHED);

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.publish(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não está em BUILDING");
    }

    @Test
    void publish__should_throw_when_course_has_no_activities() {
        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(courseActivityRepository.findByCourse(course))
                .thenReturn(List.of());

        assertThatThrownBy(() -> courseService.publish(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não há atividades");
    }

    @Test
    void publish__should_throw_when_not_all_activity_types_exist() {
        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(courseActivityRepository.findByCourse(course))
                .thenReturn(List.of(
                        activity(Type.OPEN_TEXT, 1),
                        activity(Type.SINGLE_CHOICE, 2)
                ));

        assertThatThrownBy(() -> courseService.publish(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ao menos uma atividade de cada tipo");
    }

    @Test
    void publish__should_throw_when_activity_order_is_not_continuous() {
        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(courseActivityRepository.findByCourse(course))
                .thenReturn(List.of(
                        activity(Type.OPEN_TEXT, 1),
                        activity(Type.SINGLE_CHOICE, 3),
                        activity(Type.MULTIPLE_CHOICE, 4)
                ));

        assertThatThrownBy(() -> courseService.publish(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ordem das atividades não está contínua");
    }

    @Test
    void publish__should_publish_course_when_all_rules_are_valid() {
        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(courseActivityRepository.findByCourse(course))
                .thenReturn(List.of(
                        activity(Type.OPEN_TEXT, 1),
                        activity(Type.SINGLE_CHOICE, 2),
                        activity(Type.MULTIPLE_CHOICE, 3)
                ));

        courseService.publish(1L);

        verify(courseRepository).save(course);
        assertThat(course.getStatus()).isEqualTo(Status.PUBLISHED);
    }

    private CourseActivity activity(Type type, int order) {
        return new CourseActivity(
                course,
                "Atividade " + order,
                type,
                order
        );
    }
}

