package repository.mapper;

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseRSMapper implements ResultSetMapper<Course> {
    @Override
    public Course map(ResultSet resultSet) {
        List<Course> course = mapAll(resultSet);
        return course.size() == 0 ? null : course.get(0);
    }

    @Override
    public List<Course> mapAll(ResultSet resultSet) {
        List<Course> courses = new ArrayList<>();
        try {
            Course course = null;
            List<Student> students = null;
            Long prevId = -1L;
            while (resultSet.next()) {
                Long id = resultSet.getLong(5); // skip teacher columns
                if (prevId < id) {
                    if (course != null) course.setStudents(students);
                    students = new ArrayList<>();
                    Teacher teacher = new Teacher();
                    teacher.setId(resultSet.getLong(1));
                    teacher.setName(resultSet.getString(2));
                    teacher.setSalary(resultSet.getLong(3));
                    teacher.setAge(resultSet.getInt(4));
                    course = new Course();
                    course.setId(id);
                    course.setName(resultSet.getString(6));
                    course.setType(CourseType.valueOf(resultSet.getString(7)));
                    course.setDescription(resultSet.getString(8));
                    course.setTeacher(teacher);
                    course.setPrice(resultSet.getLong(10)); //skip teacher_id
                    courses.add(course);
                    prevId = id;
                }
                long studentId = resultSet.getLong(14); // skip subscriptions columns
                if (studentId > 0) {
                    Student student = new Student();
                    student.setId(studentId);
                    student.setName(resultSet.getString(15));
                    student.setAge(resultSet.getInt(16));
                    student.setRegistrationDate(resultSet.getDate(17));
                    students.add(student);
                }
            }
            if (course != null) course.setStudents(students);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return courses;
    }
}
