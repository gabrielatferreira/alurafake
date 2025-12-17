package br.com.alura.AluraFake.course.instructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instructor")
public class InstructorReportController {

    private final InstructorReportService reportService;

    public InstructorReportController(InstructorReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{instructorId}/courses")
    public ResponseEntity<InstructorCoursesReportDTO> report(
            @PathVariable Long instructorId
    ) {
        InstructorCoursesReportDTO report =
                reportService.generateReport(instructorId);

        return ResponseEntity.ok(report);
    }
}

