package com.cpiclife.precisionmarketing.precision.service;

import com.cpiclife.precisionmarketing.precision.Mapper.ResultMapper;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionDescartesFields;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionResult;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import org.springframework.beans.factory.annotation.Autowired;
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

    public static void main(String[] args) {
        System.out.println(Integer.valueOf(1)==Integer.valueOf(1));

    }
}
