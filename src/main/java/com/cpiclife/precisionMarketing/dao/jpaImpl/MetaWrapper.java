package com.cpiclife.precisionMarketing.dao.jpaImpl;

import com.cpiclife.precisionMarketing.dao.MetaInterface;
import com.cpiclife.precisionMarketing.dao.jpaImpl.jpa.MetaMapper;
import com.cpiclife.precisionMarketing.model.PrecisionMetaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/26 1:22
 */
@Component
public class MetaWrapper implements MetaInterface {
    @Autowired
    private MetaMapper metaMapper;
    @Override
    public List<PrecisionMetaInfo> getAllVariable() {
        return metaMapper.findAll();
    }

    @Override
    public List<PrecisionMetaInfo> findVariableByCode(String fieldCode) {
        return metaMapper.findByFieldCode(fieldCode);
    }
}
