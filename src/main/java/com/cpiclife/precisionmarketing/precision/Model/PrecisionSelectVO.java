package com.cpiclife.precisionmarketing.precision.Model;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
@ToString
public class PrecisionSelectVO {
    private PrecisionMetaInfo metaInfo;
    private List<PrecisionMetaEnumInfo> enumInfo;

    public PrecisionSelectVO(PrecisionMetaInfo metaInfo, List<PrecisionMetaEnumInfo> enumInfo) {
        this.metaInfo = metaInfo;
        this.enumInfo = enumInfo;
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

    public static void main(String[] args) throws Exception {
        System.out.println(makeData());
    }
    public static List<PrecisionSelectVO> makeData() throws Exception {
        List<PrecisionSelectVO> result=new ArrayList<>();
        List<PrecisionMetaInfo> metaInfos=PrecisionMetaInfo.getDefault();
        List<PrecisionMetaEnumInfo> metaEnumInfos=PrecisionMetaEnumInfo.getDefault();
        for (PrecisionMetaInfo PrecisionMetaInfo : metaInfos) {
            List<PrecisionMetaEnumInfo> list=new ArrayList<>();
            for (PrecisionMetaEnumInfo PrecisionMetaEnumInfo : metaEnumInfos) {
                if (PrecisionMetaEnumInfo.getFieldId().equals(PrecisionMetaInfo.getFieldId())){
                    list.add(PrecisionMetaEnumInfo);
                }
            }
            PrecisionSelectVO vo=new PrecisionSelectVO(PrecisionMetaInfo,list);
            result.add(vo);
        }
        return result;
    }
}
