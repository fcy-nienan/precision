package com.cpiclife.precisionMarketing.dao.jpaImpl;

import com.cpiclife.precisionMarketing.dao.jpaImpl.jpa.TaskMapper;
import com.cpiclife.precisionMarketing.model.Page;
import com.cpiclife.precisionMarketing.dao.TaskInterface;
import com.cpiclife.precisionMarketing.model.PrecisionTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:20
 */
@Component
public class TaskWrapper implements TaskInterface {
    @Autowired
    private TaskMapper taskMapper;
    @Override
    public List<PrecisionTask> findByTaskIdAndUserId(long taskId, long userId) {
        return taskMapper.findByTaskIdAndUserId(taskId, String.valueOf(userId));
    }

    @Override
    public List<PrecisionTask> findByPrecisionId(long precisionId) {
        return taskMapper.findByPrecisionId(precisionId);
    }

    @Override
    public PrecisionTask save(PrecisionTask task) {
        return taskMapper.save(task);
    }

    @Override
    public Page findPageByPrecisionId(long parseLong, int pageIndex, int pageSize) {
        return null;
    }

    @Override
    public List<PrecisionTask> findByPrecisionIdAndUserId(long precisionId, String userId) {
        return taskMapper.findByPrecisionIdAndUserId(precisionId,userId);
    }
}
