package br.com.alura.AluraFake.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseActivityService {

    private final CourseActivityRepository activityRepository;

    public CourseActivityService(CourseActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void validateAndReorder(Course course, Integer newOrder) {

        Integer maxOrder = activityRepository.findMaxOrderByCourseId(course.getId());
        if (maxOrder == null) {
            maxOrder = 0;
        }

        // Não permitir saltos
        if (newOrder > maxOrder + 1) {
            throw new IllegalArgumentException(
                    "Ordem inválida. A sequência de atividades está incorreta."
            );
        }

        // Deslocar atividades
        List<CourseActivity> toShift =
                activityRepository
                        .findByCourseIdAndActivityOrderGreaterThanEqualOrderByActivityOrderDesc(
                                course.getId(),
                                newOrder
                        );

        for (CourseActivity activity : toShift) {
            activity.setActivityOrder(activity.getOrder() + 1);
        }
    }
}
