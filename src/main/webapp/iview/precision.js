var testUrl='http://29.23.13.116:80/zxgl';
var localUrl='http://localhost:31001/zxgl';
var productUrl='http://10.180.165.15:31001/zxgl';
var developUrl='http://29.23.32.38:31001/zxgl';
var outerUrl='http://localhost:8080';
var baseUrl=outerUrl;
function initField(){
	o=new Object();
    o.id=''
    o.taskId=''
    o.times=''
    o.variableType=''
    o.fieldCstrValue='';
    o.fieldCode='';
    o.fieldId='';
    o.fieldName='';
    o.fieldType='';
    return o;
}
function dateFtt(fmt,date) { 
    var o = {
        "M+" : date.getMonth()+1,                 //月份
        "d+" : date.getDate(),                    //日
        "h+" : date.getHours(),                   //小时
        "m+" : date.getMinutes(),                 //分
        "s+" : date.getSeconds(),                 //秒
        "q+" : Math.floor((date.getMonth()+3)/3), //季度
        "S"  : date.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
}
function computeStatus(status){
    if(status==0){
        return "已取消";
    }else if(status==1){
        return "待盘点";
    }else if(status==2){
        return "盘点中";
    }else if(status==3){
        return "盘点完成";
    }else if(status==10){
        return "待下发";
    }else if (status==11){
        return "下发中";
    }else if(status==12){
        return "下发完成";
    }else{
        return status;
    }
}
function computeOperator(operator){
    if(operator=='in'){
        return "包含"
    }else if(operator=='='){
        return "等于";
    }else if(operator=='>'){
    	return "大于";
    }else if(operator=='<'){
    	return "小于";
    }else if(operator=='[]'){
    	return "区间";
    }else if(operator=='!='){
    	return "不等于";
    }else if(operator=='notin'){
    	return "不包含";
    }else{
    	return operator;
    }
}
function computeSubCompany(row,condition){
    var fieldId=2617;
    var enumCode=row.company;
    for (var i = 0; i < condition.length; i++) {
        if (fieldId == condition[i].metaInfo.fieldId) {
            var enumList = condition[i].enumInfo;
            for (var j = 0; j < enumList.length; j++) {
                if (enumCode == enumList[j].enumCode) {
                    return enumList[j].enumValue;
                }
            }
        }
    }
    return row.company;
}
function getString(row,condition){
	var fieldType=row.fieldType;
	var str="";
	if(fieldType=='enum'){
		return getEnumValueString(row,condition);
	}else if(fieldType=='date'){
		return getSpecialFieldsString(row);
	}
	return str;
}
function getSpecialFieldsString(row){
	var enumCodeValues='';
	console.log(row);
	if(row.comparisonOperator=='='){
		console.log(row.equalValue=='');
		console.log(row.equalValue);
		if(row.equalValue!=''){
			enumCodeValues=dateFtt("yyyy-MM-dd",new Date(row.equalValue));
		}
    }else if(row.comparisonOperator=='[]'){
    	if(row.hasOwnProperty("distanceValue")
    			&&row.distanceValue!=null
    			&&row.distanceValue[0]!=''
    			&&row.distanceValue[1]!=''){
	    	var x1=dateFtt("yyyy-MM-dd",new Date(row.distanceValue[0]));
	    	var x2=dateFtt("yyyy-MM-dd",new Date(row.distanceValue[1]));
	    	enumCodeValues=x1+","+x2;
    	}else{
    		enumCodeValues=row.enumCode;
    	}
    }else if(row.comparisonOperator=='in'){
    	enumCodeValues=row.inValue;
    }
	return enumCodeValues;
}
function initCondition(vue){
    var x=localStorage.getItem("conditionString");
    if(x!=null){
        var data=JSON.parse(x);
        init(data,vue);
        return;
    }
    vue.$http.get(baseUrl+'/precisionServlet.do?type=condition').then(function(res){
        vue.info(res.data)
        if(res.data.code==200){
            init(res.data.data,vue);
            localStorage.setItem("conditionString",JSON.stringify(res.data.data));
        }
    },function(){
        vue.$Message.error('获取所有条件失败');
    });
}
function init(data,vue){
    vue.condition=data;
    for(var i=0;i<data.length;i++){
        vue.multiFlag[i]=false;
        vue.selectedValue[i]=new Object()
        vue.selectedValue[i].equalVisible='';
        vue.selectedValue[i].distanceVisible='display:none;';
        vue.selectedValue[i].inVisible='display:none;';
        vue.selectedValue[i].equalValue='';
        vue.selectedValue[i].distanceValue=[];
        vue.selectedValue[i].inValue=[];
    }
}
function getFieldAndEnumString(fieldId,enumCode,fieldCode,condition){
	var chineseValue='';
    var currentYear=new Date().getFullYear();
    if(fieldCode!=null&&fieldCode=='birthdate'){
    	return currentYear-enumCode;
    }
    //判断enumCode是这种  3060100  还是这种 1,2,3,4
    //enumCode还有可能是一个Array,遍历整个条件对象找值
    if(enumCode==null||enumCode=='')return enumCode;
    if(enumCode.indexOf(",")!=-1||enumCode instanceof Array){
        for (var i = 0; i < condition.length; i++) {
            if (fieldId == condition[i].metaInfo.fieldId) {
                var enumList = condition[i].enumInfo;
                for (var j = 0; j < enumList.length; j++) {
                    if(enumCode.indexOf(enumList[j].enumCode)!=-1){
                        chineseValue=chineseValue+","+enumList[j].enumValue;
                    }
                }
                return chineseValue.substr(1)
            }
        }
        return chineseValue
    }else {
        for (var i = 0; i < condition.length; i++) {
            if (fieldId == condition[i].metaInfo.fieldId) {
                var enumList = condition[i].enumInfo;
                for (var j = 0; j < enumList.length; j++) {
                    if (enumCode == enumList[j].enumCode) {
                        chineseValue =enumList[j].enumValue;
                        break;
                    }
                }
                if(chineseValue=='')return enumCode;
                return chineseValue;
            }
        }
    }
}
function getEnumValueString(row,condition){
    var enumCode=row.enumCode;
    var fieldId=row.fieldId;
    var fieldCode=row.fieldCode;
    return getFieldAndEnumString(fieldId,enumCode,fieldCode,condition);
}
function flushValidCondition(selectedValue){
	var x=new Array();
    for(var i=0;i<selectedValue.length;i++) {
        var o = selectedValue[i];
        if(o.fieldCode==null)continue;
        if(o.fieldType=='date'){
            var enumCodeValues=getSpecialFieldsString(o);
            if(enumCodeValues!=null&&enumCodeValues!=''){
                o.enumCode=enumCodeValues;
                x.push(o);
            }
        }else if(o.fieldCode==null
            ||o.enumCode==null
            ||o.comparisonOperator==null
            ||o.enumCode.length==0){
        }else{
            x.push(o);
        }
    }
    console.log("刷新后的条件:"+JSON.stringify(x));
    return x;
}
function getValidCondition(selectedValue){
	console.log("原始对象:"+JSON.stringify(selectedValue))
    var x=new Array()
    for(var i=0;i<selectedValue.length;i++){
        if(selectedValue[i].hasOwnProperty('fieldCode')&&
        selectedValue[i].hasOwnProperty('enumCode')&&
        selectedValue[i].hasOwnProperty('comparisonOperator')&&
        selectedValue[i].comparisonOperator!=''&&
        selectedValue[i].enumCode!=''&&
        selectedValue[i].enumCode!='[]'){
            x.push(selectedValue[i])
        }
    }
    console.log("去除空的元素:" + JSON.stringify(x))
    //连接enumcode
    var currentYear=new Date().getFullYear();
    for(var i=0;i<x.length;i++){
        //如果有空的不转化
    	if(x[i].enumCode==null||x[i].enumCode=='')continue;
    	//如果是特殊的日期也不转化,在前面已经转了
    	if(x[i].fieldType=='date')continue;
    	//if(specialFields.indexOf(x[i].fieldCode)!=-1)continue;
    	//数组型
        if(x[i].enumCode.indexOf(",")!=-1||x[i].enumCode instanceof Array){ 
            //出生日期需要显示为年龄,前台选择具体的年龄后自动转换为具体哪年
        	if(x[i].fieldCode!=null&&x[i].fieldCode=='birthdate'){
				for(var m=0;m<x[i].enumCode.length;m++){
					x[i].enumCode[m]=currentYear-x[i].enumCode[m]
				}
            }else{                                                                          
                //其他的枚举值如果是数组的话转化[1,2,3,4]==>1,2,3,4
    			x[i].enumCode = Object.values(x[i].enumCode).join(',')
            }
        }else{//单个值型
        	if(x[i].fieldCode!=null&&x[i].fieldCode=='birthdate'){
				x[i].enumCode=currentYear-x[i].enumCode;
        	}
        }
    }
    return JSON.stringify(x)
}
