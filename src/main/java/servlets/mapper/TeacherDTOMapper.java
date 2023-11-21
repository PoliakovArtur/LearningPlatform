package servlets.mapper;

import model.Course;
import model.Teacher;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.List;

public class TeacherDTOMapper {
    private TeacherDTOMapper() {}

    public static Teacher map(JSONObject incomingDTO) {
        Teacher teacher = new Teacher();
        if(incomingDTO.containsKey("id")) {
            teacher.setId((Long) incomingDTO.get("id"));
        }
        String name = (String) incomingDTO.get("name");
        Long salary = (Long) incomingDTO.get("salary");
        Integer age = ((Long) incomingDTO.get("age")).intValue();
        if(name == null || salary == null) throw new NullPointerException("not enough fields for teacher");
        teacher.setName(name);
        teacher.setAge(age);
        teacher.setSalary(salary);
        return teacher;
    }

    public static JSONObject map(Teacher teacher) {
        JSONObject outGoingDTO = new JSONObject();
        outGoingDTO.put("id", teacher.getId());
        outGoingDTO.put("name", teacher.getName());
        outGoingDTO.put("salary", teacher.getSalary());
        outGoingDTO.put("age", teacher.getAge());
        List<Course> courses = teacher.getCourses();
        if(courses != null) {
            JSONArray jsonCourses = new JSONArray();
            for(Course course : courses) {
                JSONObject jsonCourse = CourseDTOMapper.map(course);
                jsonCourses.add(jsonCourse);
            }
            outGoingDTO.put("courses", jsonCourses);
        }
        return outGoingDTO;
    }

    public static JSONObject mapAll(List<Teacher> teachers) {
        JSONObject outGoingDTO = new JSONObject();
        JSONArray jsonTeachers = new JSONArray();
         for(Teacher teacher : teachers) {
            jsonTeachers.add(map(teacher));
         }
         outGoingDTO.put("teachers", jsonTeachers);
         return outGoingDTO;
    }
}
