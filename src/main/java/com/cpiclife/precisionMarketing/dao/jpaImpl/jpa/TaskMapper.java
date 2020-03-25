package com.cpiclife.precisionMarketing.dao.jpaImpl.jpa;

import com.cpiclife.precisionMarketing.model.PrecisionTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface TaskMapper extends JpaRepository<PrecisionTask,Long>{
    List<PrecisionTask> findByCompany(String company);
    public List<PrecisionTask> findByTaskId(long taskId);
    List<PrecisionTask> findByStatus(long status);
    List<PrecisionTask> findByPrecisionId(long precisionId);
    List<PrecisionTask> findByTaskIdAndUserId(long taskId, String userId);
    List<PrecisionTask> findByPrecisionIdAndUserId(long precisionId,String userId);
    @Query(value = "SELECT * FROM task WHERE company = ?1",
            countQuery = "SELECT count(*) FROM task WHERE company = ?1",
            nativeQuery = true)
    Page<PrecisionTask> findUserCanVisible(String company, Pageable pageable);
}
