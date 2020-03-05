package com.cpiclife.precisionmarketing.precision.service;

import com.cpiclife.precisionmarketing.precision.Mapper.FieldsMapper;
import com.cpiclife.precisionmarketing.precision.Model.Page;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionDescartesFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/4 0:51
 */
@Component("precisionDescartesFieldsService")
public class PrecisionDescartesFieldsService {
    @Autowired
    private FieldsMapper fieldsMapper;
    public long getMax(long taskId){
        return fieldsMapper.getMax(taskId);
    }
    public List<PrecisionDescartesFields> queryMaxCondition(long taskId){
        long max=getMax(taskId);
        return fieldsMapper.findByTaskIdAndTimes(taskId,max);
    }

    public void save(PrecisionDescartesFields fields){
        fieldsMapper.save(fields);
    }
}
