package service;

import java.util.List;

public interface Service<T, K> {

    T findById(K id);

    boolean deleteById(K id);

    List<T> findAll();

    T save(T t);

    T update(T t);
}
