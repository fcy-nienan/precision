package com.cpiclife.precisionmarketing.precision.service;

import com.cpiclife.precisionmarketing.precision.Mapper.TaskMapper;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Page getUserAllVisibleTask(String userId,String company,Long pageIndex,Long pageSize){
//        List<PrecisionTask> byCompany=taskMapper.findByCompany(company);
//        return byCompany;
        Page userCanVisible = taskMapper.findUserCanVisible(company, PageRequest.of(pageIndex.intValue(), pageSize.intValue()));
        return userCanVisible;
    }
    public boolean checkTaskValid(long taskId,String userId){
        List<PrecisionTask> precisionTasks=taskMapper.findByTaskIdAndUserId(taskId,userId);
        if (precisionTasks!=null&&precisionTasks.size()!=0){
            return true;
        }
        return false;
    }
}
