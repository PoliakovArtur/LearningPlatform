package repository.impl;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    Optional<T> findById(K id);

    boolean deleteById(K id);

    List<T> findAll();

    T save(T t);

    T update(T t);

}
