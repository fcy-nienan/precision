package com.cpiclife.precisionMarketing.model;

import javax.persistence.*;

@Entity(name="fields")
public class PrecisionDescartesFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long taskId;
    @Column
    private Long times;
    @Column
    private String fieldCode;
    @Column
    private String fieldType;
    @Column
    private String fieldName;
    @Column
    private String fieldId;
    @Column
    private String comparisonOperator;
    @Column
    private String fieldCstrValue;
    @Column
    private String variableType;
    @Column
    private String enumCode;//该条件的枚举值的英文

    public PrecisionDescartesFields() {
    }

    public PrecisionDescartesFields(Long id, Long taskId, Long times, String fieldCode, String fieldType, String fieldName, String fieldId, String comparisonOperator, String fieldCstrValue, String variableType, String enumCode) {
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

    public String getEnumCode() {
        return enumCode;
    }

    public void setEnumCode(String enumCode) {
        this.enumCode = enumCode;
    }
}
