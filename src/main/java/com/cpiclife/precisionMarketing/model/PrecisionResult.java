package com.cpiclife.precisionMarketing.model;

import javax.persistence.*;

@Entity(name="result")
public class PrecisionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long precisionId;
    @Column
    private Long times;
    @Column
    private Long taskId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @Column
    private Long resultId;
    @Column
    private Long amount;
    @Column
    private Long usedAmount;
    @Column
    private String precent;
    @Column
    private Long selected;
    @Column
    private String descartesfields;//限制条件的英文值
    private String fieldsName;//限制条件的中文值
    @Column
    private String guestGroupName;

    public PrecisionResult() {

    }

    public PrecisionResult(Long id, Long taskId, Long precisionId,Long times, Long resultId, Long amount, Long usedAmount, String precent, Long selected, String descartesfields, String guestGroupName) {
        this.precisionId=precisionId;
        this.id = id;
        this.taskId = taskId;
        this.times = times;
        this.resultId = resultId;
        this.amount = amount;
        this.usedAmount = usedAmount;
        this.precent = precent;
        this.selected = selected;
        this.descartesfields = descartesfields;
        this.guestGroupName = guestGroupName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrecisionId() {
        return precisionId;
    }

    public void setPrecisionId(Long precisionId) {
        this.precisionId = precisionId;
    }

    public Long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(Long usedAmount) {
        this.usedAmount = usedAmount;
    }

    public String getPrecent() {
        return precent;
    }

    public void setPrecent(String precent) {
        this.precent = precent;
    }

    public Long getSelected() {
        return selected;
    }

    public void setSelected(Long selected) {
        this.selected = selected;
    }

    public String getDescartesfields() {
        return descartesfields;
    }

    public void setDescartesfields(String descartesfields) {
        this.descartesfields = descartesfields;
    }

    public String getGuestGroupName() {
        return guestGroupName;
    }

    public void setGuestGroupName(String guestGroupName) {
        this.guestGroupName = guestGroupName;
    }

    public String getFieldsName() {
        return fieldsName;
    }

    public void setFieldsName(String fieldsName) {
        this.fieldsName = fieldsName;
    }
}
