package com.cpiclife.precisionmarketing.precision.Controller;


import com.cpiclife.precisionmarketing.precision.Model.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/3 2:20
 */
@Controller
public class Hello {
    @RequestMapping("/index")
    public String index(){
        return "index";
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

        return PrecisionSelectVO.makeData();
    }
    @RequestMapping("/Condition")
    public String Condition(HttpServletRequest request) throws Exception {
        System.out.println(request.getServletContext().getRealPath("/"));
        return "Condition";
    }
}
