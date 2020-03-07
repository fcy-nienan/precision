package com.cpiclife.precisionmarketing.precision.Mapper;

import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface TaskMapper extends JpaRepository<PrecisionTask,Long> {
    List<PrecisionTask> findByCompany(String company);
    public List<PrecisionTask> findByTaskId(long taskId);
    List<PrecisionTask> findByStatus(long status);
    List<PrecisionTask> findByPrecisionId(long precisionId);
    List<PrecisionTask> findByTaskIdAndUserId(long taskId,String userId);
    @Query(value = "SELECT * FROM task WHERE company = ?1",
            countQuery = "SELECT count(*) FROM task WHERE company = ?1",
            nativeQuery = true)
    Page<PrecisionTask> findUserCanVisible(String company, Pageable pageable);
}
