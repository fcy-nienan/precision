package com.cpiclife.precisionMarketing.servlet;

import com.cpiclife.precisionMarketing.model.*;
import com.cpiclife.precisionMarketing.service.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.cpiclife.precisionMarketing.model.TaskEnum.WAIT_COUNT;
import static com.cpiclife.precisionMarketing.model.TaskEnum.WAIT_SAMPLE;

/*
 * Author:fcy
 * Date:2020/3/7 22:00
 */
public class PrecisionServlet extends HttpServlet {
    /**
	 * 
	 */
	private ServletConfig config;	
	public void init(ServletConfig config){
		this.config = config;
	}
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
//    根据字符串中的数组获取相应的属性值
    public String[] getParams(String[] strings,HttpServletRequest request) throws UnsupportedEncodingException{
//    	try {
//			request.setCharacterEncoding("utf-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        String[] result=new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            String parameter = request.getParameter(strings[i]);
            parameter=URLDecoder.decode(parameter==null?"":parameter,"UTF-8");
            result[i]=parameter;
        }
        return result;
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
                try {
                    String[] requestValues=getParams(wrapper.params,req);

                    System.out.println("开始调用方法:"+wrapper.method+"---请求参数属性:"+Arrays.toString(wrapper.params)+"---请求参数值:"+ Arrays.toString(requestValues));
                    long start=System.currentTimeMillis();
//                    该方法有参数
                    if (requestValues!=null) {
                        result = declaredMethods[i].invoke(methodInvocation, requestValues);
                    }else{//该方法无参数
                        result=declaredMethods[i].invoke(methodInvocation);
                    }
                    System.out.println(wrapper.method+"方法调用结束:花费时间:"+(System.currentTimeMillis()-start));
                } catch (Exception e){
                    e.printStackTrace();
                    return ResponseVO.error().msg("调用方法失败!");
                }
                return (ResponseVO) result;
            }
        }
        System.out.println("未找到请求映射的方法名:"+wrapper);
        return ResponseVO.error().msg("请求失败--未找到相应的方法!");
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type=req.getParameter("type");
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(this.config.getServletContext());
        MethodInvocation methodInvocation = (MethodInvocation)webApplicationContext.getBean("methodInvocation");
//        调用后台方法
        Object o = invokeMethod(req,type, methodInvocation);
//        写入响应流
        ObjectMapper mapper=new ObjectMapper();
//        将返回对象转化为json字符串
        String s = mapper.writeValueAsString(o);
//        返回给前台
        resp.setContentType("application/json; charset=utf-8");
        OutputStream outputStream = resp.getOutputStream();
        outputStream.write(s.getBytes("utf-8"));
    }
}
