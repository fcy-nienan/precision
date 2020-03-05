package com.cpiclife.precisionmarketing.precision.service;

import com.cpiclife.precisionmarketing.precision.Mapper.TaskMapper;
import com.cpiclife.precisionmarketing.precision.Model.Page;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/4 0:51
 */
@Component("precisionTaskService")
public class PrecisionTaskService {
    @Autowired
    private TaskMapper taskMapper;
    public void createNewCount(String precisionId,String userId,String company){
        PrecisionTask task=new PrecisionTask();
        task.setId(0l);
        task.setUserId(userId);
        task.setPrecisionId(Long.parseLong(precisionId));
        task.setCompany(company);
        task.setStatus(0l);
        taskMapper.save(task);

    }
    public List<PrecisionTask> getUserAllVisibleTask(String userId,String company,Long pageIndex,Long pageSize){
        List<PrecisionTask> byCompany=taskMapper.findByCompany(company);
        for (int i=0;i<byCompany.size();i++){
            byCompany.get(i).updateStatusName();
        }
        return byCompany;
    }
    public Page getData(String userId,String company,int pageIndex,int pageSize){
        List<PrecisionTask> byCompany = taskMapper.findByCompany(company);

        List<PrecisionTask> result=new ArrayList<>();
        int startIndex=(pageIndex-1)*pageSize;
        int j=0;
        for (int i=0;i<byCompany.size();i++){
            PrecisionTask task = byCompany.get(i);
            if (task.getUserId().equals(userId)&&task.getCompany().equals(company)){
                j++;
                if(j>=startIndex) {
                    result.add(task);
                }
            }
        }
        return new Page(byCompany.size(),pageIndex,pageSize,result);
    }
}
