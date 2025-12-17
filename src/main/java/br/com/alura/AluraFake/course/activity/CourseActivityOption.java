package br.com.alura.AluraFake.course.activity;

import jakarta.persistence.*;

@Entity
@Table(name = "course_activity_option")
public class CourseActivityOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private CourseActivity activityId;

    @Column(name = "option_text",nullable = false, length = 80)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean correct;

    @Deprecated
    public CourseActivityOption() {}

    public CourseActivityOption(CourseActivity activityId, String optionText, Boolean correct) {
        this.activityId = activityId;
        this.optionText = optionText;
        this.correct = correct;
    }
}

