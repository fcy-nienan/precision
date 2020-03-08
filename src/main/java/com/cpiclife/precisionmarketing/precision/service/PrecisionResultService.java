package com.cpiclife.precisionmarketing.precision.service;

import com.cpiclife.precisionmarketing.precision.Mapper.ResultMapper;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionDescartesFields;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionResult;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/4 0:52
 */
@Component("precisionResultService")
public class PrecisionResultService {
    @Autowired
    private ResultMapper resultMapper;
//    获取最大一次的盘点结果
    public List<PrecisionResult> getLatestResult(Long taskId){
        List<PrecisionResult> results=new ArrayList<>();
        Integer max=resultMapper.getMax(taskId);
        if (max==null){
            results=resultMapper.findByTaskId(taskId);
        }else{
            results=resultMapper.findByTaskIdAndTimes(taskId,max);
        }
        return results;
    }
    public PrecisionResult save(PrecisionResult result){
        return resultMapper.save(result);
    }
    public Integer getMax(Long taskId){
        return resultMapper.getMax(taskId);
    }
    public List<PrecisionResult> findByTaskId(long taskId){
        return resultMapper.findByTaskId(taskId);
    }
    public List<PrecisionResult> findByTaskIdAndTimes(long taskId,long times){
        return resultMapper.findByTaskIdAndTimes(taskId,times);
    }
}
