package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.course.activity.*;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseActivityRepository activityRepository;

    @Mock
    private CourseActivityOptionRepository optionRepository;

    @Mock
    private CourseActivityService courseActivityService;

    @InjectMocks
    private TaskService taskService;

    private Course course;

    @BeforeEach
    void setup() {
        User instructor = new User(
                "Instrutor",
                "instrutor@alura.com.br",
                Role.INSTRUCTOR
        );

        // Simula entidade persistida
        ReflectionTestUtils.setField(instructor, "id", 1L);

        course = new Course("Java", "Curso Java", instructor);
        course.setStatus(Status.BUILDING);

        // Simula entidade persistida
        ReflectionTestUtils.setField(course, "id", 1L);
    }

    @Test
    void createOpenTextTask__should_throw_when_order_is_invalid() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setOrder(0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createOpenTextTask(dto)
        );

        assertEquals("Ordem deve ser um número inteiro positivo.", exception.getMessage());
    }

    @Test
    void createOpenTextTask__should_throw_when_course_not_found() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setOrder(1);

        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createOpenTextTask(dto)
        );

        assertEquals("Curso não encontrado.", exception.getMessage());
    }

    @Test
    void createOpenTextTask__should_save_activity_when_valid() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Explique o que é Java");
        dto.setOrder(1);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(activityRepository.existsByCourseIdAndStatement(1L, dto.getStatement()))
                .thenReturn(false);

        taskService.createOpenTextTask(dto);

        verify(courseActivityService)
                .validateAndReorder(course, 1);

        verify(activityRepository)
                .save(any(CourseActivity.class));
    }

    @Test
    void createSingleChoiceTask__should_throw_when_more_than_one_correct_option() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Qual é correto?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("A", true),
                new SingleChoiceOptionDTO("B", true)
        ));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createSingleChoiceTask(dto)
        );

        assertEquals(
                "A atividade deve ter exatamente uma alternativa correta.",
                exception.getMessage()
        );
    }

    @Test
    void createSingleChoiceTask__should_save_activity_and_options_when_valid() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Qual anotação inicia o Spring Boot?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("SpringBootApplication", true),
                new SingleChoiceOptionDTO("Component", false),
                new SingleChoiceOptionDTO("Service", false)
        ));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(activityRepository.existsByCourseIdAndStatement(1L, dto.getStatement()))
                .thenReturn(false);

        taskService.createSingleChoiceTask(dto);

        verify(activityRepository).save(any(CourseActivity.class));
        verify(optionRepository, times(3)).save(any(CourseActivityOption.class));
    }

    @Test
    void createMultipleChoiceTask__should_throw_when_less_than_two_correct_options() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Escolha as corretas");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new MultipleChoiceOptionDTO("A", true),
                new MultipleChoiceOptionDTO("B", false)
        ));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createMultipleChoiceTask(dto)
        );

        assertEquals(
                "Atividade de múltipla escolha deve ter ao menos duas alternativas corretas e uma incorreta.",
                exception.getMessage()
        );
    }

    @Test
    void createMultipleChoiceTask__should_save_activity_and_options_when_valid() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Quais são estereótipos do Spring?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new MultipleChoiceOptionDTO("Component", true),
                new MultipleChoiceOptionDTO("Service", true),
                new MultipleChoiceOptionDTO("Repository", false)
        ));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(activityRepository.existsByCourseIdAndStatement(1L, dto.getStatement()))
                .thenReturn(false);

        taskService.createMultipleChoiceTask(dto);

        verify(activityRepository).save(any(CourseActivity.class));
        verify(optionRepository, times(3)).save(any(CourseActivityOption.class));
    }
}
