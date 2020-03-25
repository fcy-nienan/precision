package com.cpiclife.precisionMarketing.dao.jpaImpl.jpa;

import com.cpiclife.precisionMarketing.model.PrecisionMetaInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface MetaMapper extends JpaRepository<PrecisionMetaInfo,Long> {
    @Query(nativeQuery = true,value = "select * from metainfo")
    public List<PrecisionMetaInfo> findAll();
    public List<PrecisionMetaInfo> findByFieldId(long fieldId);
    public List<PrecisionMetaInfo> findByFieldCode(String fieldCode);

}
