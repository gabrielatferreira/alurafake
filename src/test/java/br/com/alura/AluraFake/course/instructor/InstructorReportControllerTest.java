package br.com.alura.AluraFake.course.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstructorReportController.class)
class InstructorReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstructorReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void instructorCourses__should_return_404_when_instructor_is_not_found() throws Exception {
        when(reportService.generateReport(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found"));

        mockMvc.perform(get("/instructor/1/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$[0].field").value("global"))
                .andExpect(jsonPath("$[0].message").value("Instructor not found"));
    }


    @Test
    void instructorCourses__should_return_400_when_user_is_not_instructor() throws Exception {
        when(reportService.generateReport(2L))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an instructor"));

        mockMvc.perform(get("/instructor/2/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("global"))
                .andExpect(jsonPath("$[0].message").value("User is not an instructor"));
    }

    @Test
    void instructorCourses__should_return_empty_list_when_instructor_has_no_courses() throws Exception {
        InstructorCoursesReportDTO emptyReport = new InstructorCoursesReportDTO(List.of(), 0L);
        when(reportService.generateReport(3L)).thenReturn(emptyReport);

        mockMvc.perform(get("/instructor/3/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses.length()").value(0))
                .andExpect(jsonPath("$.totalPublishedCourses").value(0));
    }

    @Test
    void instructorCourses__should_return_courses_report_when_instructor_has_courses() throws Exception {
        User paulo = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
        Course java = new Course("Java", "Curso de Java", paulo);
        Course spring = new Course("Spring", "Curso de Spring", paulo);

        InstructorCourseItemDTO javaDTO = new InstructorCourseItemDTO(java, 3L);
        InstructorCourseItemDTO springDTO = new InstructorCourseItemDTO(spring, 2L);
        InstructorCoursesReportDTO reportDTO = new InstructorCoursesReportDTO(List.of(javaDTO, springDTO), 2L);

        when(reportService.generateReport(4L)).thenReturn(reportDTO);

        mockMvc.perform(get("/instructor/4/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses[0].title").value("Java"))
                .andExpect(jsonPath("$.courses[0].activitiesCount").value(3))
                .andExpect(jsonPath("$.courses[1].title").value("Spring"))
                .andExpect(jsonPath("$.courses[1].activitiesCount").value(2))
                .andExpect(jsonPath("$.totalPublishedCourses").value(2));
    }
}
