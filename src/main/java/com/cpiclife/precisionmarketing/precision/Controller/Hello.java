package com.cpiclife.precisionmarketing.precision.Controller;


import com.cpiclife.precisionmarketing.precision.Model.*;
import com.cpiclife.precisionmarketing.precision.service.PrecisionMetaInfoService;
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
    private PrecisionMetaInfoService infoService;
    @RequestMapping("/index")
    public String index(){
        return "index";
    }
    @RequestMapping("/iview")
    public String tst(){
        return "iview";
    }
    @RequestMapping("/multi")
    public String multi(){return "multi";}
    @RequestMapping("/precisionTask")
    public String task(){
        return "precisionTask";
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
    @RequestMapping("/cancel")
    @ResponseBody
    public String cancelCount(@RequestParam("userId")String userId,
                              @RequestParam("taskId")String taskId)throws Exception{
        return "success";
    }
}
