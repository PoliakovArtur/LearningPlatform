package servlets.mapper;

import model.Course;
import model.Student;
import model.Subscription;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Date;
import java.util.List;

public class SubscriptionDTOMapper {

    private SubscriptionDTOMapper() {
    }

    public static Subscription map(JSONObject incomingDTO) {
        if (incomingDTO.containsKey("student_id") && incomingDTO.containsKey("course_id") && incomingDTO.containsKey("subscription_date")) {
            Student student = new Student();
            student.setId((Long) incomingDTO.get("student_id"));
            Course course = new Course();
            course.setId((Long) incomingDTO.get("course_id"));
            Date subscriptionDate = Date.valueOf((String) incomingDTO.get("subscription_date"));
            return new Subscription(student, course, subscriptionDate);
        } else {
            throw new NullPointerException("no enough keys for subscription");
        }
    }


    public static JSONObject map(Subscription subscription) {
        JSONObject outGoingDTO = new JSONObject();
        outGoingDTO.put("subscription_date", subscription.getSubscriptionDate().toString());
        if (subscription.getStudent() != null) {
            JSONObject jsonStudent = StudentDTOMapper.map(subscription.getStudent());
            outGoingDTO.put("student", jsonStudent);
        }
        JSONObject jsonCourse = CourseDTOMapper.map(subscription.getCourse());
        outGoingDTO.put("course", jsonCourse);
        return outGoingDTO;
    }


    public static JSONObject mapAll(List<Subscription> subscriptions) {
        JSONObject outGoingDTO = new JSONObject();
        JSONArray jsonSubscriptions = new JSONArray();
        for (Subscription subscription : subscriptions) {
            JSONObject jsonSubscription = map(subscription);
            jsonSubscriptions.add(jsonSubscription);
        }
        outGoingDTO.put("subscriptions", jsonSubscriptions);
        return outGoingDTO;
    }
}
