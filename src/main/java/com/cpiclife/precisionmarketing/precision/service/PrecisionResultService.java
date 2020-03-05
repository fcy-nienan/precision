package com.cpiclife.precisionmarketing.precision.service;

import com.cpiclife.precisionmarketing.precision.Model.PrecisionDescartesFields;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionResult;
import com.cpiclife.precisionmarketing.precision.Model.PrecisionTask;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/*
 * Author:fcy
 * Date:2020/3/4 0:52
 */
@Component("precisionResultService")
public class PrecisionResultService {
    private Long id;
    private Long taskId;
    private Long times;
    private Long resultId;
    private Long amount;
    private Long usedAmount;
    private String precent;
    private Long selected;
    private String descartesfields;
    private String guestGroupName;
    private static List<PrecisionResult> data=new ArrayList<>();
    private static long autoCount;
    public void insert(PrecisionResult result){
        result.setId(autoCount++);
        data.add(result);
    }
    public PrecisionResult queryById(long id){
        for (int i=0;i<data.size();i++){
            if (data.get(i).getId()==id){
                return data.get(i);
            }
        }
        return null;
    }
    public void save(PrecisionResult result){
        for(int i=0;i<data.size();i++){
            if (data.get(i).getId()==result.getId()){
                data.remove(i);
                data.add(result);
            }
        }
    }
    public long getMax(Long taskId){
        long max=0;
        for (int i=0;i<data.size();i++){
            max=Math.max(data.get(i).getTimes(),max);
        }
        return max;
    }
    public List<PrecisionResult> getUserSelected(long taskId){
        long max=getMax(taskId);
        List<PrecisionResult> result=new ArrayList<>();
        for (int i=0;i<data.size();i++){
            if (data.get(i).getTimes()==max&&data.get(i).getSelected()==1){
                result.add(data.get(i));
            }
        }
        return result;
    }
//    查询最新的盘点结果
    public List<PrecisionResult> getLastestResult(long taskId){
        long max=getMax(taskId);
        List<PrecisionResult> result=new ArrayList<>();
        for (int i=0;i<data.size();i++){
            if (data.get(i).getTimes()==max){
                result.add(data.get(i));
            }
        }
        return result;
    }
}
