package com.cpiclife.precisionmarketing.precision.Mapper;

import com.cpiclife.precisionmarketing.precision.Model.PrecisionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Column;
import javax.transaction.Transactional;
import java.util.List;

public interface ResultMapper extends JpaRepository<PrecisionResult,Long> {
    @Query(nativeQuery = true,
            value = "select max(times) from result where task_id=:taskId")
    public Integer getMax(@Param("taskId") Long taskId);
    public List<PrecisionResult> findByTaskId(long taskId);
    public List<PrecisionResult> findByTaskIdAndTimes(long taskId,long times);

}
