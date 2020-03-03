package com.cpiclife.precisionmarketing.precision.Model;

import java.util.Date;

public class PrecisionTask {
    private Long id;
    private Long precisionId;
    private Long taskId;
    private Long status;
    private String userId;
    private String company;
    private Date insertMane;
    private Date lastModified;

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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Date getInsertMane() {
        return insertMane;
    }

    public void setInsertMane(Date insertMane) {
        this.insertMane = insertMane;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
