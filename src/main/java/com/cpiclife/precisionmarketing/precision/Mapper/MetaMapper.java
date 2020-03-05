package com.cpiclife.precisionmarketing.precision.Mapper;

import com.cpiclife.precisionmarketing.precision.Model.PrecisionMetaInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import java.util.List;

public interface MetaMapper extends JpaRepository<PrecisionMetaInfo,Long> {

    public List<PrecisionMetaInfo> findByFieldId(long fieldId);
    public List<PrecisionMetaInfo> findByFieldCode(String fieldCode);

}
