package com.cpiclife.precisionmarketing.precision.service;


import com.cpiclife.precisionmarketing.precision.Model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/4 0:51
 */
@Component("precisionMetaInfoService")
public class PrecisionMetaInfoService {
    public List<PrecisionSelectVO> makeData() throws Exception {
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
