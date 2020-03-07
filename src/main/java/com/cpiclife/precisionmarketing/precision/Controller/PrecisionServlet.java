package com.cpiclife.precisionmarketing.precision.Controller;

import com.cpiclife.precisionmarketing.precision.Model.PrecisionDescartesFields;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionResult;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import com.cpiclife.precisionmarketing.precision.Model.ResponseVO;
import com.cpiclife.precisionmarketing.precision.service.PrecisionTaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.cpiclife.precisionmarketing.precision.Model.TaskEnum.WAIT_COUNT;
import static com.cpiclife.precisionmarketing.precision.Model.TaskEnum.WAIT_SAMPLE;

/*
 * Author:fcy
 * Date:2020/3/7 22:00
 */
public class PrecisionServlet extends HttpServlet {
    public String[] getParams(String[] strings,HttpServletRequest request){
        String[] result=new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            String parameter = request.getParameter(strings[i]);
            if (parameter!=null){
                result[i]=parameter;
            }
        }
        return result;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    private ResponseVO invokeMethod(HttpServletRequest req,String type,MethodInvocation methodInvocation){
        Method[] declaredMethods = MethodInvocation.class.getDeclaredMethods();
        MethodInvocation.methodWrapper wrapper=MethodInvocation.getInvoke(type);
        if (wrapper==null){
            System.out.println("请求方法不存在!"+type);
            return ResponseVO.error().msg("请求方法不存在!"+type);
        }
        Object result=null;
        for(int i=0;i<declaredMethods.length;i++){
            if (declaredMethods[i].getName().equals(wrapper.method)){
                String[] requestValues=getParams(wrapper.params,req);
                System.out.println("开始调用方法:"+wrapper.method+"---请求参数属性:"+Arrays.toString(wrapper.params)+"---请求参数值:"+ Arrays.toString(requestValues));
                try {
                    if (requestValues!=null) {
                        result = declaredMethods[i].invoke(methodInvocation, requestValues);
                    }else{
                        result=declaredMethods[i].invoke(methodInvocation);
                    }
                } catch (Exception e){
                    System.out.println("调用方法失败!");
                    e.printStackTrace();
                }
                return (ResponseVO) result;
            }
        }
        System.out.println("未找到请求映射的方法名:"+wrapper);
        return ResponseVO.error().msg("请求失败!");
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("doPost");
        String type=req.getParameter("type");
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        PrecisionTaskService taskService = (PrecisionTaskService)webApplicationContext.getBean("precisionTaskService");
        MethodInvocation methodInvocation = (MethodInvocation)webApplicationContext.getBean("methodInvocation");
//        调用后台方法
        Object o = invokeMethod(req,type, methodInvocation);
//        写入响应流
        ObjectMapper mapper=new ObjectMapper();
        String s = mapper.writeValueAsString(o);
        resp.setContentType("application/json; charset=utf-8");
        OutputStream outputStream = resp.getOutputStream();
        outputStream.write(s.getBytes("utf-8"));
    }
}
