package com.cpiclife.precisionmarketing.precision.Controller;

import com.cpiclife.precisionmarketing.precision.Model.TaskEnum.*;
import com.cpiclife.precisionmarketing.precision.Mapper.*;
import com.cpiclife.precisionmarketing.precision.Model.*;
import com.cpiclife.precisionmarketing.precision.service.PrecisionDescartesFieldsService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionMetaInfoService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionResultService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionTaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.cpiclife.precisionmarketing.precision.Model.TaskEnum.*;

/*
 * Author:fcy
 * Date:2020/3/3 2:20
 */
@Controller
public class Hello {
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
    @RequestMapping("/index")
    public String index(){
        return "index";
    }
    @RequestMapping("/task")
    public String task(){
        return "task";
    }

    @RequestMapping("/condition")
    @ResponseBody
    public ResponseVO data() throws Exception {
        System.out.println(infoService.getCanSelectCondition());
        return ResponseVO.success().data(infoService.getCanSelectCondition()).msg("获取所有可选条件成功!");
    }
    @RequestMapping("/countFinished")
    @ResponseBody
    public ResponseVO countFinished(){
        task.CountFinished();
        return ResponseVO.success().msg("盘点完成!");
    }
    @RequestMapping("/checkTaskValid")
    @ResponseBody
    public ResponseVO checkTaskValid(@RequestParam("taskId")String taskId,
                                 @RequestParam("userId")String userId){
        System.out.println("检测用户id和任务id");
        return ResponseVO.success().data(taskService.checkTaskValid(Long.parseLong(taskId), userId));
    }
    @RequestMapping("/startSample")
    @ResponseBody
    public ResponseVO startSample(@RequestParam("value")String value,
                              @RequestParam("taskId")String taskId,
                              @RequestParam("userId")String userId)throws Exception{
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
    @RequestMapping("/updateCount")
    @ResponseBody
    public ResponseVO updateCount(@RequestParam("value")String value,
                              @RequestParam("taskId")String taskId) throws JsonProcessingException {
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
    //开始盘点
    @RequestMapping("/startCount")
    @ResponseBody
    public ResponseVO startCount(@RequestParam("userId")String userId,
                             @RequestParam("precisionId")String precisionId,
                             @RequestParam("value")String value,
                             @RequestParam("company")String company)throws Exception{
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
    @RequestMapping("/getAllResult")
    @ResponseBody
    public ResponseVO getAllResult(@RequestParam("taskId")String taskId){
        List<PrecisionResult> latestResult = resultService.getLatestResult(Long.parseLong(taskId));
        for (PrecisionResult precisionResult : latestResult) {
            precisionResult.setFieldsName(precisionResult.getDescartesfields());
        }
        return ResponseVO.success().data(latestResult).msg("获取所有盘点结果成功!");
    }

    @RequestMapping("/getCurrentTask")
    @ResponseBody
    public ResponseVO getCurrent(@RequestParam("taskId")String taskId)throws Exception{

        List<PrecisionTask> byTaskId = taskMapper.findByTaskId(Long.parseLong(taskId));
        if (byTaskId!=null&&byTaskId.size()!=0){
            return ResponseVO.success().data(byTaskId.get(0)).msg("获取任务信息成功!");
        }
        return ResponseVO.error().msg("当前任务不存在!");
    }
    //获取用户所有可见的任务
    @RequestMapping("/getAllTask")
    @ResponseBody
    public ResponseVO getAllTask(@RequestParam("userId")String userId,
                                          @RequestParam("company")String company,
                                          @RequestParam("pageIndex")String pageIndex,
                                          @RequestParam("pageSize")String pageSize)throws Exception {
        System.out.println("getAllTask:"+userId+":"+pageIndex+":"+pageSize);
        return ResponseVO.success().data(taskService.getUserAllVisibleTask(userId, company,
                Long.parseLong(pageIndex)-1, Long.parseLong(pageSize))).msg("获取所有任务成功!");
    }
    @RequestMapping("/getLastestCondition")
    @ResponseBody
    public ResponseVO getLastestCondition(
            @RequestParam("taskId")String taskId){

        return ResponseVO.success().data(fieldsService.queryMaxCondition(Long.parseLong(taskId)))
                .msg("获取最近的盘点条件成功!");
    }
}
