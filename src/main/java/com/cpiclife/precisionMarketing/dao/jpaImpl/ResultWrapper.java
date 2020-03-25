package com.cpiclife.precisionMarketing.dao.jpaImpl;

import com.cpiclife.precisionMarketing.dao.ResultInterface;
import com.cpiclife.precisionMarketing.dao.jpaImpl.jpa.ResultMapper;
import com.cpiclife.precisionMarketing.model.PrecisionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:21
 */
@Component
public class ResultWrapper implements ResultInterface {
    @Autowired
    private ResultMapper resultMapper;
    @Override
    public Long getMaxByTaskId(Long taskId) {
        return resultMapper.getMax(taskId);
    }

    @Override
    public List<PrecisionResult> findByTaskId(Long taskId) {
        return resultMapper.findByTaskId(taskId);
    }

    @Override
    public List<PrecisionResult> findByTaskIdAndTimes(Long taskId, Long max) {
        return resultMapper.findByTaskIdAndTimes(taskId,max);
    }

    @Override
    public void save(PrecisionResult result) {
        resultMapper.save(result);
    }

    @Override
    public void saveAll(List<PrecisionResult> result) {
        for (PrecisionResult precisionResult : result) {
            resultMapper.save(precisionResult);
        }
    }
}
