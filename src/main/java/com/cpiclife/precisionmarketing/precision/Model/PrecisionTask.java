package com.cpiclife.precisionmarketing.precision.Model;

import javax.persistence.*;
import java.util.Date;
@Entity(name="task")
public class PrecisionTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long precisionId;
    @Column
    private Long taskId;
    @Column
    private Long status;
    private String statusName;
    @Column
    private String userId;
    @Column
    private String company;
    @Column
    private Date insertDate;
    @Column
    private Date lastModified;

    public PrecisionTask( ) {

    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public PrecisionTask(Long id, Long precisionId, Long taskId, Long status, String userId, String company, Date insertDate, Date lastModified) {
        this.id = id;
        this.precisionId = precisionId;
        this.taskId = taskId;
        this.status = status;
        this.userId = userId;
        this.company = company;
        this.insertDate = insertDate;
        this.lastModified = lastModified;
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

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
