package repository.mapper;

import java.sql.ResultSet;
import java.util.List;

public interface ResultSetMapper<E> {
    E map(ResultSet resultSet);
    List<E> mapAll(ResultSet resultSet);
}
