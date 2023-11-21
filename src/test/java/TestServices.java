import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.impl.*;
import service.Service;
import service.impl.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static db.Data.*;

public class TestServices {
    private static CourseService courseService;
    private static StudentService studentService;
    private static TeacherService teacherService;
    private static SubscriptionService subscriptionService;
    private static CourseRepository courseRepository;
    private static StudentRepository studentRepository;
    private static TeacherRepository teacherRepository;
    private static SubscriptionRepository subscriptionRepository;

    @BeforeAll
    public static void setUp() {
        courseRepository = mock(CourseRepository.class);
        studentRepository = mock(StudentRepository.class);
        teacherRepository = mock(TeacherRepository.class);
        subscriptionRepository = mock(SubscriptionRepository.class);
        teacherService = new TeacherService(teacherRepository);
        courseService = new CourseService(courseRepository, teacherService);
        studentService = new StudentService(studentRepository);
        subscriptionService = new SubscriptionService(subscriptionRepository, studentService, courseService);
    }

    @Test
    public void testFindById() {
        testFindById(teacherService, teacherRepository, new Teacher());
        testFindById(studentService, studentRepository, new Student());
        testFindById(courseService, courseRepository, new Course());
    }

    private static <T>  void testFindById(Service<T,Long> service, Repository<T, Long> tkRepository, T entity) {
        when(tkRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(tkRepository.findById(2L)).thenReturn(Optional.empty());
        T findedEntity = service.findById(1L);
        assertEquals(entity, findedEntity);
        assertThrows(ServiceException.class, () -> service.findById(2L));
    }

    @Test
    public void testDeleteById() {
        testDeleteById(teacherService, teacherRepository, 1L, 2L, new Teacher());
        testDeleteById(studentService, studentRepository, 1L, 2L, new Student());
        testDeleteById(courseService, courseRepository, 1L, 2L, new Course());
    }

    private static <T,K>  void testDeleteById(Service<T,K> service, Repository<T, K> tkRepository, K existKey, K notExistKey, T entity) {
        when(tkRepository.deleteById(existKey)).thenReturn(true);
        when(tkRepository.deleteById(notExistKey)).thenReturn(false);
        assertTrue(service.deleteById(existKey));
        assertFalse(service.deleteById(notExistKey));
    }

    @Test
    public void testFindAll() {
        testFindAll(studentService, studentRepository, getStudents());
        testFindAll(teacherService, teacherRepository, getTeachers());
        testFindAll(courseService, courseRepository, getCourses());
        testFindAll(subscriptionService, subscriptionRepository, getSubscriptions());
    }

    private <T,K> void testFindAll(Service<T,K> service, Repository<T, K> tkRepository, List<T> entities) {
        when(tkRepository.findAll()).thenReturn(entities);
        assertEquals(entities, service.findAll());
    }

    @Test
    public void testSaveStudent() {
        Student fullStudent = getStudents().get(0);
        Student notFullStudent = new Student(null, "name", null, null);
        testSave(studentService, studentRepository, new Student(), fullStudent, notFullStudent);
    }

    @Test
    public void testSaveCourse() {
        Course fullCourse =  getCourses().get(0);
        Teacher teacher = fullCourse.getTeacher();
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        Course notFullCourse = new Course(null, CourseType.DESIGN, null, null, null);
        testSave(courseService, courseRepository, new Course(), fullCourse, notFullCourse);
    }

    @Test
    public void testSaveTeacher() {
        Teacher fullTeacher = getTeachers().get(0);
        Teacher notFullTeacher = new Teacher("name", null, null);
        testSave(teacherService, teacherRepository, new Teacher(), fullTeacher, notFullTeacher);
    }

    @Test
    public void testSaveSubscription() {
        Subscription fullSubscription = getSubscriptions().get(0);
        Student student = fullSubscription.getStudent();
        Course course = fullSubscription.getCourse();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        Subscription notFullSubscription = new Subscription(null, new Course(), null);
        testSave(subscriptionService, subscriptionRepository, new Subscription(), getSubscriptions().get(0), notFullSubscription);
    }

    private <T,K> void testSave(Service<T,K> service, Repository<T, K> repository, T emptyEntity, T fullEntity, T notFullEntity) {
        assertThrows(ServiceException.class, () -> service.save(emptyEntity));
        assertThrows(ServiceException.class, () -> service.save(notFullEntity));
        service.save(fullEntity);
        verify(repository).save(fullEntity);

    }

    @Test
    public void testUpdateStudent() {
        Student withoutId = new Student("name", 32, new Date(System.currentTimeMillis()));
        Student withoutColumns = new Student(2L, null, null, null);
        Student valid = getStudents().get(0);
        testUpdate(studentService, studentRepository, withoutId, withoutColumns, valid);
    }

    @Test
    public void testUpdateTeacher() {
        Teacher withoutId = new Teacher("name", 33000L, 33);
        Teacher withoutColumns = new Teacher(2L, null, null, null);
        Teacher valid = getTeachers().get(0);
        testUpdate(teacherService, teacherRepository, withoutId, withoutColumns, valid);
    }

    @Test
    public void testUpdateCourse() {
        Course withoutId = new Course("name", null, null, null, null);
        Course withoutColumns = new Course(2L, null, null, null, null, null);
        Course valid = getCourses().get(0);
        testUpdate(courseService, courseRepository, withoutId, withoutColumns, valid);
    }

    @Test
    public void testUpdateSubscription() {
        Subscription withoutId = new Subscription();
        Student student = new Student();
        Course course = new Course();
        student.setId(1L);
        course.setId(1L);
        Subscription withoutColumns = new Subscription(student, course, null);
        Subscription valid = getSubscriptions().get(0);
        testUpdate(subscriptionService, subscriptionRepository, withoutId, withoutColumns, valid);
    }

    private <T,K> void testUpdate(Service<T,K> service, Repository<T,K> repository, T withoutId, T withoutColumns, T valid) {
        assertThrows(ServiceException.class, () -> service.update(withoutId));
        assertThrows(ServiceException.class, () -> service.update(withoutColumns));
        service.update(valid);
        verify(repository).update(valid);
    }

}
