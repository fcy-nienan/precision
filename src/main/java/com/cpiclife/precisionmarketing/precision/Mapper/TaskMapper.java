package com.cpiclife.precisionmarketing.precision.Mapper;

import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface TaskMapper extends JpaRepository<PrecisionTask,Long> {
    List<PrecisionTask> findByCompany(String company);
    @Transactional(Transactional.TxType.REQUIRED)
    @Modifying
    @Query("update task set status=:status where taskId=:taskId and userId=:userId")
    void updateStatus(@Param("userId")Long userId, @Param("taskId")Long taskId,@Param("status")Long status);

    public List<PrecisionTask> findByTaskId(long taskId);
}
