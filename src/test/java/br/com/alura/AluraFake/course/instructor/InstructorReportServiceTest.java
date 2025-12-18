package br.com.alura.AluraFake.course.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.course.activity.CourseActivityRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructorReportServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseActivityRepository activityRepository;

    @InjectMocks
    private InstructorReportService instructorReportService;

    private User instructor;

    @BeforeEach
    void setup() {
        instructor = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
    }

    @Test
    void generateReport__should_throw_404_when_instructor_not_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> instructorReportService.generateReport(1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Usuário não encontrado", exception.getReason());
    }

    @Test
    void generateReport__should_throw_400_when_user_is_not_instructor() {
        User student = new User("Aluno", "aluno@alura.com.br", Role.STUDENT);

        when(userRepository.findById(2L)).thenReturn(Optional.of(student));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> instructorReportService.generateReport(2L)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Usuário não é um instrutor", exception.getReason());
    }

    @Test
    void generateReport__should_return_empty_report_when_instructor_has_no_courses() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(instructor));
        when(courseRepository.findByInstructor(instructor)).thenReturn(List.of());
        when(courseRepository.countByInstructorAndStatus(instructor, Status.PUBLISHED))
                .thenReturn(0L);

        InstructorCoursesReportDTO report =
                instructorReportService.generateReport(3L);

        assertNotNull(report);
        assertTrue(report.getCourses().isEmpty());
        assertEquals(0L, report.getTotalPublishedCourses());
    }

    @Test
    void generateReport__should_return_courses_with_activity_count_and_published_total() {
        when(userRepository.findById(4L)).thenReturn(Optional.of(instructor));

        Course java = new Course("Java", "Curso Java", instructor);
        Course spring = new Course("Spring", "Curso Spring", instructor);

        when(courseRepository.findByInstructor(instructor))
                .thenReturn(List.of(java, spring));

        when(activityRepository.countByCourse(java)).thenReturn(3L);
        when(activityRepository.countByCourse(spring)).thenReturn(2L);

        when(courseRepository.countByInstructorAndStatus(instructor, Status.PUBLISHED))
                .thenReturn(1L);

        InstructorCoursesReportDTO report =
                instructorReportService.generateReport(4L);

        assertEquals(2, report.getCourses().size());
        assertEquals("Java", report.getCourses().get(0).getTitle());
        assertEquals(3L, report.getCourses().get(0).getActivitiesCount());
        assertEquals("Spring", report.getCourses().get(1).getTitle());
        assertEquals(2L, report.getCourses().get(1).getActivitiesCount());
        assertEquals(1L, report.getTotalPublishedCourses());
    }
}
