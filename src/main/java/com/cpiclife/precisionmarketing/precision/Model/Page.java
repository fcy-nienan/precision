package com.cpiclife.precisionmarketing.precision.Model;

import java.util.List;
import java.util.logging.Logger;

public class Page {
    private int recordCount;
    private int pageIndex;
    private int pageSize;
    private List<Object> result;
    private int pageCount;
    public Page(int recordCount, int pageIndex, int pageSize, List result) {
        this.recordCount = recordCount;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.result = result;
        if (recordCount%pageSize==0){
            pageCount=recordCount/pageSize;
        }else{
            pageCount=(recordCount/pageSize)+1;
        }
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Object> getResult() {
        return result;
    }

    public void setResult(List<Object> result) {
        this.result = result;
    }
}
