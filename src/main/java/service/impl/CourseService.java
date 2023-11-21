package service.impl;

import model.Course;
import model.Teacher;
import repository.impl.CourseRepository;
import repository.impl.TeacherRepository;
import service.Service;

import java.util.List;

public class CourseService implements Service<Course, Long> {
    private CourseRepository courseRepository;
    private TeacherService teacherService;

    public CourseService(CourseRepository repository, TeacherService teacherService) {
        this.courseRepository = repository;
        this.teacherService = teacherService;
    }

    @Override
    public Course findById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ServiceException("not found course by id " + id));
    }

    @Override
    public boolean deleteById(Long id) {
        return courseRepository.deleteById(id);
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public Course save(Course course) {
        if(course.getName() == null ||
                course.getPrice() == null ||
                course.getDescription() == null ||
                course.getType() == null ||
                course.getTeacher() == null) {
            throw new ServiceException("not enough columns to save course");
        }
        teacherService.findById(course.getTeacher().getId());
        return courseRepository.save(course);
    }

    @Override
    public Course update(Course course) {
        if(course.getId() == null) throw new ServiceException("need to set id to update course");
        if(course.getName() == null &&
                course.getPrice() == null &&
                course.getDescription() == null &&
                course.getType() == null &&
                course.getTeacher() == null) {
            throw new ServiceException("need to set one column at least to update course");
        }
        return courseRepository.update(course);
    }
}
