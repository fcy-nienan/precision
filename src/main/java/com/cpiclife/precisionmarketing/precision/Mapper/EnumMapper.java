package com.cpiclife.precisionmarketing.precision.Mapper;

import com.cpiclife.precisionmarketing.precision.Model.PrecisionMetaEnumInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import java.util.List;

public interface EnumMapper extends JpaRepository<PrecisionMetaEnumInfo,Long> {
    public List<PrecisionMetaEnumInfo> findByFieldId(Long fieldId);
}
