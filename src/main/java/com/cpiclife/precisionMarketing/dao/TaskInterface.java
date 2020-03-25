package com.cpiclife.precisionMarketing.dao;

import com.cpiclife.precisionMarketing.model.Page;
import com.cpiclife.precisionMarketing.model.PrecisionTask;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:12
 */
public interface TaskInterface {
    List<PrecisionTask> findByTaskIdAndUserId(long taskId, long userId);

    List<PrecisionTask> findByPrecisionId(long precisionId);

    PrecisionTask save(PrecisionTask task);

    Page findPageByPrecisionId(long parseLong, int pageIndex, int pageSize);

    List<PrecisionTask> findByPrecisionIdAndUserId(long precisionId, String userId);
}
