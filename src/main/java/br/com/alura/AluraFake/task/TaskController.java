package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/task/new/opentext")
    public ResponseEntity<Void> newOpenTextExercise(
            @RequestBody @Valid NewOpenTextTaskDTO dto) {
        taskService.createOpenTextTask(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity<Void> newSingleChoice(
            @RequestBody @Valid NewSingleChoiceTaskDTO dto) {
        taskService.createSingleChoiceTask(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity<Void> newMultipleChoice(@RequestBody @Valid NewMultipleChoiceTaskDTO request) {
        taskService.createMultipleChoiceTask(request);
        return ResponseEntity.ok().build();
    }
}