package com.cpiclife.precisionMarketing.model;

import com.cpiclife.precisionMarketing.dao.jpaImpl.jpa.*;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/*
 * Author:fcy
 * Date:2020/3/26 0:37
 */
public class BaseHibernateDao {
    @Autowired
    protected FieldsMapper fieldsMapper;
    @Autowired
    protected ResultMapper resultMapper;
    @Autowired
    protected TaskMapper taskMapper;
    @Autowired
    protected MetaMapper metaMapper;
    @Autowired
    protected EnumMapper enumMapper;
    @Bean(name="sessionFactory")
    public SessionFactory factory(){
        return new SessionFactoryImpl(null,null);
    }

    protected void setSessionFactory(SessionFactory sf) {
    }

    protected Object findBySql(String sql, Map<String, Object> param) {
        return null;
    }
}
