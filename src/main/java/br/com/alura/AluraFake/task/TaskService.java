package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.*;
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

    // 1.1 — Atividade de Resposta Aberta
    public void createOpenTextTask(NewOpenTextTaskDTO request) {

        // A ordem deve ser um número inteiro positivo.
        if (request.getOrder() == null || request.getOrder() <= 0) {
            throw new IllegalArgumentException("Ordem deve ser um número inteiro positivo.");
        }

        // Um curso só pode receber atividades se seu status for BULDING.
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Curso não está em BUILDING.");
        }

        // O curso não pode ter duas questões com o mesmo enunciado
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

    //1.2 — Atividade de alternativa única
    public void createSingleChoiceTask(NewSingleChoiceTaskDTO request) {

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Curso não está em BUILDING.");
        }

        // Regra: não pode repetir enunciado no mesmo curso
        if (activityRepository.existsByCourseIdAndStatement(
                course.getId(), request.getStatement())) {
            throw new IllegalArgumentException(
                    "Já existe uma questão com esse enunciado para este curso."
            );
        }

        List<SingleChoiceOptionDTO> options = request.getOptions();

        // Regra: mínimo 2 e máximo 5 alternativas
        if (options.size() < 2 || options.size() > 5) {
            throw new IllegalArgumentException(
                    "A atividade deve ter no mínimo 2 e no máximo 5 alternativas."
            );
        }

        // Regra: exatamente uma correta
        long correctCount = options.stream()
                .filter(SingleChoiceOptionDTO::getIsCorrect)
                .count();

        if (correctCount != 1) {
            throw new IllegalArgumentException(
                    "A atividade deve ter exatamente uma alternativa correta."
            );
        }

        // Regra: alternativas não podem ser iguais entre si
        Set<String> uniqueOptions = options.stream()
                .map(o -> o.getOption().trim().toLowerCase())
                .collect(Collectors.toSet());

        if (uniqueOptions.size() != options.size()) {
            throw new IllegalArgumentException(
                    "As alternativas não podem ser iguais entre si."
            );
        }

        // Regra: alternativa não pode ser igual ao enunciado
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

    //1.3 — Atividade de múltipla escolha
    public void createMultipleChoiceTask(NewMultipleChoiceTaskDTO request) {

        // Ordem válida
        if (request.getOrder() == null || request.getOrder() <= 0) {
            throw new IllegalArgumentException("Ordem deve ser um número inteiro positivo.");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado."));

        // Curso deve estar em BUILDING
        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Curso não está em BUILDING.");
        }

        // Enunciado não pode se repetir
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

        // Deve ter duas ou mais corretas e ao menos uma incorreta
        if (correctCount < 2 || incorrectCount < 1) {
            throw new IllegalArgumentException(
                    "Atividade de múltipla escolha deve ter ao menos duas alternativas corretas e uma incorreta."
            );
        }

        // Alternativas não podem ser iguais entre si
        Set<String> uniqueOptions = options.stream()
                .map(o -> o.getOption().trim().toLowerCase())
                .collect(Collectors.toSet());

        if (uniqueOptions.size() != options.size()) {
            throw new IllegalArgumentException("As alternativas não podem ser iguais entre si.");
        }

        // Alternativas não podem ser iguais ao enunciado
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


