package com.cpiclife.precisionMarketing.service;

import com.cpiclife.precisionMarketing.dao.*;
import com.cpiclife.precisionMarketing.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/4 0:51
 */
@Component("precisionTaskService")
public class PrecisionTaskService {
    @Autowired
    private TaskInterface taskMapper;
    public List<PrecisionTask> findByPrecisionId(long precisionId){
        return taskMapper.findByPrecisionId(precisionId);
    }
    public PrecisionTask save(PrecisionTask task){
        return taskMapper.save(task);
    }
////    查询用户所有可以看见的任务
    public Page getUserAllVisibleTask(String precisionId,String userId,String company,int pageIndex,int pageSize){
    	Page userCanVisible;
/*    	if("0000000".equals(company)){
    		userCanVisible=taskMapper.findTotalVisible(pageIndex, pageSize);
    	}else{
            userCanVisible = taskMapper.findUserCanVisible(company, pageIndex,pageSize);
    	}*/
    	return taskMapper.findPageByPrecisionId(Long.parseLong(precisionId),pageIndex,pageSize);
    	
    }
////    检测该任务是否是该用户下的
    public boolean checkTaskValid(long precisionId,String userId){
        List<PrecisionTask> precisionTasks=taskMapper.findByPrecisionIdAndUserId(precisionId,userId);
        if (precisionTasks!=null&&precisionTasks.size()!=0){
            return true;
        }
        return false;
    }
}
