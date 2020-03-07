function dateFtt(fmt,date) { //author: meizz
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
    }else if(status==4){
        return "待抽样";
    }else if (status==5){
        return "抽样中";
    }else if(status==6){
        return "上载成功";
    }else{
        return status;
    }
}
function computeOperator(operator){
    if(operator=='in'){
        return "包含"
    }else if(operator=='='){
        return "等于";
    }else{
        return operator;
    }
}
function computeSubCompany(row,condition){
    var fieldId=2611;
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
function sleep(n) {
    var start = new Date().getTime();
    //  console.log('休眠前：' + start);
    while (true) {
        if (new Date().getTime() - start > n) {
            break;
        }
    }
    // console.log('休眠后：' + new Date().getTime());
}
function getEnumValueString(row,condition){
    var enumCode=row.enumCode;
    var fieldId=row.fieldId;
    var chineseValue=''
    //判断enumCode是这种  3060100  还是这种 1,2,3,4
    //enumCode还有可能是一个Array
    if(enumCode.indexOf(",")!=-1||enumCode instanceof Array){
        for (var i = 0; i < condition.length; i++) {
            console.log(fieldId == condition[i].metaInfo.fieldId)
            if (fieldId == condition[i].metaInfo.fieldId) {
                var enumList = condition[i].enumInfo;
                for (var j = 0; j < enumList.length; j++) {
                    console.log(enumCode.indexOf(enumList[j].enumCode))
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
            console.log(fieldId == condition[i].metaInfo.fieldId)
            if (fieldId == condition[i].metaInfo.fieldId) {
                var enumList = condition[i].enumInfo;
                for (var j = 0; j < enumList.length; j++) {
                    if (enumCode == enumList[j].enumCode) {
                        chineseValue =enumList[j].enumValue;
                        break;
                    }
                }
                return chineseValue;
            }
        }
    }
}
