package com.cpiclife.precisionMarketing.service;
import com.cpiclife.precisionMarketing.dao.*;
import com.cpiclife.precisionMarketing.dao.jpaImpl.jpa.ResultMapper;
import com.cpiclife.precisionMarketing.dao.jpaImpl.jpa.TaskMapper;
import com.cpiclife.precisionMarketing.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;
@Component
public class CountTask {
	@Value("${precision.url}")
	private String precisionUrl;
	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private ResultMapper resultMapper;
	public String getPrecisionUrl() {
		return precisionUrl;
	}

	public void setPrecisionUrl(String precisionUrl) {
		this.precisionUrl = precisionUrl;
	}

	public void countFinished(){
		List<PrecisionTask> byStatus = taskMapper.findByStatus(TaskEnum.WAIT_COUNT.index());
		for (PrecisionTask status : byStatus) {
			status.setStatus(TaskEnum.COUNT_FINISHED.index());
			taskMapper.save(status);

			Long max = resultMapper.getMax(status.getTaskId());
			long times=1;
			if(max!=null)times=max+1;
			PrecisionResult result=new PrecisionResult();
			result.setTaskId(status.getTaskId());
			result.setTimes(times);
			result.setDescartesfields("gender|#|1|&|nums|#|20");
			PrecisionResult result1=new PrecisionResult();
			result1.setTaskId(status.getTaskId());
			result1.setTimes(times);
			result1.setDescartesfields("gender|#|2|&|nums|#|120");
			resultMapper.save(result);
		}
	}

}
