package repository.mapper;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRSMapper implements ResultSetMapper<Student> {

    @Override
    public Student map(ResultSet resultSet) {
        List<Student> student = mapAll(resultSet);
        return student.size() == 0 ? null : student.get(0);
    }

    @Override
    public List<Student> mapAll(ResultSet resultSet) {
        List<Student> students = new ArrayList<>();
        try {
            Long prevId = -1L;
            Student student = null;
            List<Subscription> subscriptions = null;
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                if (id > prevId) {
                    if (student != null) {
                        student.setSubscriptions(subscriptions);
                    }
                    subscriptions = new ArrayList<>();
                    student = new Student();
                    student.setId(id);
                    student.setName(resultSet.getString(2));
                    student.setAge(resultSet.getInt(3));
                    student.setRegistrationDate(resultSet.getDate(4));
                    students.add(student);
                    prevId = id;
                }
                long courseId = resultSet.getLong(8);
                if (courseId > 0) {
                    Subscription subscription = new Subscription();
                    subscription.setSubscriptionDate(resultSet.getDate(7)); //skip student_id and course_id;
                    Course course = new Course();
                    course.setId(courseId);
                    course.setName(resultSet.getString(9));
                    course.setType(CourseType.valueOf(resultSet.getString(10)));
                    course.setDescription(resultSet.getString(11));
                    course.setPrice(resultSet.getLong(13)); //skip teacher_id

                    Teacher teacher = new Teacher();
                    teacher.setId(resultSet.getLong(14));
                    teacher.setName(resultSet.getString(15));
                    teacher.setSalary(resultSet.getLong(16));
                    teacher.setAge(resultSet.getInt(17));

                    course.setTeacher(teacher);

                    subscription.setCourse(course);
                    subscriptions.add(subscription);
                }
            }
            if (student != null) student.setSubscriptions(subscriptions);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return students;
    }
}
