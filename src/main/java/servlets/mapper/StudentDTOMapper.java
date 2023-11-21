package servlets.mapper;

import model.Student;
import model.Subscription;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class StudentDTOMapper {

    private StudentDTOMapper() {
    }

    public static Student map(JSONObject incomingDTO) {
        Student student = new Student();
        if (incomingDTO.containsKey("id")) {
            student.setId((Long) incomingDTO.get("id"));
        }
        String name = (String) incomingDTO.get("name");
        Integer age = null;
        if (incomingDTO.containsKey("age")) {
            age = ((Long) incomingDTO.get("age")).intValue();
        }
        Date registrationDate = Date.valueOf((String) incomingDTO.get("registration_date"));
        student.setName(name);
        student.setAge(age);
        student.setRegistrationDate(registrationDate);
        return student;
    }

    public static JSONObject map(Student student) {
        JSONObject outGoingDTO = new JSONObject();
        outGoingDTO.put("id", student.getId());
        outGoingDTO.put("name", student.getName());
        outGoingDTO.put("age", student.getAge());
        outGoingDTO.put("registration_date", student.getRegistrationDate().toString());
        List<Subscription> subscriptions = student.getSubscriptions();
        if (subscriptions != null) {
            JSONArray jsonSubscriptions = new JSONArray();
            for (Subscription subscription : subscriptions) {
                JSONObject jsonSubscription = SubscriptionDTOMapper.map(subscription);
                jsonSubscriptions.add(jsonSubscription);
            }
            outGoingDTO.put("subscriptions", jsonSubscriptions);
        }
        return outGoingDTO;
    }

    public static JSONObject mapAll(List<Student> students) {
        JSONObject outGoingDTO = new JSONObject();
        JSONArray jsonStudents = new JSONArray();
        for (Student student : students) {
            JSONObject jsonStudent = map(student);
            jsonStudents.add(jsonStudent);
        }
        outGoingDTO.put("students", jsonStudents);
        return outGoingDTO;
    }
}
