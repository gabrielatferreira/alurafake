package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void newOpenTextExercise__should_return_ok_when_request_is_valid() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Explique o que é Spring Boot");
        dto.setOrder(1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(taskService).createOpenTextTask(any(NewOpenTextTaskDTO.class));
    }

    @Test
    void newSingleChoice__should_return_ok_when_request_is_valid() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Qual anotação inicia uma aplicação Spring Boot?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Component", true),
                new SingleChoiceOptionDTO("Configuration",false),
                new SingleChoiceOptionDTO("SpringBootApplication", false)
        ));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(taskService, times(1))
                .createSingleChoiceTask(any(NewSingleChoiceTaskDTO.class));
    }

    @Test
    void newMultipleChoice__should_return_ok_when_request_is_valid() throws Exception {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Quais são estereótipos do Spring?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new MultipleChoiceOptionDTO("Component",true),
                new MultipleChoiceOptionDTO("Service", false),
                new MultipleChoiceOptionDTO("Repository",true)
        ));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(taskService).createMultipleChoiceTask(any(NewMultipleChoiceTaskDTO.class));
    }
}
