package com.cpiclife.precisionMarketing.model;

import static com.cpiclife.precisionMarketing.model.TaskEnum.WAIT_SAMPLE;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OtherSystemTask {
	public static OtherSystemTask restartCount(PrecisionTask result){
		OtherSystemTask task=new OtherSystemTask();
		task.setActId(result.getPrecisionId().toString());
		task.setBatchId(result.getTaskId().toString());
		task.setTotals("0");
		task.setGuestGroupName("");
		task.setIfGuestGroup("");
		task.setStatus("0");//0,1,2 待处理，处理中，处理完成
		task.setType("1");//1盘点,2下发
		return task;
	}
	public static OtherSystemTask startCount(PrecisionTask result){
		OtherSystemTask task=new OtherSystemTask();
		task.setActId(result.getPrecisionId().toString());
		task.setBatchId(result.getTaskId().toString());
		task.setTotals("0");
		task.setGuestGroupName("");
		task.setIfGuestGroup("");//0:是 1否
		task.setStatus("0");//0,1,2 待处理，处理中，处理完成
		task.setType("1");//1盘点,2下发
		return task;
	}
	public static OtherSystemTask[] GroupByGuestGroupName(List<OtherSystemTask> task,Map<String,Long> allGroupName){
		int size=allGroupName.size();
		OtherSystemTask[] array=new OtherSystemTask[size];
		Iterator<Map.Entry<String,Long>> iterator=allGroupName.entrySet().iterator();
		int i=0;
		while(iterator.hasNext()){
			Map.Entry<String, Long> entry=iterator.next();
			String key=entry.getKey();
			Long count=entry.getValue();
			array[i++]=combination(key,count);
		}
		return array;
	}
	private static OtherSystemTask combination(String key,Long count){
		String[] values=key.split(",");
		String groupName=values[0],actId=values[1],batchId=values[2];
		OtherSystemTask task=new OtherSystemTask();
		task.setActId(actId);
		task.setBatchId(batchId);
		task.setTotals(count.toString());
		task.setGuestGroupName(groupName);
		task.setIfGuestGroup("0");
		task.setStatus("0");//0,1,2 待处理，处理中，处理完成
		task.setType("2");//1盘点,2下发
		return task;
	}
	public static OtherSystemTask waitDispatcher(PrecisionResult result){
		OtherSystemTask task=new OtherSystemTask();
		task.setActId(result.getPrecisionId().toString());
		task.setBatchId(result.getTaskId().toString());
		task.setTotals(result.getUsedAmount().toString());
		task.setGuestGroupName(result.getGuestGroupName());
		task.setIfGuestGroup("0");
		task.setStatus("0");//0,1,2 待处理，处理中，处理完成
		task.setType("2");//1盘点,2下发
		return task;
	}
	
	private String actId;
	private String batchId;
	private String totals;
	private String status;
	private String type;
	private String ifGuestGroup;
	private String guestGroupName;
	public OtherSystemTask(){
		
	}
	public OtherSystemTask(String actId, String batchId, String totals,
			String status, String type, String ifGuestGroup,
			String guestGroupName) {
		this.actId = actId;
		this.batchId = batchId;
		this.totals = totals;
		this.status = status;
		this.type = type;
		this.ifGuestGroup = ifGuestGroup;
		this.guestGroupName = guestGroupName;
	}
	public String getActId() {
		return actId;
	}
	public void setActId(String actId) {
		this.actId = actId;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getTotals() {
		return totals;
	}
	public void setTotals(String totals) {
		this.totals = totals;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIfGuestGroup() {
		return ifGuestGroup;
	}
	public void setIfGuestGroup(String ifGuestGroup) {
		this.ifGuestGroup = ifGuestGroup;
	}
	public String getGuestGroupName() {
		return guestGroupName;
	}
	public void setGuestGroupName(String guestGroupName) {
		this.guestGroupName = guestGroupName;
	}
}
