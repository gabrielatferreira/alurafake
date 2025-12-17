package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.activity.CourseActivity;
import br.com.alura.AluraFake.course.activity.CourseActivityRepository;
import br.com.alura.AluraFake.task.Type;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseActivityRepository courseActivityRepository;

    public CourseService(CourseRepository courseRepository, CourseActivityRepository courseActivityRepository) {
        this.courseRepository = courseRepository;
        this.courseActivityRepository = courseActivityRepository;
    }

    @Transactional
    public void publish(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso não encontrado"));

        if (course.getStatus() != Status.BUILDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O curso não pode ser publicado, pois não está em BUILDING.");
        }

        List<CourseActivity> activities = courseActivityRepository.findByCourse(course);

        if (activities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O curso não pode ser publicado, pois não há atividades.");
        }

        Set<Type> existingTypes = activities.stream().map(CourseActivity::getType).collect(Collectors.toSet());

        if (existingTypes.size() < Type.values().length) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O curso não pode ser publicado, pois deve ter ao menos uma atividade de cada tipo.");
        }

        List<Integer> orders = activities.stream()
                .map(CourseActivity::getActivityOrder)
                .sorted()
                .toList();

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i) != i + 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O curso não pode ser publicado, pois a ordem das atividades não está contínua. " +
                                "Esperado ordem " + (i + 1) + " mas encontrado " + orders.get(i)
                );
            }
        }

        course.updateStatus();
        courseRepository.save(course);
    }
}
