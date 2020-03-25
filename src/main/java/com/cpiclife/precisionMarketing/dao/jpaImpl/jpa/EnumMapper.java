package com.cpiclife.precisionMarketing.dao.jpaImpl.jpa;

import com.cpiclife.precisionMarketing.model.PrecisionMetaEnumInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface EnumMapper extends JpaRepository<PrecisionMetaEnumInfo,Long> {
    public List<PrecisionMetaEnumInfo> findByFieldId(Long fieldId);
    @Query(nativeQuery = true,value = "select * from enuminfo")
    public List<PrecisionMetaEnumInfo> findALl();
    public List<PrecisionMetaEnumInfo> findByFieldIdAndEnumCode(long fieldId,String enumCode);
}
