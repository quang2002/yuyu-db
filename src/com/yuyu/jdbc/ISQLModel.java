package com.yuyu.jdbc;

import java.util.List;

public interface ISQLModel<T> {
    public T get(Object... primaryKeys) throws Exception;
    public List<T> getall() throws Exception;

    public int insert(T data) throws Exception;
    public int update(T data) throws Exception;
    public int delete(T data) throws Exception;
}
