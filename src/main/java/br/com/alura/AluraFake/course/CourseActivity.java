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

    @Column(nullable = false)
    private String statement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(name = "activity_order", nullable = false)
    private Integer activityOrder;

    @Deprecated
    public CourseActivity() {}

    public CourseActivity(Course course, String statement, Type type, Integer activityOrder) {
        this.course = course;
        this.statement = statement;
        this.type = type;
        this.activityOrder = activityOrder;
    }

    public Type getType() {
        return type;
    }

    public Integer getOrder() {
        return activityOrder;
    }

    public void setActivityOrder(Integer activityOrder) {
        this.activityOrder = activityOrder;
    }
}
