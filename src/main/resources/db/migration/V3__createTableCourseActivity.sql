CREATE TABLE course_activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    statement VARCHAR(255) NOT NULL,
    activity_order INT NOT NULL,
    type VARCHAR(30) NOT NULL,
    created_at DATETIME,
    CONSTRAINT fk_course_activity_course
        FOREIGN KEY (course_id) REFERENCES Course(id)
);