package com.cpiclife.precisionMarketing.dao.jpaImpl.jpa;

import com.cpiclife.precisionMarketing.model.PrecisionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface ResultMapper extends JpaRepository<PrecisionResult,Long> {
    @Query(nativeQuery = true,
            value = "select max(times) from result where task_id=:taskId")
    public Long getMax(@Param("taskId") Long taskId);
    public List<PrecisionResult> findByTaskId(long taskId);
    public List<PrecisionResult> findByTaskIdAndTimes(long taskId, long times);
    public List<PrecisionResult> findByPrecisionIdAndTimes(long precisionId,long times);

}
