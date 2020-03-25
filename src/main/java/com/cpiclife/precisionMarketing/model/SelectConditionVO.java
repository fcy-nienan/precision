package com.cpiclife.precisionMarketing.model;

/*
 * Author:fcy
 * Date:2020/3/6 2:07
 */
public class SelectConditionVO {
    private String fieldCode;
    private String operator;
    private String enumValue;

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }

    public SelectConditionVO(String fieldCode, String operator, String enumValue) {
        this.fieldCode = fieldCode;
        this.operator = operator;
        this.enumValue = enumValue;
    }
}
