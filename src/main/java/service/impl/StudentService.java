package service.impl;

import model.Student;
import model.Subscription;
import repository.impl.StudentRepository;
import service.Service;

import java.util.List;
import java.util.Optional;

public class StudentService implements Service<Student, Long> {

    private StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Student findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ServiceException("not found student by id = " + id));
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Override
    public Student save(Student student) {
        if(student == null || student.getName() == null || student.getAge() == null || student.getRegistrationDate() == null) {
            throw new ServiceException("not enough columns to save student");
        }
        return repository.save(student);
    }

    @Override
    public Student update(Student student) {
        if(student.getId() == null) throw new ServiceException("need to set id to update student");
        if(student.getName() == null
                && student.getAge() == null
                && student.getRegistrationDate() == null) {
            throw new ServiceException("need to set at least one column to update student");
        }
        return repository.update(student);
    }
}
