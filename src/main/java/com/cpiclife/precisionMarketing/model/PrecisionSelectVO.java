package com.cpiclife.precisionMarketing.model;


import java.util.ArrayList;
import java.util.List;
public class PrecisionSelectVO {
    private PrecisionMetaInfo metaInfo;
    private List<PrecisionMetaEnumInfo> enumInfo;
    private List<String> operators;

    public PrecisionSelectVO() {
		super();
	}

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

    @Override
	public String toString() {
		return "PrecisionSelectVO [enumInfo=" + enumInfo + ", metaInfo="
				+ metaInfo + ", operators=" + operators + "]";
	}

	public List<String> getOperators() {
        return operators;
    }

    public void setOperators(List<String> operators) {
        this.operators = operators;
    }
}
