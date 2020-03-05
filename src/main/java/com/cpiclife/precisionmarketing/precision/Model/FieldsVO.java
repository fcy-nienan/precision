package com.cpiclife.precisionmarketing.precision.Model;

import javax.persistence.*;
import java.util.List;

public class FieldsVO {
    private Long id;
    private Long taskId;
    private Long times;
    private String fieldCode;
    private String fieldType;
    private String fieldName;
    private String fieldId;
    private String comparisonOperator;
    private String fieldCstrValue;
    private String variableType;
    private List<Object> enumCode;

    public FieldsVO() {
    }
    public static PrecisionDescartesFields transfer(FieldsVO vo){
        PrecisionDescartesFields fields=new PrecisionDescartesFields();
        fields.setId(vo.id);
        fields.setTaskId(vo.taskId);
        fields.setTimes(vo.times);
        fields.setFieldCstrValue(vo.fieldCstrValue);
        fields.setFieldCode(vo.fieldCode);
        fields.setFieldType(vo.fieldType);
        fields.setFieldName(vo.fieldName);
        fields.setFieldId(vo.fieldId);
        fields.setComparisonOperator(vo.comparisonOperator);
        fields.setVariableType(vo.variableType);
        List<Object> en=vo.getEnumCode();
        if (en.size()==1){
            fields.setEnumCode(en.get(0).toString());
        }else{
            StringBuilder builder=new StringBuilder();
            if (vo.comparisonOperator.equals("in")){
                builder.append("(");
            }
            for (int i=0;i<en.size();i++){
                builder.append(en.get(i)).append(",");
            }
            builder.deleteCharAt(builder.length()-1);
            if (vo.comparisonOperator.equals("in")){
                builder.append("(");
            }
            fields.setEnumCode(builder.toString());
        }
        return fields;
    }
    public FieldsVO(Long id, Long taskId, Long times, String fieldCode, String fieldType, String fieldName, String fieldId, String comparisonOperator, String fieldCstrValue, String variableType, List<Object> enumCode) {
        this.id = id;
        this.taskId = taskId;
        this.times = times;
        this.fieldCode = fieldCode;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.fieldId = fieldId;
        this.comparisonOperator = comparisonOperator;
        this.fieldCstrValue = fieldCstrValue;
        this.variableType = variableType;
        this.enumCode = enumCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(String comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public String getFieldCstrValue() {
        return fieldCstrValue;
    }

    public void setFieldCstrValue(String fieldCstrValue) {
        this.fieldCstrValue = fieldCstrValue;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public List<Object> getEnumCode() {
        return enumCode;
    }

    public void setEnumCode(List<Object> enumCode) {
        this.enumCode = enumCode;
    }
}
