package servlets;

import model.Subscription;
import model.Teacher;
import repository.impl.CourseRepository;
import repository.impl.StudentRepository;
import repository.impl.SubscriptionRepository;
import repository.impl.TeacherRepository;
import service.impl.CourseService;
import service.impl.StudentService;
import service.impl.SubscriptionService;
import service.impl.TeacherService;

public class ServiceFactory {
    private ServiceFactory() {}

    public static CourseService courseService(TeacherService teacherService) {
        CourseRepository courseRepository = new CourseRepository();
        return new CourseService(courseRepository, teacherService);
    }

    public static TeacherService teacherService() {
        TeacherRepository teacherRepository = new TeacherRepository();
        return new TeacherService(teacherRepository);
    }

    public static StudentService studentService() {
        StudentRepository studentRepository = new StudentRepository();
        return new StudentService(studentRepository);
    }

    public static SubscriptionService subscriptionService(StudentService studentService, CourseService courseService) {
        SubscriptionRepository subscriptionRepository = new SubscriptionRepository();
        return new SubscriptionService(subscriptionRepository, studentService, courseService);
    }
}
