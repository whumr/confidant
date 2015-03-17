package com.confidant.common;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by Administrator on 2015/1/7.
 */
@Service
public abstract class BaseService<E extends BaseEntity> {

    @Autowired
    @Qualifier("baseSqlSession")
    protected SqlSession baseSqlSession;

    protected static String NAMESPACE;
    protected String namespace;

    @PostConstruct
    protected void init() {
        this.namespace = NAMESPACE;
    }

    public void insert(E entity) {
        baseSqlSession.insert(namespace + "insert", entity);
    }

    public void batchInsert(List<E> entities) {
        baseSqlSession.insert(namespace + "batchInsert", entities);
    }
}
