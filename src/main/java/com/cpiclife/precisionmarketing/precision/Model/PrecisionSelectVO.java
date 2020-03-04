package com.cpiclife.precisionmarketing.precision.Model;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
@ToString
public class PrecisionSelectVO {
    private PrecisionMetaInfo metaInfo;
    private List<PrecisionMetaEnumInfo> enumInfo;
    private List<String> operators;

    public PrecisionSelectVO(PrecisionMetaInfo metaInfo, List<PrecisionMetaEnumInfo> enumInfo, List<String> operators) {
        this.metaInfo = metaInfo;
        this.enumInfo = enumInfo;
        this.operators = operators;
    }

    public PrecisionMetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(PrecisionMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    public List<PrecisionMetaEnumInfo> getEnumInfo() {
        return enumInfo;
    }

    public void setEnumInfo(List<PrecisionMetaEnumInfo> enumInfo) {
        this.enumInfo = enumInfo;
    }

    public List<String> getOperators() {
        return operators;
    }

    public void setOperators(List<String> operators) {
        this.operators = operators;
    }
}
