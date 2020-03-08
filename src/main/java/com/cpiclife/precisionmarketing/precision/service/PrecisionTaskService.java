package com.cpiclife.precisionmarketing.precision.service;

import com.cpiclife.precisionmarketing.precision.Mapper.TaskMapper;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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
    public  List<PrecisionTask> findByCompany(String company){
        return taskMapper.findByCompany(company);
    }
    public List<PrecisionTask> findByStatus(long status){
        return taskMapper.findByStatus(status);
    }
    public List<PrecisionTask> findByPrecisionId(long precisionId){
        return taskMapper.findByPrecisionId(precisionId);
    }
    public List<PrecisionTask> findByTaskIdAndUserId(long taskId,String userId){
        return taskMapper.findByTaskIdAndUserId(taskId,userId);
    }
    public PrecisionTask save(PrecisionTask task){
        return taskMapper.save(task);
    }
    public List<PrecisionTask> findByTaskId(Long taskId){
        return taskMapper.findByTaskId(taskId);
    }
//    查询用户所有可以看见的任务
    public Page getUserAllVisibleTask(String userId,String company,Long pageIndex,Long pageSize){
        Page userCanVisible = taskMapper.findUserCanVisible(company, PageRequest.of(pageIndex.intValue(), pageSize.intValue()));
        return userCanVisible;
    }
//    检测该任务是否是该用户下的
    public boolean checkTaskValid(long taskId,String userId){
        List<PrecisionTask> precisionTasks=taskMapper.findByTaskIdAndUserId(taskId,userId);
        if (precisionTasks!=null&&precisionTasks.size()!=0){
            return true;
        }
        return false;
    }
}
