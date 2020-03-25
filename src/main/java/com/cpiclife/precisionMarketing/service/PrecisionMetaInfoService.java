package com.cpiclife.precisionMarketing.service;


import com.cpiclife.precisionMarketing.dao.EnumInterface;
import com.cpiclife.precisionMarketing.dao.MetaInterface;
import com.cpiclife.precisionMarketing.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/4 0:51
 */
@Component("precisionMetaInfoService")
public class PrecisionMetaInfoService {
	@Autowired 
	private MetaInterface metaMapper;
	@Autowired
	private EnumInterface enumMapper;
	public static volatile List<PrecisionSelectVO> selectVO=new ArrayList();
	private Object initLock=new Object();
	public Object flushCondition(){
		List<PrecisionSelectVO> result=getAllSelectVO();
	    if(result.size()!=0){
	    	selectVO=result;
	    }
	    return result;
	}
//    获取所有可选的条件//双重锁，只初始化一次，另外开一个线程每天完成更新这个selectVO
    public List<PrecisionSelectVO> getCanSelectCondition() throws Exception {
    	if(selectVO.size()==0){
    		synchronized(initLock){
    			if(selectVO.size()==0){
    		    	List<PrecisionSelectVO> result=getAllSelectVO();
			        selectVO=result;
    			}
    		}
    	}
    	return selectVO;
    }
    private List<PrecisionSelectVO> getAllSelectVO(){
    	List<PrecisionSelectVO> result=new ArrayList();
        List<PrecisionMetaInfo> metaInfos=metaMapper.getAllVariable();
        List<PrecisionMetaEnumInfo> metaEnumInfos=enumMapper.getAllEnum();
        for (PrecisionMetaInfo PrecisionMetaInfo : metaInfos) {
            List<PrecisionMetaEnumInfo> list=new ArrayList();
            for (PrecisionMetaEnumInfo PrecisionMetaEnumInfo : metaEnumInfos) {
                if (PrecisionMetaEnumInfo.getFieldId().equals(PrecisionMetaInfo.getFieldId())){
                    list.add(PrecisionMetaEnumInfo);
                }
            }
//            排序
            Collections.sort(list,new Comparator(){
				@Override
				public int compare(Object o1, Object o2) {
					// TODO Auto-generated method stub
					PrecisionMetaEnumInfo var1=(PrecisionMetaEnumInfo)o1;
					PrecisionMetaEnumInfo var2=(PrecisionMetaEnumInfo)o2;
					if(var1!=null&&var2!=null&&var1.getEnumCode()!=null&&var2.getEnumCode()!=null){
						return (int) (var1.getId()-var2.getId());
					}
					return 0;
				}
            	
            });
            List<String> res=operatorsList(PrecisionMetaInfo.getSupportOperators());
            PrecisionSelectVO vo=new PrecisionSelectVO(PrecisionMetaInfo,list,res);
            result.add(vo);
        }
        return result;
    }
    private List<String> operatorsList(String supportOperators){
        List<String> result=new ArrayList();
        for (String s : supportOperators.split(",")) {
            result.add(s);
        }
        return result;
    }
    public String getChineseValue(String fieldCode,String enumCode){
    	try{
    		List<PrecisionMetaInfo> meta=metaMapper.findVariableByCode(fieldCode);
    		String fieldName=meta.get(0).getFieldName();
    		if("date".equals(meta.get(0).getFieldType())){//如果是日期类型,由于日期没有枚举值,所以直接返回相应的中文表达式
    			return fieldName+","+enumCode;//param:final_date,2020-03-03     chineseValue: 保险到期日,2020-03-03
    		}
    		List<PrecisionMetaEnumInfo> enumList=enumMapper.findEnumByCode(enumCode, meta.get(0).getFieldId());
    		return fieldName+","+enumList.get(0).getEnumValue();
    	}catch(Exception e){
    		e.printStackTrace();
    		return fieldCode+"-"+enumCode;
    	}
    }
    
}
