package com.cpiclife.precisionMarketing.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
	public static void sendToOtherSystem(String json,String url){
		//重试三次
        int max=3;
        while(max>0){
            try{
            	String msg=HttpClient.SendJson(json,url);
            	System.out.println("httpClient:Msg:"+msg);
            	break;
            }catch(Exception e){
            	max=max-1;
            	e.printStackTrace();
            }
        }
	}
	public static String SendJson(String json,String url)throws Exception{
		OutputStreamWriter outputStream = null;
        BufferedReader responseBuffer = null;
        //String url="http://29.4.147.17:31001/api/salesNotice/updateStatus";
        URL targetUrl=new URL(url);
        HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
        System.out.println("连接的url:"+url);
        httpConnection.setDoOutput(true);
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/json");

        outputStream = new OutputStreamWriter(httpConnection.getOutputStream(), "UTF-8");
        outputStream.write(json);
        outputStream.flush();
        if (httpConnection.getResponseCode() != 200) {
        	System.out.println("响应码:"+httpConnection.getResponseCode());
            return "";
        } else {
            responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream()), "UTF-8"));
            StringBuilder output=new StringBuilder();
            String msg="";
            while ((msg = responseBuffer.readLine()) != null) {
                output.append(msg);
            }
            httpConnection.disconnect();
            responseBuffer.close();
            outputStream.close();
            return output.toString();
        }

    }
}
