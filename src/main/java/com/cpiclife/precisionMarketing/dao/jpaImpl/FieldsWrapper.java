package com.cpiclife.precisionMarketing.dao.jpaImpl;

import com.cpiclife.precisionMarketing.dao.FieldsInterface;
import com.cpiclife.precisionMarketing.dao.jpaImpl.jpa.FieldsMapper;
import com.cpiclife.precisionMarketing.model.PrecisionDescartesFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:22
 */
@Component
public class FieldsWrapper implements FieldsInterface {
    @Autowired
    private FieldsMapper fieldsMapper;
    @Override
    public Long getMax(long taskId) {
        return fieldsMapper.getMax(taskId);
    }

    @Override
    public List<PrecisionDescartesFields> findByTaskIdAndTimes(long taskId, long max) {
        return fieldsMapper.findByTaskIdAndTimes(taskId,max);
    }

    @Override
    public void save(PrecisionDescartesFields precisionDescartesFields) {
        fieldsMapper.save(precisionDescartesFields);
    }
}
