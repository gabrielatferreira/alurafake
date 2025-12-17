package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Type;
import jakarta.persistence.*;

@Entity
@Table(name = "course_activity")
public class CourseActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(name = "activity_order", nullable = false)
    private Integer activityOrder;

    @Deprecated
    public CourseActivity() {}

    public CourseActivity(Course course, Type type, Integer order) {
        this.course = course;
        this.type = type;
        this.activityOrder = order;
    }

    public CourseActivity(Course course, Type type, String statement, Integer integer) {
    }

    public Type getType() {
        return type;
    }

    public Integer getOrder() {
        return activityOrder;
    }
}
