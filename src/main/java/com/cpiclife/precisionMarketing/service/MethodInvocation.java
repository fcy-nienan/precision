package com.cpiclife.precisionMarketing.service;

import com.cpiclife.precisionMarketing.dao.*;
import com.cpiclife.precisionMarketing.model.*;
import com.cpiclife.precisionMarketing.service.*;


import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import static com.cpiclife.precisionMarketing.model.TaskEnum.WAIT_COUNT;
import static com.cpiclife.precisionMarketing.model.TaskEnum.WAIT_SAMPLE;

/*
 * Author:fcy
 * Date:2020/3/7 22:30
 */
@Component("methodInvocation")
public class MethodInvocation {
    @Autowired
    private PrecisionMetaInfoService infoService;
    @Autowired
    private PrecisionTaskService taskService;
    @Autowired
    private PrecisionDescartesFieldsService fieldsService;
    @Autowired
    private PrecisionResultService resultService;
    @Autowired
    private CountTask countTask;
    public static class methodWrapper{
        public String[] params;
        public String method;
        public methodWrapper() {
        }
        public methodWrapper(String method, String[] params){
            this.method=method;
            this.params=params;
        }
        @Override
        public String toString() {
            return "methodWrapper{" +
                    "params=" + Arrays.toString(params) +
                    ", method='" + method + '\'' +
                    '}';
        }
    }
    public static methodWrapper getInvoke(String type){
        return methodMap.get(type);
    }
//    第一个参数前端请求的类型,第二个参数调用的方法,后面的通过逗号分隔的就是请求的参数(全是string),然后是换行符
//    请求参数必须有序,要不然会出错
//    格式:url:method:param1,param2,param3\r\n
    private static Map<String,methodWrapper> methodMap=new HashMap();
    private static String controllerMap=
            "getAllTask:getAllTask:precisionId,userId,company,pageIndex,pageSize\r\n" +
            "getLatestCondition:getLatestCondition:precisionId\r\n" +
            "getCurrentTask:getCurrentTask:precisionId\r\n" +
            "getAllResult:getAllResult:precisionId\r\n" +
            "startCount:startCount:userId,precisionId,value,company\r\n" +
            "updateCount:updateCount:value,precisionId\r\n" +
            "startSample:startSample:value,precisionId,userId\r\n" +
            "checkTaskValid:checkTaskValid:precisionId,userId\r\n" +
            "countFinished:countFinished:\r\n" +
            "condition:condition:\r\n"+
            "countFinished:countFinished:\r\n"+
            "getSplitResult:getSplitResult:precisionId,userId\r\n"+
            "flushCondition:flushCondition:";
    static {
        try{
            if (controllerMap==null){
                System.out.println("未配置servlet映射!");
            }else {
                String[] strings = controllerMap.split("\r\n");
                StringBuilder builder = new StringBuilder();
                for (String one : strings) {
                    if (one == null) {
                        System.out.println("解析错误!");
                    }
                    String[] split = one.split(":",3);
                    if (split.length != 3) {
                        System.out.println("配置错误:" + one);
                    }
                    methodWrapper wrapper = new methodWrapper();
                    String type = split[0];
                    wrapper.method=split[1];
//                    "".split(",").length==1   true
                    if(split[2].equals("")){
                        wrapper.params=new String[0];
                    }else {
                        wrapper.params=split[2].split(",");
                    }
                    builder.append("注册方法:---url:" + type + "---"+wrapper + "\r\n");
                    methodMap.put(type, wrapper);
                }
                System.out.println(builder.toString());
            }
        }catch (Exception e){
            System.out.println("注册servlet方法失败!");
            e.printStackTrace();
        }
    }
    public ResponseVO countFinished()throws Exception{
        countTask.countFinished();
        return ResponseVO.success();
    }
    public ResponseVO flushCondition()throws Exception{
        return ResponseVO.success().data(infoService.flushCondition()).msg("获取所有可选条件成功!");
    }
//    获取所有可选的条件
    public ResponseVO condition() throws Exception {
        return ResponseVO.success().data(infoService.getCanSelectCondition()).msg("获取所有可选条件成功!");
    }
//    检测用户和任务是否关联
    public ResponseVO checkTaskValid(String precisionId,
                                     String userId){
        System.out.println("检测用户id和任务id");
        return ResponseVO.success().data(taskService.checkTaskValid(Long.parseLong(precisionId), userId));
    }
//    开始下发
    public ResponseVO startSample(String value,
                                  String precisionId,
                                  String userId)throws Exception{
        System.out.println("下发请求:"+value);
        System.out.println(precisionId+":"+userId);
        if (taskService.checkTaskValid(Long.parseLong(precisionId),userId)){
//            更新任务状态
            List<PrecisionTask> byTaskId = taskService.findByPrecisionId(Long.parseLong(precisionId));
            byTaskId.get(0).setStatus(WAIT_SAMPLE.index());
            taskService.save(byTaskId.get(0));

            ObjectMapper mapper=new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<PrecisionResult> precisionResults = mapper.readValue(value, new TypeReference<List<PrecisionResult>>() {});
            List<OtherSystemTask> otherModel=new ArrayList<OtherSystemTask>();
            Map<String,Long> nameAndCount=new HashMap();
            for (int i=0;i<precisionResults.size();i++){
                PrecisionResult result = precisionResults.get(i);
                result.setSelected(1l);
                resultService.save(result);
                OtherSystemTask task=OtherSystemTask.waitDispatcher(result);
                otherModel.add(task);
                
                String key=task.getGuestGroupName()+","+task.getActId()+","+task.getBatchId();
                long count=Long.parseLong(task.getTotals());
                Long exists=nameAndCount.get(key);
                if(exists==null)exists=0l;
                nameAndCount.put(key, exists+count);
            }
            
            OtherSystemTask[] data=OtherSystemTask.GroupByGuestGroupName(otherModel,nameAndCount);
            
            
            String json=mapper.writeValueAsString(data);
            String url=countTask.getPrecisionUrl();
            System.out.println("发送的json数据:"+json);
            System.out.println("发送的url:"+url);
            HttpClient.sendToOtherSystem(json,url);
            
            return ResponseVO.success().msg("下发中,等待后台处理!");
        }else{
            return ResponseVO.error().msg("下发失败,不合法的任务号!");
        }
    }
//    重新盘点
    public ResponseVO updateCount(String value,
                                  String precisionId) throws JsonProcessingException, UnsupportedEncodingException {
        if (precisionId==null||value==null){
            return ResponseVO.error().msg("不合法的参数!");
        }
        System.out.println("更新盘点任务:"+precisionId+":"+":"+":"+value);

        List<PrecisionTask> taskList=taskService.findByPrecisionId(Long.parseLong(precisionId));
        if (taskList==null||taskList.size()==0){
            return ResponseVO.error().msg("不存在的任务号!");
        }
        PrecisionTask task=taskList.get(0);
        task.setStatus(WAIT_COUNT.index());
        task.setLastModified(new Date());
        task=taskService.save(task);
        ObjectMapper mapper=new ObjectMapper();
        System.out.println("重新盘点:"+task);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<PrecisionDescartesFields> vo;
		try {
			vo = mapper.readValue(value, new TypeReference<List<PrecisionDescartesFields>>() {});
		} catch (IOException e1) {
			e1.printStackTrace();
			return ResponseVO.interError().msg("反序列化失败!");
		}
		
        
		OtherSystemTask[] otherSystem=new OtherSystemTask[1];
        otherSystem[0]=OtherSystemTask.restartCount(task);
        String json;
		try {
			json = mapper.writeValueAsString(otherSystem);
			String url=countTask.getPrecisionUrl();
            System.out.println("发送的json数据:"+json);
            System.out.println("发送的url:"+url);
	        HttpClient.sendToOtherSystem(json,url);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("反序列化失败!");
			e1.printStackTrace();
		}
        try {
            fieldsService.save(vo,task.getId());
        }catch (Exception e){
            return ResponseVO.interError().msg("服务器内部错误!");
        }
        return ResponseVO.success().msg("新盘点任务提交成功!");
    }
//    创建盘点
    public ResponseVO startCount(String userId,
                                 String precisionId,
                                 String value,
                                 String company)throws Exception{
        if (userId==null||precisionId==null||company==null){
            return ResponseVO.error().msg("非法请求");
        }
        System.out.println("开始盘点任务:"+userId+":"+precisionId+":"+company+":"+value);

        List<PrecisionTask> existsTask=taskService.findByPrecisionId(Long.parseLong(precisionId));
        if (existsTask != null && existsTask.size() != 0) {
            return ResponseVO.error().msg("请从其他系统重新进入并盘点!");
        }

        PrecisionTask task=new PrecisionTask();
        task.setUserId(userId);
        task.setCompany(company);
        task.setPrecisionId(Long.parseLong(precisionId));
        task.setStatus(WAIT_COUNT.index());
        task.setInsertDate(new Date());
        task.setLastModified(new Date());
        task=taskService.save(task);
        task.setTaskId(task.getId());
        taskService.save(task);
        ObjectMapper mapper=new ObjectMapper();
        System.out.println("创建盘点任务:"+task);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<PrecisionDescartesFields> vo=mapper.readValue(value, new TypeReference<List<PrecisionDescartesFields>>() {});
        System.out.println("json转化后的对象:"+vo);
        
        OtherSystemTask[] otherSystem=new OtherSystemTask[1];
        otherSystem[0]=OtherSystemTask.startCount(task);
        String json=mapper.writeValueAsString(otherSystem);
        String url=countTask.getPrecisionUrl();
        System.out.println("发送的json数据:"+json);
        System.out.println("发送的url:"+url);
        HttpClient.sendToOtherSystem(json,url);
        
        try {
            fieldsService.save(vo,task.getId());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseVO.interError().msg("服务器内部错误!");
        }
        return ResponseVO.success().msg("提交盘点任务成功!");
    }
    private void flushResult(String precisionId,List<PrecisionResult> LatestResult){
        for (PrecisionResult precisionResult : LatestResult) {
            precisionResult.setPrecisionId(Long.parseLong(precisionId));
            if(precisionResult.getFieldsName()==null){
                String english=precisionResult.getDescartesfields();
                String[] arr=english.split("\\|\\&\\|");
                if(arr.length==1){//后台盘点没有值，只插入了一个nums|#|0
                    precisionResult.setAmount(0l);
                    precisionResult.setFieldsName("盘点无数据");
                }else{
                    StringBuilder builder=new StringBuilder();
                    for(int i=0;i<arr.length-1;i++){
                        String s=arr[i];
                        String[] fields=s.split("\\|\\#\\|");
                        String fieldCode=fields[0];
                        String enumCode=fields[1];
                        String chinese=infoService.getChineseValue(fieldCode, enumCode);
                        builder.append(chinese).append(",");
                    }
                    builder.deleteCharAt(builder.length()-1);
                    precisionResult.setFieldsName(builder.toString());
                    String[] last=arr[arr.length-1].split("\\|\\#\\|");
                    String mark=last[0],amount=last[1];
                    if("nums".equals(mark)){
                        try{
                            precisionResult.setAmount(Long.parseLong(amount));
                        }catch(Exception e){
//            				如果前台出现-1说明后台插入的值不是数字
                            precisionResult.setAmount(-1l);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
//    获取当前任务id最新的盘点结果
    public ResponseVO getAllResult(String precisionId){
//    	先检测任务id是否存在
    	List<PrecisionTask> taskList=taskService.findByPrecisionId(Long.parseLong(precisionId));
    	if(taskList==null||taskList.size()==0)return ResponseVO.error().msg("错误的任务号!");
    	long taskId=taskList.get(0).getTaskId();
    	//从后台获取数据，第一次获取的时候fieldName是NULL,需要解析后台传过来的结果数据
        List<PrecisionResult> LatestResult = resultService.getLastestResultByTaskId(taskId);
        flushResult(precisionId,LatestResult);
        resultService.saveAll(LatestResult);
        return ResponseVO.success().data(LatestResult).msg("获取所有盘点结果成功!");
    }
    public ResponseVO getSplitResult(String precisionId,String userId){
        boolean b = taskService.checkTaskValid(Long.parseLong(precisionId), userId);
        if (!b)return ResponseVO.error().msg("错误的任务号!");
        List<PrecisionResult> byPrecisionIdAndTimes = resultService.findByPrecisionIdAndTimes(Long.parseLong(precisionId), -1l);
        return ResponseVO.success().data(byPrecisionIdAndTimes==null?new ArrayList():byPrecisionIdAndTimes).msg("获取切分结果成功!");
    }
//    获取当前任务信息
    public ResponseVO getCurrentTask(String precisionId)throws Exception{
        List<PrecisionTask> byTaskId = taskService.findByPrecisionId(Long.parseLong(precisionId));
        if (byTaskId!=null&&byTaskId.size()!=0){
            return ResponseVO.success().data(byTaskId).msg("获取任务信息成功!");
        }
        return ResponseVO.error().msg("当前任务不存在!");
    }
//    获取用户所有可视任务
    public ResponseVO getAllTask(String precisionId,
    							 String userId,
                                 String company,
                                 String pageIndex,
                                 String pageSize)throws Exception {
        System.out.println("getAllTask:"+userId+":"+pageIndex+":"+pageSize);
        return ResponseVO.success().data(taskService.getUserAllVisibleTask(precisionId,userId, company,
                Integer.parseInt(pageIndex), Integer.parseInt(pageSize))).msg("获取所有任务成功!");
    }
//    获取用户选择的条件
    public ResponseVO getLatestCondition(
            String precisionId){
    	Long taskId=taskService.findByPrecisionId(Long.parseLong(precisionId)).get(0).getTaskId();
        return ResponseVO.success().data(fieldsService.queryMaxCondition(taskId))
                .msg("获取最近的盘点条件成功!");
    }
}
