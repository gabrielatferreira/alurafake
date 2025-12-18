package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.course.activity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final CourseRepository courseRepository;
    private final CourseActivityRepository activityRepository;
    private final CourseActivityOptionRepository optionRepository;
    private final CourseActivityService courseActivityService;

    public TaskService(CourseRepository courseRepository,
                       CourseActivityRepository activityRepository, CourseActivityOptionRepository optionRepository, CourseActivityService courseActivityService) {
        this.courseRepository = courseRepository;
        this.activityRepository = activityRepository;
        this.optionRepository = optionRepository;
        this.courseActivityService = courseActivityService;
    }

    public void createOpenTextTask(NewOpenTextTaskDTO request) {

        if (request.getOrder() == null || request.getOrder() <= 0) {
            throw new IllegalArgumentException("Ordem deve ser um número inteiro positivo.");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Curso não está em BUILDING.");
        }

        boolean alreadyExists =
                activityRepository.existsByCourseIdAndStatement(
                        course.getId(),
                        request.getStatement()
                );

        if (alreadyExists) {
            throw new IllegalArgumentException(
                    "Já existe uma questão com esse enunciado para este curso."
            );
        }

        courseActivityService.validateAndReorder(course, request.getOrder());

        CourseActivity activity = new CourseActivity(
                course,
                request.getStatement(),
                Type.OPEN_TEXT,
                request.getOrder()
        );

        activityRepository.save(activity);
    }

    public void createSingleChoiceTask(NewSingleChoiceTaskDTO request) {

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Curso não está em BUILDING.");
        }

        if (activityRepository.existsByCourseIdAndStatement(
                course.getId(), request.getStatement())) {
            throw new IllegalArgumentException(
                    "Já existe uma questão com esse enunciado para este curso."
            );
        }

        List<SingleChoiceOptionDTO> options = request.getOptions();

        if (options.size() < 2 || options.size() > 5) {
            throw new IllegalArgumentException(
                    "A atividade deve ter no mínimo 2 e no máximo 5 alternativas."
            );
        }

        long correctCount = options.stream()
                .filter(SingleChoiceOptionDTO::getIsCorrect)
                .count();

        if (correctCount != 1) {
            throw new IllegalArgumentException(
                    "A atividade deve ter exatamente uma alternativa correta."
            );
        }

        Set<String> uniqueOptions = options.stream()
                .map(o -> o.getOption().trim().toLowerCase())
                .collect(Collectors.toSet());

        if (uniqueOptions.size() != options.size()) {
            throw new IllegalArgumentException(
                    "As alternativas não podem ser iguais entre si."
            );
        }

        String statementNormalized = request.getStatement().trim().toLowerCase();

        boolean equalsStatement = options.stream()
                .anyMatch(o -> o.getOption().trim().toLowerCase()
                        .equals(statementNormalized));

        if (equalsStatement) {
            throw new IllegalArgumentException(
                    "As alternativas não podem ser iguais ao enunciado da atividade."
            );
        }

        courseActivityService.validateAndReorder(course, request.getOrder());

        CourseActivity activity = new CourseActivity(
                course,
                request.getStatement(),
                Type.SINGLE_CHOICE,
                request.getOrder()
        );

        activityRepository.save(activity);

        for (SingleChoiceOptionDTO optionDTO : request.getOptions()) {
            CourseActivityOption option = new CourseActivityOption(
                    activity,
                    optionDTO.getOption(),
                    optionDTO.getIsCorrect()
            );
            optionRepository.save(option);
        }
    }

    public void createMultipleChoiceTask(NewMultipleChoiceTaskDTO request) {

        if (request.getOrder() == null || request.getOrder() <= 0) {
            throw new IllegalArgumentException("Ordem deve ser um número inteiro positivo.");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Curso não está em BUILDING.");
        }

        boolean exists = activityRepository.existsByCourseIdAndStatement(
                course.getId(),
                request.getStatement()
        );

        if (exists) {
            throw new IllegalArgumentException(
                    "Já existe uma questão com esse enunciado para este curso."
            );
        }

        List<MultipleChoiceOptionDTO> options = request.getOptions();

        long correctCount = options.stream()
                .filter(MultipleChoiceOptionDTO::getIsCorrect)
                .count();

        long incorrectCount = options.size() - correctCount;

        if (correctCount < 2 || incorrectCount < 1) {
            throw new IllegalArgumentException(
                    "Atividade de múltipla escolha deve ter ao menos duas alternativas corretas e uma incorreta."
            );
        }

        Set<String> uniqueOptions = options.stream()
                .map(o -> o.getOption().trim().toLowerCase())
                .collect(Collectors.toSet());

        if (uniqueOptions.size() != options.size()) {
            throw new IllegalArgumentException("As alternativas não podem ser iguais entre si.");
        }

        boolean equalsStatement = options.stream()
                .anyMatch(o -> o.getOption().equalsIgnoreCase(request.getStatement()));

        if (equalsStatement) {
            throw new IllegalArgumentException(
                    "As alternativas não podem ser iguais ao enunciado da atividade."
            );
        }

        courseActivityService.validateAndReorder(course, request.getOrder());

        CourseActivity activity = new CourseActivity(
                course,
                request.getStatement(),
                Type.MULTIPLE_CHOICE,
                request.getOrder()
        );

        activityRepository.save(activity);

        for (MultipleChoiceOptionDTO dto : options) {
            CourseActivityOption option = new CourseActivityOption(
                    activity,
                    dto.getOption(),
                    dto.getIsCorrect()
            );
            optionRepository.save(option);
        }
    }
}


