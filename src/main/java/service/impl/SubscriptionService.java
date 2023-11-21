package service.impl;

import model.Course;
import model.Student;
import model.Subscription;
import repository.impl.CourseRepository;
import repository.impl.StudentRepository;
import repository.impl.SubscriptionRepository;
import service.Service;

import java.util.List;
import java.util.Map;

public class SubscriptionService implements Service<Subscription, Map.Entry<Long, Long>> {
    private SubscriptionRepository subscriptionRepository;
    private StudentService studentService;
    private CourseService courseService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, StudentService studentService, CourseService courseService) {
        this.subscriptionRepository = subscriptionRepository;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @Override
    public Subscription findById(Map.Entry<Long, Long> id) {
        throw new UnsupportedOperationException("can't find subscription by id");
    }

    @Override
    public boolean deleteById(Map.Entry<Long, Long> id) {
        throw new UnsupportedOperationException("can't delete subscription by id");
    }

    @Override
    public List<Subscription> findAll() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Subscription save(Subscription subscription) {
        Student student = subscription.getStudent();
        Course course = subscription.getCourse();

        if(student == null || course == null
        || subscription.getSubscriptionDate() == null
        || student.getId() == null
        || course.getId() == null) {
            throw new ServiceException("not enough columns to save subscription");
        }
        studentService.findById(student.getId());
        courseService.findById(course.getId());
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription update(Subscription subscription) {
        Student student = subscription.getStudent();
        Course course = subscription.getCourse();
        if(student == null || course == null
        || student.getId() == null || course.getId() == null) {
            throw new ServiceException("can't update subscriptions without course_id and student_id");
        }
        if(student.getRegistrationDate() == null) throw new ServiceException("need to set at least one column to update teacher");
        return subscriptionRepository.update(subscription);
    }
}
