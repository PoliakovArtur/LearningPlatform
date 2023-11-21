package service.impl;

import model.Teacher;
import repository.impl.TeacherRepository;
import service.Service;

import java.util.List;
import java.util.Optional;

public class TeacherService implements Service<Teacher, Long> {
    private TeacherRepository repository;

    public TeacherService(TeacherRepository repository) {
        this.repository = repository;
    }

    @Override
    public Teacher findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ServiceException("not found teacher by id " + id));
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public List<Teacher> findAll() {
        return repository.findAll();
    }

    @Override
    public Teacher save(Teacher teacher) {
        if(teacher.getName() == null
                || teacher.getAge() == null
                || teacher.getSalary() == null) {
            throw new ServiceException("not enough columns to save teacher");
        }
        return repository.save(teacher);
    }

    @Override
    public Teacher update(Teacher teacher) {
        if(teacher.getId() == null) throw new ServiceException("need to set id to update teacher");
        if(teacher.getName() == null
                && teacher.getAge() == null
                && teacher.getSalary() == null) {
            throw new ServiceException("need to set at least one column to update teacher");
        }
        return repository.update(teacher);
    }
}
