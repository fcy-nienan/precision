package com.cpiclife.precisionMarketing.dao;

import com.cpiclife.precisionMarketing.model.PrecisionMetaEnumInfo;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:15
 */
public interface EnumInterface {

    List<PrecisionMetaEnumInfo> getAllEnum();

    List<PrecisionMetaEnumInfo> findEnumByCode(String enumCode, Long fieldId);
}
