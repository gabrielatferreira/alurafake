package br.com.alura.AluraFake.course.activity;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseActivityServiceTest {

    @Mock
    private CourseActivityRepository activityRepository;

    @InjectMocks
    private CourseActivityService activityService;

    private Course course;

    @BeforeEach
    void setup() {
        course = new Course("Java", "Curso de Java", mockInstructor());
    }

    private User mockInstructor() {
        User user = mock(User.class);
        when(user.isInstructor()).thenReturn(true);
        return user;
    }

    @Test
    void validateAndReorder__should_throw_exception_when_new_order_is_invalid() {
        when(activityRepository.findMaxOrderByCourseId(course.getId())).thenReturn(2);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> activityService.validateAndReorder(course, 4)
        );

        assertEquals(
                "Ordem inválida. A sequência de atividades está incorreta.",
                exception.getMessage()
        );
    }

    @Test
    void validateAndReorder__should_shift_activities_when_new_order_is_valid() {
        when(activityRepository.findMaxOrderByCourseId(course.getId())).thenReturn(3);

        CourseActivity a1 = new CourseActivity(course, "A1", null, 2);
        CourseActivity a2 = new CourseActivity(course, "A2", null, 3);

        when(activityRepository.findByCourseIdAndActivityOrderGreaterThanEqualOrderByActivityOrderDesc(
                course.getId(), 2))
                .thenReturn(List.of(a2, a1));

        activityService.validateAndReorder(course, 2);

        assertEquals(3, a1.getActivityOrder());
        assertEquals(4, a2.getActivityOrder());
    }
}
