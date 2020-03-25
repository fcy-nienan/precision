package com.cpiclife.precisionMarketing.dao.jpaImpl;

import com.cpiclife.precisionMarketing.dao.EnumInterface;
import com.cpiclife.precisionMarketing.dao.jpaImpl.jpa.EnumMapper;
import com.cpiclife.precisionMarketing.model.PrecisionMetaEnumInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:23
 */
@Component
public class EnumWrapper implements EnumInterface {
    @Autowired
    private EnumMapper enumMapper;
    @Override
    public List<PrecisionMetaEnumInfo> getAllEnum() {
        return enumMapper.findAll();
    }

    @Override
    public List<PrecisionMetaEnumInfo> findEnumByCode(String enumCode, Long fieldId) {
        return enumMapper.findByFieldIdAndEnumCode(fieldId,enumCode);
    }
}
