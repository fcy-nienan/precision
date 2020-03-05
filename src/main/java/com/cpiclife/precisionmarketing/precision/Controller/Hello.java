package com.cpiclife.precisionmarketing.precision.Controller;


import com.cpiclife.precisionmarketing.precision.Mapper.*;
import com.cpiclife.precisionmarketing.precision.Model.*;
import com.cpiclife.precisionmarketing.precision.service.PrecisionDescartesFieldsService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionMetaInfoService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionTaskService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    //开始盘点
    @RequestMapping("/startCount")
    @ResponseBody
    public String startCount(@RequestParam("userId")String userId,
                             @RequestParam("precisionId")String precisionId,
                             @RequestParam("value")String value,
                             @RequestParam("company")String company)throws Exception{
        if (userId==null||precisionId==null||company==null){
            return "failed";
        }
        System.out.println("开始盘点任务:"+userId+":"+precisionId+":"+company+":"+value);

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
//        System.out.println("条件:"+vo);
//        List<PrecisionDescartesFields> descartesFields=new ArrayList<>();
//        for (FieldsVO fieldsVO : vo) {
//            descartesFields.add(FieldsVO.transfer(fieldsVO));
//        }
//        System.out.println("转换后的条件:"+descartesFields);
        try {
            fieldsService.save(vo,task.getId());
        }catch (Exception e){
            return "failed";
        }
        return "success";
    }

    public static void main(String[] args) {
        String s="[{\"fieldCode\":\"subCompany\",\"operator\":\"=\",\"enumValue\":\"10294\"},{\"fieldCode\":\"supCompany\",\"operator\":\"in\",\"enumValue\":[\"94389\"]},{\"fieldCode\":\"sex\",\"operator\":\"=\",\"enumValue\":\"0\"}]";

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
