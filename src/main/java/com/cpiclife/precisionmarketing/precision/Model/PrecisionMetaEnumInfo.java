package com.cpiclife.precisionmarketing.precision.Model;

import lombok.ToString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
@ToString
public class PrecisionMetaEnumInfo {
    private Long id;
    private String enumValue;
    private Long fieldId;
    private String enumCode;

    public static void main(String[] args) throws Exception {
        System.out.println(getDefault().size());
        System.out.println(getDefault());
    }
    public static List<PrecisionMetaEnumInfo> getDefault()throws Exception{
        BufferedReader reader=new BufferedReader(new InputStreamReader((PrecisionMetaEnumInfo.class.getResourceAsStream("/MetaEnumInfo.txt"))));
        String msg="";
        long id=0;
        List<PrecisionMetaEnumInfo> result=new ArrayList();
        while ((msg=reader.readLine())!=null){
            String[] splits=msg.split(":");
            PrecisionMetaEnumInfo info=new PrecisionMetaEnumInfo(id++, splits[0],Long.parseLong(splits[1]),splits[2]);
            result.add(info);
        }
        reader.close();
        return result;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrecisionMetaEnumInfo(Long id, String enumValue, Long fieldId, String enumCode) {
        this.id = id;
        this.enumValue = enumValue;
        this.fieldId = fieldId;
        this.enumCode = enumCode;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getEnumCode() {
        return enumCode;
    }

    public void setEnumCode(String enumCode) {
        this.enumCode = enumCode;
    }
}
