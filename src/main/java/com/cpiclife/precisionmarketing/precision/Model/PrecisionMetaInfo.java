package com.cpiclife.precisionmarketing.precision.Model;

import lombok.ToString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@ToString
public class PrecisionMetaInfo {
    private Long id;
    private Long fieldId;
    private String fieldCode;
    private String fieldName;
    private String fieldType;
    private String supportOperators;
    private List<String> operatorsList;

    public static void main(String[] args) throws Exception {
        System.out.println(getDefault());
    }
    public static List<PrecisionMetaInfo> getDefault()throws Exception{
        BufferedReader reader=new BufferedReader(new InputStreamReader((PrecisionMetaInfo.class.getResourceAsStream("/MetaInfo.txt"))));
        String msg="";
        long id=0;
        List<PrecisionMetaInfo> result=new ArrayList();
        while ((msg=reader.readLine())!=null){
            String[] splits=msg.split(":");
            PrecisionMetaInfo info=new PrecisionMetaInfo(id++, Long.parseLong(splits[0]),splits[1],splits[2],splits[3],splits[4]);
            result.add(info);
        }
        reader.close();
        return result;
    }
    public List<String> operatorsList(){
        List<String> result=new ArrayList<>();
        for (String s : this.supportOperators.split(",")) {
            result.add(s);
        }
        System.out.println(Arrays.toString(result.toArray()));
        return result;
    }

    public PrecisionMetaInfo(Long id, Long fieldId, String fieldCode, String fieldName, String fieldType, String supportOperators) {
        this.id = id;
        this.fieldId = fieldId;
        this.fieldCode = fieldCode;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.supportOperators = supportOperators;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getSupportOperators() {
        return supportOperators;
    }

    public void setSupportOperators(String supportOperators) {
        this.supportOperators = supportOperators;
    }
}
