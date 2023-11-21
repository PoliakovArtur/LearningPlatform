package servlets.mapper;

import model.Course;
import model.CourseType;
import model.Student;
import model.Teacher;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

public class CourseDTOMapper {

    public static Course map(JSONObject incomingDto) {
        Course course = new Course();
        if(incomingDto.containsKey("id")) {
            Long id = (Long) incomingDto.get("id");
            course.setId(id);
        }
        String name = (String) incomingDto.get("name");
        String description = (String) incomingDto.get("description");
        CourseType type = CourseType.valueOf((String) incomingDto.get("type"));
        Long price = (Long) incomingDto.get("price");
        Long teacherId = (Long) incomingDto.get("teacher_id");
        if(name == null || description == null || price == null || teacherId == null) {
            throw new NullPointerException("not enough keys for course");
        }
        course.setType(type);
        course.setName(name);
        course.setPrice(price);
        course.setDescription(description);
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        course.setTeacher(teacher);
        return course;
    }

    public static JSONObject map(Course course) {
        JSONObject outGoingDTO = new JSONObject();
        outGoingDTO.put("id", course.getId());
        outGoingDTO.put("name", course.getName());
        outGoingDTO.put("description", course.getDescription());
        outGoingDTO.put("type", course.getType().toString());
        outGoingDTO.put("price", course.getPrice());
        Teacher teacher = course.getTeacher();
        if(teacher != null) {
            JSONObject jsonTeacher = TeacherDTOMapper.map(teacher);
            outGoingDTO.put("teacher", jsonTeacher);
        }
        List<Student> students = course.getStudents();
        if(students != null) {
            JSONArray jsonStudents = new JSONArray();
            for(Student student : students) {
                JSONObject jsonStudent = StudentDTOMapper.map(student);
                jsonStudents.add(jsonStudent);
            }
            outGoingDTO.put("students", jsonStudents);
        }
        return outGoingDTO;
    }

    public static JSONObject mapAll(List<Course> courses) {
        JSONObject outGoingDTO = new JSONObject();
        JSONArray jsonCourses = new JSONArray();
        for(Course course : courses) {
            JSONObject jsonCourse = map(course);
            jsonCourses.add(jsonCourse);
        }
        outGoingDTO.put("courses", jsonCourses);
        return outGoingDTO;
    }
}
