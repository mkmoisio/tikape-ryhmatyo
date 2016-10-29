package tikape.runko.database;

import java.sql.*;
import java.util.*;

public interface Dao<T, K> {

    T create(T t) throws SQLException;

    T findOne(K key) throws SQLException;

    List<T> findAll() throws SQLException;

    public List<T> findAllIn(Collection<K> keys) throws SQLException;
    
    public T collect(ResultSet rs) throws SQLException;
}
