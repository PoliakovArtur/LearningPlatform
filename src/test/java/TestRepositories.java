import db.ConnectionManager;
import db.Data;
import model.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import repository.impl.*;
import java.io.*;
import java.sql.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRepositories {


    private final static List<Teacher> expTeachers = Data.getTeachers();
    private final static List<Student> expStudents = Data.getStudents();
    private final static List<Course> expCourses = Data.getCourses();
    private final static List<Subscription> expSubscriptions = Data.getSubscriptions();
    private final static StudentRepository studentRepository = new StudentRepository();
    private final static TeacherRepository teacherRepository = new TeacherRepository();
    private final static SubscriptionRepository subscriptionRepository = new SubscriptionRepository();
    private final static CourseRepository courseRepository = new CourseRepository();
    private final static MySQLContainer<?> CONTAINER = new MySQLContainer<>("mysql:latest")
            .withInitScript("src/main/resources/learning_platform_script.sql");


    @BeforeAll
    public static void startContainer() throws SQLException, IOException {
        CONTAINER.start();
        ConnectionManager.setUrl(CONTAINER.getJdbcUrl());
        ConnectionManager.setPassword(CONTAINER.getPassword());
        ConnectionManager.setUser(CONTAINER.getUsername());
        load();
    }

    private static void load() throws SQLException, IOException {
        /*BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/learning_platform_script.sql"));
        Connection connection = ConnectionManager.getConnection();
        Statement statement = connection.createStatement();
        reader.lines().forEach(q -> {
            try {
                statement.execute(q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        reader.close();
        connection.close();*/
        expStudents.forEach(studentRepository::save);
        expTeachers.forEach(teacherRepository::save);
        expCourses.forEach(courseRepository::save);
        expSubscriptions.forEach(subscriptionRepository::save);
    }

    @Test
    @Order(1)
    public void testFindAll() {
        testFindAll(studentRepository, expStudents, Comparator.comparing(Student::getId));
        testFindAll(courseRepository, expCourses, Comparator.comparing(Course::getId));
        testFindAll(teacherRepository, expTeachers, Comparator.comparing(Teacher::getId));
        testFindAll(subscriptionRepository, expSubscriptions, Comparator.comparing(Subscription::getSubscriptionDate));
    }

    private <T, K> void testFindAll(Repository<T, K> repository, List<T> expected, Comparator<T> comparator) {
        List<T> actual = repository.findAll();
        actual.sort(comparator);
        assertEquals(expected, actual);
    }

    @Test
    @Order(2)
    public void testFindById() {
        testFindById(studentRepository, expStudents, Student::getId);
        testFindById(teacherRepository, expTeachers, Teacher::getId);
        testFindById(courseRepository, expCourses, Course::getId);
    }

    private <T, K> void testFindById(Repository<T, K> repository, List<T> expected, Function<T, K> idGetter) {
        for(T entity : expected) {
            K key = idGetter.apply(entity);
            T actual = repository.findById(key).get();
            assertEquals(entity, actual);
        }
    }

    @Test
    @Order(3)
    public void testUpdate() {
        Student student = expStudents.get(4);
        student.setName("Валиев Антон");
        student.setAge(87);
        expStudents.set(4, student);

        Course course = expCourses.get(2);
        course.setPrice(2000000L);
        expCourses.set(2, course);

        Teacher teacher = expTeachers.get(3);
        teacher.setName("Порогов Анатолий");
        expTeachers.set(3, teacher);

        int lastSubscription = expSubscriptions.size() - 1;
        Subscription subscription = expSubscriptions.get(lastSubscription);
        subscription.setSubscriptionDate(Date.valueOf("2022-12-22"));
        expSubscriptions.set(lastSubscription, subscription);

        testUpdate(studentRepository, student, Student::getId);
        testUpdate(teacherRepository, teacher, Teacher::getId);
        testUpdate(courseRepository, course, Course::getId);
        testUpdate(subscriptionRepository, subscription, s -> Map.entry(s.getStudent().getId(), s.getCourse().getId()));
    }

    private <T, K> void testUpdate(Repository<T, K> repository, T expected, Function<T, K> idGetter) {
        K id = idGetter.apply(expected);
        repository.update(expected);
        T actual = repository.findById(id).get();
        assertEquals(expected, actual);
    }

    @Test
    @Order(4)
    public void testSave() {
        Student student = new Student(23L, "Совсем новый студент", 34, Date.valueOf("2022-12-22"));
        Teacher teacher = new Teacher(8L, "Совсем новый учитель", 45000L, 45);
        Course course = new Course(12L, "Совсем новый курс", CourseType.MANAGEMENT, "Представляем вам совсем новый курс", teacher, 5_000_000L);
        Subscription subscription = new Subscription(student, course, Date.valueOf("2022-12-22"));
        expStudents.add(student); expCourses.add(course); expSubscriptions.add(subscription); expTeachers.add(teacher);
        testSave(studentRepository, student, Student::getId);
        testSave(teacherRepository, teacher, Teacher::getId);
        testSave(courseRepository, course, Course::getId);
        testSave(subscriptionRepository, subscription, s -> Map.entry(s.getStudent().getId(), s.getCourse().getId()));
    }

    private <T,K> void testSave(Repository<T, K> repository, T entity, Function<T, K> idGetter) {
        repository.save(entity);
        T addedEntity = repository.findById(idGetter.apply(entity)).get();
        System.out.println(entity.equals(addedEntity));
        assertEquals(entity, addedEntity);
    }

    @Test
    @Order(5)
    public void testDelete() {
        Student student = expStudents.get(expStudents.size() - 1);
        Course course = expCourses.get(expCourses.size() - 1);
        Teacher teacher = expTeachers.get(expTeachers.size() - 1);
        Subscription subscription = expSubscriptions.get(expSubscriptions.size() - 1);
        testDelete(studentRepository, student.getId(), 1000L);
        testDelete(courseRepository, course.getId(), 1000L);
        testDelete(teacherRepository, teacher.getId(), 1000L);
    }

    private <T,K> void testDelete(Repository<T, K> repository, K existsId, K notExistsId) {
        assertTrue(repository.deleteById(existsId));
        assertThrows(NullPointerException.class, () -> repository.findById(existsId));
        assertFalse(repository.deleteById(notExistsId));
    }

    @AfterAll
    public static void stopContainer() {
        System.out.println("stop");
        CONTAINER.stop();
    }
}