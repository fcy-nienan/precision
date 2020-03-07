package com.cpiclife.precisionmarketing.precision.Model;

/*
 * Author:fcy
 * Date:2020/3/7 20:35
 */
public enum TaskEnum {
    CANCELED(0,"已取消"),
    WAIT_COUNT(1,"待盘点"),
    COUNTING(2,"盘点中"),
    COUNT_FINISHED(3,"盘点完成"),
    WAIT_SAMPLE(4,"待抽样"),
    SAMPLING(5,"抽样中"),
    UPLOADING(6,"上载成功");
    long index;
    String msg;
    TaskEnum(int index,String msg){
        this.index=index;
        this.msg=msg;
    }
    public long index() {
        return index;
    }
    public String msg() {
        return msg;
    }
}
