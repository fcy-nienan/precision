package com.cpiclife.precisionMarketing.service;

import com.cpiclife.precisionMarketing.dao.ResultInterface;
import com.cpiclife.precisionMarketing.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/4 0:52
 */
@Component("precisionResultService")
public class PrecisionResultService {
    @Autowired
    private ResultInterface resultMapper;
    public List<PrecisionResult> getLastestResultByTaskId(Long taskId){
    	List<PrecisionResult> results=new ArrayList();
        Long max=resultMapper.getMaxByTaskId(taskId);
        if (max==null){
            results=resultMapper.findByTaskId(taskId);
        }else{
            results=resultMapper.findByTaskIdAndTimes(taskId,max);
        }
        return results;
    }
    public List<PrecisionResult> findByTaskIdAndTimes(Long taskId,Long max){
        return resultMapper.findByTaskIdAndTimes(taskId,max);
    }
    public List<PrecisionResult> findByPrecisionIdAndTimes(Long precisionId,Long max){
        return resultMapper.findByPrecisionIdAndTimes(precisionId,max);
    }
    public void save(PrecisionResult result){
        resultMapper.save(result);
    }
    public void saveAll(List<PrecisionResult> result){
    	resultMapper.saveAll(result);
    }
}
