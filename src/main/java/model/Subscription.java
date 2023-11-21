package model;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.Objects;

public class Subscription {
    private Student student;
    private Course course;
    private Date subscriptionDate;

    public Subscription() {}

    public Subscription(Student student, Course course, Date subscriptionDate) {
        this.student = student;
        this.course = course;
        this.subscriptionDate = subscriptionDate;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription that)) return false;
        return Objects.equals(getStudent(), that.getStudent()) && Objects.equals(getCourse(), that.getCourse()) && Objects.equals(getSubscriptionDate(), that.getSubscriptionDate());
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "student=" + student.getId() +
                ", course=" + course.getId() +
                ", subscriptionDate=" + subscriptionDate +
                '}';
    }
}
