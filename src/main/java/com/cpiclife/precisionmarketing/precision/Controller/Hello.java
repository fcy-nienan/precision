package com.cpiclife.precisionmarketing.precision.Controller;


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
    public List<PrecisionSelectVO> data() throws Exception {
        System.out.println(infoService.getCanSelectCondition());
        return infoService.getCanSelectCondition();
    }
    @RequestMapping("/countFinished")
    @ResponseBody
    public String countfinished(){
        task.CountFinished();
        return "success";
    }
    @RequestMapping("/updateCount")
    @ResponseBody
    public String updateCount(@RequestParam("value")String value,
                              @RequestParam("taskId")String taskId) throws JsonProcessingException {
        if (taskId==null||value==null){
            return "failed";
        }
        System.out.println("更新盘点任务:"+taskId+":"+":"+":"+value);

        List<PrecisionTask> taskList=taskMapper.findByTaskId(Long.parseLong(taskId));
        if (taskList==null||taskList.size()==0){
            return "failed";
        }
        PrecisionTask task=taskList.get(0);
        task.setStatus(1l);
        task.setLastModified(new Date());
        task=taskMapper.save(task);
        ObjectMapper mapper=new ObjectMapper();
        System.out.println("创建盘点任务:"+task);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<PrecisionDescartesFields> vo=mapper.readValue(value, new TypeReference<List<PrecisionDescartesFields>>() {});
        try {
            fieldsService.save(vo,task.getId());
        }catch (Exception e){
            return "failed";
        }
        return "success";
    }
    @Data
    @AllArgsConstructor
    class VO{
        private String code;
    }
    //开始盘点
    @RequestMapping("/startCount")
    @ResponseBody
    public VO startCount(@RequestParam("userId")String userId,
                             @RequestParam("precisionId")String precisionId,
                             @RequestParam("value")String value,
                             @RequestParam("company")String company)throws Exception{
        if (userId==null||precisionId==null||company==null){
            return new VO("failed");
        }
        System.out.println("开始盘点任务:"+userId+":"+precisionId+":"+company+":"+value);

        List<PrecisionTask> existsTask=taskMapper.findByPrecisionId(Long.parseLong(precisionId));
        if (existsTask != null && existsTask.size() != 0) {
            return new VO("existed");
        }

        PrecisionTask task=new PrecisionTask();
        task.setUserId(userId);
        task.setCompany(company);
        task.setPrecisionId(Long.parseLong(precisionId));
        task.setStatus(1l);
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
            return new VO("failed");
        }
        return new VO("success");
    }
    @RequestMapping("/cancelCount")
    @ResponseBody
    public String cancelCount(@RequestParam("taskId")String taskId){
        List<PrecisionTask> byTaskId = taskMapper.findByTaskId(Long.parseLong(taskId));
        if (byTaskId!=null&&byTaskId.size()!=0){
            PrecisionTask task = byTaskId.get(0);
            task.setStatus(0l);
            taskMapper.save(task);
            return "success";
        }
        return "failed";
    }

    @RequestMapping("/getAllResult")
    @ResponseBody
    public List<PrecisionResult> getAllResult(@RequestParam("taskId")String taskId){
        List<PrecisionResult> lastestResult = resultService.getLastestResult(Long.parseLong(taskId));
        for (PrecisionResult precisionResult : lastestResult) {
            precisionResult.setFieldsName(precisionResult.getDescartesfields());
        }
        return lastestResult;
    }

    @RequestMapping("/getCurrentStatus")
    @ResponseBody
    public String getCurrent(@RequestParam("taskId")String taskId)throws Exception{

        List<PrecisionTask> byTaskId = taskMapper.findByTaskId(Long.parseLong(taskId));
        if (byTaskId!=null&&byTaskId.size()!=0){
            return byTaskId.get(0).getStatus().toString();
        }
        return "failed";
    }
    //获取用户所有可见的任务
    @RequestMapping("/getAllTask")
    @ResponseBody
    public List<PrecisionTask> getAllTask(@RequestParam("userId")String userId,
                                          @RequestParam("company")String company,
                                          @RequestParam("pageIndex")String pageIndex,
                                          @RequestParam("pageSize")String pageSize)throws Exception {
        System.out.println("getAllTask:"+userId+":"+pageIndex+":"+pageSize);
        return taskService.getUserAllVisibleTask(userId,company,Long.parseLong(pageIndex),
                Long.parseLong(pageSize));
    }
    @RequestMapping("/getLastestCondition")
    @ResponseBody
    public List<PrecisionDescartesFields> getLastestCondition(
            @RequestParam("taskId")String taskId){

        return fieldsService.queryMaxCondition(Long.parseLong(taskId));
    }
}
