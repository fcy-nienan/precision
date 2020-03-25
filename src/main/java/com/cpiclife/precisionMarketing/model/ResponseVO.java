package com.cpiclife.precisionMarketing.model;

/*
 * Author:fcy
 * Date:2020/3/7 11:41
 */
public class ResponseVO {
    public String code;
    public Object data;
    public String msg;
    public ResponseVO data(Object data){
        this.data=data;
        return this;
    }
    public ResponseVO code(String code){
        this.code=code;
        return this;
    }
    public ResponseVO msg(String msg){
        this.msg=msg;
        return this;
    }

    public ResponseVO(String code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public ResponseVO(String code) {
        this.code = code;
    }

    public ResponseVO(){

    }
    public static ResponseVO forbid(){
        return new ResponseVO("401").msg("forbid");
    }
    public static ResponseVO success(){
        return new ResponseVO("200").msg("请求成功!");
    }
    public static ResponseVO error(){
        return new ResponseVO("400").msg("error");
    }
    public static ResponseVO interError(){
        return new ResponseVO("500").msg("interError");
    }
}
