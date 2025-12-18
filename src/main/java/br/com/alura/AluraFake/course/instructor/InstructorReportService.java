package br.com.alura.AluraFake.course.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.course.activity.CourseActivityRepository;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InstructorReportService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseActivityRepository activityRepository;

    public InstructorReportService(
            UserRepository userRepository,
            CourseRepository courseRepository,
            CourseActivityRepository activityRepository
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.activityRepository = activityRepository;
    }

    public InstructorCoursesReportDTO generateReport(Long instructorId) {

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuário não encontrado"
                        )
                );

        if (!instructor.isInstructor()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Usuário não é um instrutor"
            );
        }

        List<Course> courses = courseRepository.findByInstructor(instructor);

        List<InstructorCourseItemDTO> courseItems =
                courses.stream()
                        .map(course -> new InstructorCourseItemDTO(
                                course,
                                activityRepository.countByCourse(course)
                        ))
                        .toList();

        long publishedCount =
                courseRepository.countByInstructorAndStatus(
                        instructor,
                        Status.PUBLISHED
                );

        return new InstructorCoursesReportDTO(
                courseItems,
                publishedCount
        );
    }
}

