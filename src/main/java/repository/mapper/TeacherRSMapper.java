package repository.mapper;

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherRSMapper implements ResultSetMapper<Teacher> {
    @Override
    public Teacher map(ResultSet resultSet) {
        List<Teacher> teachers = mapAll(resultSet);
        return teachers.size() == 0 ? null : teachers.get(0);
    }

    @Override
    public List<Teacher> mapAll(ResultSet resultSet) {
        List<Teacher> teachers = new ArrayList<>();
        try {
            Teacher teacher = null;
            List<Course> courses = null;
            Long prevId = -1L;
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                if (prevId < id) {
                    if (teacher != null) teacher.setCourses(courses);
                    courses = new ArrayList<>();
                    teacher = new Teacher();
                    teacher.setId(id);
                    teacher.setName(resultSet.getString(2));
                    teacher.setSalary(resultSet.getLong(3));
                    teacher.setAge(resultSet.getInt(4));
                    teachers.add(teacher);
                    prevId = id;
                }
                Course course = new Course();
                long courseId = resultSet.getLong(5);
                if (courseId > 0) {
                    course.setId(courseId);
                    course.setName(resultSet.getString(6));
                    course.setType(CourseType.valueOf(resultSet.getString(7)));
                    course.setDescription(resultSet.getString(8));
                    course.setPrice(resultSet.getLong(10)); //skip teacher_id;;
                    courses.add(course);
                }
            }
            if (teacher != null) teacher.setCourses(courses);
            return teachers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
