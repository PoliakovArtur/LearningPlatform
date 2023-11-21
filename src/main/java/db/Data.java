package db;

import model.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Data {
    private Data() {}

    private static List<Teacher> teachers;
    private static List<Course> courses;
    private static List<Student> students;
    private static List<Subscription> subscriptions;

    public static List<Teacher> getTeachers() {
        if (teachers == null) setTeachers();
        return teachers;
    }

    public static List<Course> getCourses() {
        if (courses == null) setCourses();
        return courses;
    }

    public static List<Student> getStudents() {
        if (students == null) setStudents();
        return students;
    }

    public static List<Subscription> getSubscriptions() {
        if (subscriptions == null) setSubscriptions();
        return subscriptions;
    }

    private static void setSubscriptions() {
        if (courses == null) setCourses();
        if (students == null) setStudents();
        subscriptions = new ArrayList<>();
        subscriptions.add(new Subscription(students.get(0), courses.get(0), Date.valueOf("2018-01-01")));
        subscriptions.add(new Subscription(students.get(1), courses.get(0), Date.valueOf("2018-04-11")));
        subscriptions.add(new Subscription(students.get(2), courses.get(1), Date.valueOf("2018-01-02")));
        subscriptions.add(new Subscription(students.get(3), courses.get(1), Date.valueOf("2018-04-12")));
        subscriptions.add(new Subscription(students.get(4), courses.get(2), Date.valueOf("2018-01-03")));
        subscriptions.add(new Subscription(students.get(5), courses.get(2), Date.valueOf("2018-04-13")));
        subscriptions.add(new Subscription(students.get(6), courses.get(3), Date.valueOf("2018-01-04")));
        subscriptions.add(new Subscription(students.get(7), courses.get(3), Date.valueOf("2018-04-14")));
        subscriptions.add(new Subscription(students.get(8), courses.get(4), Date.valueOf("2018-01-05")));
        subscriptions.add(new Subscription(students.get(9), courses.get(4), Date.valueOf("2018-04-15")));
        subscriptions.add(new Subscription(students.get(10), courses.get(5), Date.valueOf("2018-01-06")));
        subscriptions.add(new Subscription(students.get(11), courses.get(5), Date.valueOf("2018-04-16")));
        subscriptions.add(new Subscription(students.get(12), courses.get(6), Date.valueOf("2018-01-07")));
        subscriptions.add(new Subscription(students.get(13), courses.get(6), Date.valueOf("2018-04-17")));
        subscriptions.add(new Subscription(students.get(14), courses.get(7), Date.valueOf("2018-01-08")));
        subscriptions.add(new Subscription(students.get(15), courses.get(7), Date.valueOf("2018-04-18")));
        subscriptions.add(new Subscription(students.get(16), courses.get(8), Date.valueOf("2018-01-09")));
        subscriptions.add(new Subscription(students.get(17), courses.get(8), Date.valueOf("2018-04-19")));
        subscriptions.add(new Subscription(students.get(18), courses.get(9), Date.valueOf("2018-01-10")));
        subscriptions.add(new Subscription(students.get(19), courses.get(9), Date.valueOf("2018-04-20")));
        subscriptions.add(new Subscription(students.get(20), courses.get(10), Date.valueOf("2018-01-11")));
        subscriptions.add(new Subscription(students.get(21), courses.get(10), Date.valueOf("2018-04-21")));
        subscriptions.add(new Subscription(students.get(21), courses.get(5), Date.valueOf("2018-01-12")));
        subscriptions.sort(Comparator.comparing(Subscription::getSubscriptionDate));
    }

    private static void setTeachers() {
        teachers = new ArrayList<>();
        teachers.add(new Teacher(1L, "Ягешев Сидор", 10000L, 18));
        teachers.add(new Teacher(2L, "Яглинцев Владислав", 20000L, 19));
        teachers.add(new Teacher(3L, "Апалков Ростислав", 30000L, 20));
        teachers.add(new Teacher(4L, "Колиух Вацлав", 5000L, 21));
        teachers.add(new Teacher(5L, "Ядренкин Елисей", 2000L, 22));
        teachers.add(new Teacher(6L, "Шерков Артемий", 3000L, 23));
        teachers.add(new Teacher(7L, "Долбанов Виктор", 2000L, 20));
    }

    private static void setCourses() {
        if (teachers == null) setTeachers();
        courses = new ArrayList<>();
        courses.add(new Course(1L, "Веб-разработчик c 0 до PRO", CourseType.PROGRAMMING, "Представляем вашему вниманию шикарный курс Веб-разработчик c 0 до PRO", teachers.get(0), 189600L));
        courses.add(new Course(2L, "Мобильный разработчик с нуля", CourseType.PROGRAMMING, "Представляем вашему вниманию шикарный курс Мобильный разработчик с нуля", teachers.get(0), 138000L));
        courses.add(new Course(3L, "Java-разработчик", CourseType.PROGRAMMING, "Представляем вашему вниманию шикарный курс Java-разработчик", teachers.get(2), 78000L));
        courses.add(new Course(4L, "PHP-разработчик с 0 до PRO", CourseType.PROGRAMMING, "Представляем вашему вниманию шикарный курс PHP-разработчик с 0 до PRO", teachers.get(3), 40000L));
        courses.add(new Course(5L, "Python-разработчик с нуля", CourseType.PROGRAMMING, "Представляем вашему вниманию шикарный курс Python-разработчик с нуля", teachers.get(1), 60000L));
        courses.add(new Course(6L, "Анимация интерфейсов", CourseType.DESIGN, "Представляем вашему вниманию шикарный курс Анимация интерфейсов", teachers.get(4), 85500L));
        courses.add(new Course(7L, "UX-дизайн", CourseType.DESIGN, "Представляем вашему вниманию шикарный курс UX-дизайн", teachers.get(4), 42750L));
        courses.add(new Course(8L, "SMM-маркетолог от А до Я", CourseType.MARKETING, "Представляем вашему вниманию шикарный курс SMM-маркетолог от А до Я", teachers.get(2), 72000L));
        courses.add(new Course(9L, "Performance-маркетинг", CourseType.MARKETING, "Представляем вашему вниманию шикарный курс Performance-маркетинг", teachers.get(1), 96000L));
        courses.add(new Course(10L, "Управление командами", CourseType.MANAGEMENT, "Представляем вашему вниманию шикарный курс Управление командами", teachers.get(5), 4000L));
        courses.add(new Course(11L, "Управление Digital-проектами", CourseType.MANAGEMENT, "Представляем вашему вниманию шикарный курс Управление Digital-проектами", teachers.get(5), 100000L));
    }

    private static void setStudents() {
        students = new ArrayList<>();
        students.add(new Student(1L, "Фуриков Эрнст", 18, Date.valueOf("2016-01-01")));
        students.add(new Student(2L, "Амбражевич Порфирий", 19, Date.valueOf("2016-01-08")));
        students.add(new Student(3L, "Носов Макар", 20, Date.valueOf("2016-01-15")));
        students.add(new Student(4L, "Кооскора Вениамин", 21, Date.valueOf("2016-01-22")));
        students.add(new Student(5L, "Беленков Денис", 22, Date.valueOf("2016-01-29")));
        students.add(new Student(6L, "Блаженов Артем", 23, Date.valueOf("2016-02-05")));
        students.add(new Student(7L, "Элиашев Владимир", 24, Date.valueOf("2016-02-12")));
        students.add(new Student(8L, "Корбылев Якуб", 25, Date.valueOf("2016-02-19")));
        students.add(new Student(9L, "Пономарев Валерьян", 26, Date.valueOf("2016-02-26")));
        students.add(new Student(10L, "Шаломенцев Феоктист", 27, Date.valueOf("2016-03-04")));
        students.add(new Student(11L, "Шамило Федосий", 28, Date.valueOf("2016-03-11")));
        students.add(new Student(12L, "Коржаков Юлий", 29, Date.valueOf("2016-03-18")));
        students.add(new Student(13L, "Якусик Вячеслав", 30, Date.valueOf("2016-03-25")));
        students.add(new Student(14L, "Кутяков Ефрем", 31, Date.valueOf("2016-04-01")));
        students.add(new Student(15L, "Бондарев Игорь", 32, Date.valueOf("2016-04-08")));
        students.add(new Student(16L, "Журавлев Севастьян", 33, Date.valueOf("2016-04-15")));
        students.add(new Student(17L, "Иньшов Геннадий", 34, Date.valueOf("2016-04-22")));
        students.add(new Student(18L, "Шабанов Клавдий", 35, Date.valueOf("2016-04-29")));
        students.add(new Student(19L, "Жириновский Наум", 36, Date.valueOf("2016-05-06")));
        students.add(new Student(20L, "Шалаганов Константин", 37, Date.valueOf("2016-05-13")));
        students.add(new Student(21L, "Пичугин Дмитрий", 38, Date.valueOf("2016-05-20")));
        students.add(new Student(22L, "Гилёв Самсон", 39, Date.valueOf("2016-05-27")));
    }
}
