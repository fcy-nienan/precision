package com.cpiclife.precisionmarketing.precision.Controller;

import com.cpiclife.precisionmarketing.precision.Mapper.TaskMapper;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import com.cpiclife.precisionmarketing.precision.Model.TaskEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;
@Component
public class CountTask {
    @Autowired
    private TaskMapper taskMapper;
    public PrecisionTask findCanCount(){
        List<PrecisionTask> byStatus = taskMapper.findByStatus(TaskEnum.WAIT_COUNT.index());
        if (byStatus!=null&&byStatus.size()!=0){
            return byStatus.get(0);
        }
        return null;
    }
    public void CountFinished(){
        PrecisionTask canCount = findCanCount();
        if (canCount!=null){
            canCount.setStatus(TaskEnum.COUNT_FINISHED.index());
            taskMapper.save(canCount);
        }
    }
}
