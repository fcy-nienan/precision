package com.cpiclife.precisionmarketing.precision.Mapper;

import com.cpiclife.precisionmarketing.precision.Model.PrecisionDescartesFields;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Column;
import java.util.List;

public interface FieldsMapper extends JpaRepository<PrecisionDescartesFields,Long> {
    @Query(nativeQuery = true,
            value = "select max(times) from fields where task_id=:taskId")
    public Long getMax(@Param("taskId") Long taskId);
    public List<PrecisionDescartesFields> findByTaskId(long taskId);
    public List<PrecisionDescartesFields> findByTaskIdAndTimes(long taskId,long times);
}
