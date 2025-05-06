package com.magicrealms.magiclib.common.repository;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface IBaseRepository<T> {

    void insert(T entity);

    T queryById(Object id);

    void updateById(Object id, Consumer<T> entity);

    void deleteById(Object id);

}
