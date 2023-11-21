package repository.mapper;

import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionRSMapper implements ResultSetMapper<Subscription> {
    @Override
    public Subscription map(ResultSet resultSet) {
        List<Subscription> subscriptions = mapAll(resultSet);
        return subscriptions.size() == 0 ? null : subscriptions.get(0);
    }

    @Override
    public List<Subscription> mapAll(ResultSet resultSet) {
        List<Subscription> subscriptions = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Subscription subscription = new Subscription();
                Student student = new Student();
                student.setId(resultSet.getLong(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setRegistrationDate(resultSet.getDate(4));

                subscription.setStudent(student);
                subscription.setSubscriptionDate(resultSet.getDate(7)); //skip student_id and //course_id

                Course course = new Course();
                course.setId(resultSet.getLong(8));
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
            return subscriptions;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
