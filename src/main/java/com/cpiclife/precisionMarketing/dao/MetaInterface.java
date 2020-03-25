package com.cpiclife.precisionMarketing.dao;

import com.cpiclife.precisionMarketing.model.PrecisionMetaInfo;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:15
 */
public interface MetaInterface {

    List<PrecisionMetaInfo> getAllVariable();

    List<PrecisionMetaInfo> findVariableByCode(String fieldCode);
}
