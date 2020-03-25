package com.cpiclife.precisionMarketing.dao;

import com.cpiclife.precisionMarketing.model.PrecisionResult;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:14
 */
public interface ResultInterface {

    Long getMaxByTaskId(Long taskId);

    List<PrecisionResult> findByTaskId(Long taskId);

    List<PrecisionResult> findByTaskIdAndTimes(Long taskId, Long max);

    void save(PrecisionResult result);

    void saveAll(List<PrecisionResult> result);
}
