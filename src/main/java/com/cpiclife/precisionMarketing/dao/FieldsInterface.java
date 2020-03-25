package com.cpiclife.precisionMarketing.dao;

import com.cpiclife.precisionMarketing.model.PrecisionDescartesFields;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:16
 */
public interface FieldsInterface {

    Long getMax(long taskId);

    List<PrecisionDescartesFields> findByTaskIdAndTimes(long taskId, long max);

    void save(PrecisionDescartesFields precisionDescartesFields);
}
