package com.cpiclife.precisionmarketing.precision.Controller;


import com.cpiclife.precisionmarketing.precision.Mapper.*;
import com.cpiclife.precisionmarketing.precision.Model.*;
import com.cpiclife.precisionmarketing.precision.service.PrecisionDescartesFieldsService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionMetaInfoService;
import com.cpiclife.precisionmarketing.precision.service.PrecisionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

    @RequestMapping("/data")
    @ResponseBody
    public List<PrecisionSelectVO> data() throws Exception {
        System.out.println(infoService.getCanSelectCondition());
        return infoService.getCanSelectCondition();
    }
    private static HashMap<String,String> map=new HashMap();
    @RequestMapping("/save")
    @ResponseBody
    public String save(@RequestParam("value")String jsonString,@RequestParam("userId")String userId,
                       @RequestParam("taskId")String taskId)throws Exception {
        map.put(userId+":"+taskId,jsonString);
        System.out.println(userId+":"+taskId+"----"+jsonString);
        return "success";
    }
    @RequestMapping("/selectedCondition")
    @ResponseBody
    public String selectedCondition(@RequestParam("userId")String userId,
                                    @RequestParam("taskId")String taskId)throws Exception{
        String str=userId+":"+taskId;
        System.out.println(userId+":"+taskId);
        return map.get(str);
    }
    //取消盘点
    @RequestMapping("/cancel")
    @ResponseBody
    public String cancelCount(@RequestParam("userId")String userId,
                              @RequestParam("taskId")String taskId)throws Exception{
        return "success";
    }
    //开始盘点
    @RequestMapping("/startCount")
    @ResponseBody
    public String startCount(@RequestParam("userId")String userId,
                             @RequestParam("taskId")String taskId)throws Exception{

        taskMapper.updateStatus(Long.parseLong(userId),
                Long.parseLong((taskId)),
                0l);
        System.out.println("开始盘点任务:"+userId+":"+taskId);
//        保存盘点任务
//        修改盘点状态
//        修改任务状态
        return "success";
    }


    //获取用户所有可见的任务
    @RequestMapping("/getAllTask")
    @ResponseBody
    public Page getAllTask(@RequestParam("userId")String userId,
                                          @RequestParam("company")String company,
                                          @RequestParam("pageIndex")String pageIndex,
                                          @RequestParam("pageSize")String pageSize)throws Exception {
        System.out.println("getAllTask:"+userId+":"+pageIndex+":"+pageSize);
        return taskService.getData(userId,company,
                Integer.parseInt(pageIndex),
                Integer.parseInt(pageSize));
    }
    @RequestMapping("/getLastestCondition")
    @ResponseBody
    public List<PrecisionDescartesFields> getLastestCondition(@RequestParam("taskId")String taskId){
        return fieldsService.queryMaxCondition(Long.parseLong(taskId));
    }
}
