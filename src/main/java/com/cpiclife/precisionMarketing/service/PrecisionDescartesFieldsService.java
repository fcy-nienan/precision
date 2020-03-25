package com.cpiclife.precisionMarketing.service;
import com.cpiclife.precisionMarketing.dao.EnumInterface;
import com.cpiclife.precisionMarketing.dao.FieldsInterface;
import com.cpiclife.precisionMarketing.dao.MetaInterface;
import com.cpiclife.precisionMarketing.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/4 0:51
 */
@Component("precisionDescartesFieldsService")
public class PrecisionDescartesFieldsService {
    @Autowired
    private FieldsInterface fieldsMapper;
    @Autowired
    private MetaInterface metaMapper;
    @Autowired
    private EnumInterface enumMapper;
//    获取改任务号的最大的times(每盘点一次times加一)
    public long getMax(long taskId){
        return fieldsMapper.getMax(taskId);
    }
////    查询改任务号的最近的盘点条件
    public List<PrecisionDescartesFields> queryMaxCondition(long taskId){
        long max=getMax(taskId);
        List<PrecisionDescartesFields> byTaskIdAndTimes = fieldsMapper.findByTaskIdAndTimes(taskId, max);
        
        return byTaskIdAndTimes;
    }
////    保存改任务号的盘点条件
    public void save(List<PrecisionDescartesFields> vo,long taskId){
        long times=1;
//        第一次可能没有值max返回的是0,其他都是times+1
        long max = fieldsMapper.getMax(taskId);
        times=max+1;
        for (int i=0;i<vo.size();i++){
            if (vo.get(i).getFieldCode()!=null&&vo.get(i).getComparisonOperator()!=null&&vo.get(i).getEnumCode()!=null) {
                vo.get(i).setTimes(times);
                vo.get(i).setTaskId(taskId);
                vo.get(i).setFieldCstrValue(vo.get(i).getEnumCode());
                fieldsMapper.save(vo.get(i));
            }
        }
    }
}
