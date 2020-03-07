package com.cpiclife.precisionmarketing.precision.Controller;

import com.cpiclife.precisionmarketing.precision.Mapper.*;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionDescartesFields;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionResult;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import com.cpiclife.precisionmarketing.precision.Model.ResponseVO;
import com.cpiclife.precisionmarketing.precision.service.PrecisionDescartesFieldsService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionMetaInfoService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionResultService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionTaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.*;

import static com.cpiclife.precisionmarketing.precision.Model.TaskEnum.WAIT_COUNT;
import static com.cpiclife.precisionmarketing.precision.Model.TaskEnum.WAIT_SAMPLE;

/*
 * Author:fcy
 * Date:2020/3/7 22:30
 */
@Component("methodInvocation")
public class MethodInvocation {
    @Autowired
    private EnumMapper enumMapper;
    @Autowired
    private FieldsMapper fieldsMapper;
    @Autowired
    private MetaMapper metaMapper;
    @Autowired
    private ResultMapper resultMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private PrecisionMetaInfoService infoService;
    @Autowired
    private PrecisionTaskService taskService;
    @Autowired
    private PrecisionDescartesFieldsService fieldsService;
    @Autowired
    private PrecisionResultService resultService;
    @Autowired
    private CountTask task;
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
    public static void main(String[] args) {
        System.out.println("".split(",").length);
    }
//    第一个参数前端请求的类型,第二个参数调用的方法,后面的通过逗号分隔的就是请求的参数(全是string),然后是换行符
//    请求参数必须有序,要不然会出错
//    格式:url:method:param1,param2,param3\r\n
    private static Map<String,methodWrapper> methodMap=new HashMap();
    private static String controllerMap=
            "getAllTask:getAllTask:userId,company,pageIndex,pageSize\r\n" +
            "getLatestCondition:getLatestCondition:taskId\r\n" +
            "getCurrentTask:getCurrentTask:taskId\r\n" +
            "getAllResult:getAllResult:taskId\r\n" +
            "startCount:startCount:userId,precisionId,value,company\r\n" +
            "updateCount:updateCount:value,taskId\r\n" +
            "startSample:startSample:value,taskId,userId\r\n" +
            "checkTaskValid:checkTaskValid:taskId,userId\r\n" +
            "countFinished:countFinished:\r\n" +
            "condition:condition:\r\n";
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
    public ResponseVO condition() throws Exception {
        System.out.println(infoService.getCanSelectCondition());
        return ResponseVO.success().data(infoService.getCanSelectCondition()).msg("获取所有可选条件成功!");
    }
    public ResponseVO countFinished(){
        task.CountFinished();
        return ResponseVO.success().msg("盘点完成!");
    }
    public ResponseVO checkTaskValid(String taskId,
                                     String userId){
        System.out.println("检测用户id和任务id");
        return ResponseVO.success().data(taskService.checkTaskValid(Long.parseLong(taskId), userId));
    }
    public ResponseVO startSample(String value,
                                  String taskId,
                                  String userId)throws Exception{
        System.out.println("抽样请求:"+value);
        System.out.println(taskId+":"+userId);
        if (taskService.checkTaskValid(Long.parseLong(taskId),userId)){
//            更新任务状态
            List<PrecisionTask> byTaskId = taskMapper.findByTaskId(Long.parseLong(taskId));
            byTaskId.get(0).setStatus(WAIT_SAMPLE.index());
            taskMapper.save(byTaskId.get(0));

            ObjectMapper mapper=new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<PrecisionResult> precisionResults = mapper.readValue(value, new TypeReference<List<PrecisionResult>>() {});
            for (int i=0;i<precisionResults.size();i++){
                PrecisionResult result = precisionResults.get(i);
                result.setSelected(1l);
                resultMapper.save(result);
            }
            return ResponseVO.success().msg("抽样中,等待后台处理!");
        }else{
            return ResponseVO.error().msg("抽样失败,不合法的任务号!");
        }
    }
    public ResponseVO updateCount(String value,
                                  String taskId) throws JsonProcessingException {
        if (taskId==null||value==null){
            return ResponseVO.error().msg("不合法的参数!");
        }
        System.out.println("更新盘点任务:"+taskId+":"+":"+":"+value);

        List<PrecisionTask> taskList=taskMapper.findByTaskId(Long.parseLong(taskId));
        if (taskList==null||taskList.size()==0){
            return ResponseVO.error().msg("不存在的任务号!");
        }
        PrecisionTask task=taskList.get(0);
        task.setStatus(WAIT_COUNT.index());
        task.setLastModified(new Date());
        task=taskMapper.save(task);
        ObjectMapper mapper=new ObjectMapper();
        System.out.println("重新盘点:"+task);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<PrecisionDescartesFields> vo=mapper.readValue(value, new TypeReference<List<PrecisionDescartesFields>>() {});
        try {
            fieldsService.save(vo,task.getId());
        }catch (Exception e){
            return ResponseVO.interError().msg("服务器内部错误!");
        }
        return ResponseVO.success().msg("新盘点任务提交成功!");
    }
    public ResponseVO startCount(String userId,
                                 String precisionId,
                                 String value,
                                 String company)throws Exception{
        if (userId==null||precisionId==null||company==null){
            return ResponseVO.error().msg("非法请求");
        }
        System.out.println("开始盘点任务:"+userId+":"+precisionId+":"+company+":"+value);

        List<PrecisionTask> existsTask=taskMapper.findByPrecisionId(Long.parseLong(precisionId));
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
        task=taskMapper.save(task);
        task.setTaskId(task.getId());
        taskMapper.save(task);
        ObjectMapper mapper=new ObjectMapper();
        System.out.println("创建盘点任务:"+task);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<PrecisionDescartesFields> vo=mapper.readValue(value, new TypeReference<List<PrecisionDescartesFields>>() {});
        try {
            fieldsService.save(vo,task.getId());
        }catch (Exception e){
            return ResponseVO.interError().msg("服务器内部错误!");
        }
        return ResponseVO.success().msg("提交盘点任务成功!");
    }
    public ResponseVO getAllResult(String taskId){
        List<PrecisionResult> LatestResult = resultService.getLatestResult(Long.parseLong(taskId));
        for (PrecisionResult precisionResult : LatestResult) {
            precisionResult.setFieldsName(precisionResult.getDescartesfields());
        }
        return ResponseVO.success().data(LatestResult).msg("获取所有盘点结果成功!");
    }
    public ResponseVO getCurrentTask(String taskId)throws Exception{

        List<PrecisionTask> byTaskId = taskMapper.findByTaskId(Long.parseLong(taskId));
        if (byTaskId!=null&&byTaskId.size()!=0){
            return ResponseVO.success().data(byTaskId.get(0)).msg("获取任务信息成功!");
        }
        return ResponseVO.error().msg("当前任务不存在!");
    }
    public ResponseVO getAllTask(String userId,
                                 String company,
                                 String pageIndex,
                                 String pageSize)throws Exception {
        System.out.println("getAllTask:"+userId+":"+pageIndex+":"+pageSize);
        return ResponseVO.success().data(taskService.getUserAllVisibleTask(userId, company,
                Long.parseLong(pageIndex)-1, Long.parseLong(pageSize))).msg("获取所有任务成功!");
    }
    public ResponseVO getLatestCondition(
            String taskId){
        return ResponseVO.success().data(fieldsService.queryMaxCondition(Long.parseLong(taskId)))
                .msg("获取最近的盘点条件成功!");
    }
}
