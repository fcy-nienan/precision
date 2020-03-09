<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cpiclife.precisionmarketing.precision.Model.*"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="com.cpiclife.precisionmarketing.precision.service.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%
    String userId=request.getParameter("userId");
    String precisionId=request.getParameter("precisionId");
    String company=request.getParameter("company");
    System.out.println(userId+":"+precisionId+":"+company);
    if (userId!=null&&precisionId!=null&&company!=null){
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletConfig().getServletContext());
    }else{
        System.out.println("非法请求!"+new Date());
    }

%>
<html>
<head>
    <meta charset="UTF-8"/>
    <script src="iview/vue.min.js"></script>
    <!-- 引入样式 -->
    <link rel="stylesheet" href="iview/iview.css">
    <!-- 引入组件库 -->
    <script src="iview/iview.min.js"></script>
    <script src="https://cdn.staticfile.org/vue-resource/1.5.1/vue-resource.min.js"></script>
    <script src="iview/precision.js"></script>
</head>
<body>
<div id="div">
    <div style="width:100%;text-align:center;">
        <i-table stripe border show-header highlight-row  height="300" style="text-align:center;"  :columns="taskColumns" :data="taskList" :no-data-text="noDataShow">
            <template slot-scope="{ row, index }" slot="action">
                <i-button type="primary" size="small" @click="queryInfo(row,index)" >查看盘点信息</i-button>
            </template>
        </i-table>
        <Page :current="pageIndex" :page-size="pageSize" show-total :total="total"  @on-change="changePage"></Page>
    </div>
    <div style="width:100%;height:40px;"></div>
    <div style="text-align:center;">
        <div style="width:100%;text-align:center;height:250px;overflow:scroll;">
            <template v-for="(vo,index) in condition">
                <Row>
                    <i-col span="8">
                        {{ vo.metaInfo.fieldName }}
                    </i-col>
                    <i-col span="8">
                        <i-select   @on-change="operatorChange($event,vo.metaInfo.fieldCode,vo.metaInfo.fieldId,vo.metaInfo.fieldName,vo.metaInfo.fieldType,index)"  placeholder="请选择关系" clearable style="width:260px">
                            <i-option v-for="(operator,index) in vo.operators" :value="operator">{{ operator }}</i-option>
                        </i-select>
                    </i-col>
                    <i-col span="8">
                        <i-select  filterable ref="enum" @on-change="changeValue($event,vo.metaInfo.fieldCode,vo.metaInfo.fieldId,vo.metaInfo.fieldName,vo.metaInfo.fieldType,index)"  :multiple="multiFlag[index]" placeholder="枚举值" style="width:260px">
                            <i-option v-for="(enumValue,index) in vo.enumInfo" :value="enumValue.enumCode">{{ enumValue.enumValue }}</i-option>
                        </i-select>
                    </i-col>
                </Row>
            </template>
        </div>
        <div style="width:100%;height:40px;text-align:center;">
            <Row style="text-align:center;">
                <i-button type="info" @click="startCount" >开始盘点</i-button>
                <i-button type="info" @click="flushCondition">刷新盘点条件</i-button>
            </Row>
        </div>
    </div>
<!--    下面的表格显示用户当前已经选择的条件-->
    <i-table stripe border height="200" :columns="selectTableHeader" :data="showValue" :no-data-text="noDataShow"></i-table>
</div>
<script>
    var baseUrl='http://localhost:8080/';
    var vue=new Vue({
        el:"#div",
        data(){
            return{
                noDataShow:'无数据',
                //用户任务信息
                taskList:[],
                taskColumns:[
                    {
                        title:"用户id",
                        key:'userId',
                        sortable:true,
                    },
                    {
                        title:"营销id",
                        key:'precisionId',
                        sortable:true,
                    },
                    {
                        title:"状态",
                        key:'statusName',
                        sortable:true,
                        render(h,params){
                            var x=computeStatus(params.row.status)
                            return h('strong',x);
                        }
                    },
                    {
                        title:"分公司",
                        key:'company',
                        sortable:true,
                        render(h,params){
                            var x=computeSubCompany(params.row,vue.condition)
                            return h('strong',x);
                        }
                    },
                    {
                        title: '操作',
                        slot:'action',
                        align: 'center',
                        sortable:true,
                    },
                    {
                        title:'开始日期',
                        key:'insertDate',
                        sortable:true,
                    },
                    {
                        title:'最后修改时间',
                        key:'lastModified',
                        sortable:true,
                    }
                ],
                pageIndex:1,
                pageSize:5,
                total:'',
                selectTableHeader:[
                    {
                        "title":"变量名",
                        "key":"fieldName",
                        sortable:true,
                    },
                    {
                        "title":"条件",
                        "key":"comparisonOperator",
                        sortable:true,
                        render(h,params){
                            var x=computeOperator(params.row.comparisonOperator);
                            return h('strong',x)
                        }
                    },
                    {
                        "title":"枚举值",
                        "key":"enumCode",
                        sortable:true,
                        render(h,params){
                            var x=getEnumValueString(params.row,vue.condition);
                            return h('strong',x)
                        }
                    }
                ],

                userId:<%=userId%>,
                precisionId:<%=precisionId%>,
                company:<%=company%>,

                //后台的所有条件
                condition:[],
                //第三个是否是多选框
                multiFlag:[],
                //所有变量的个数
                variableSize:0,
                //用户当前已经选择的值
                selectedValue:[],
            //    前台显示选择的值(有这个是因为刷新条件的时候operateChange方法会报错,空的对象)
                showValue:[]
            }
        },
        mounted:function () {
            this.getAllCondition()
            this.getAllTask()
        },
        methods: {
            getEnumValueString(row){
                var enumCode=row.enumCode;
                var fieldId=row.fieldId;
                var con=this.condition;
                var chineseValue=''
                if(enumCode instanceof Array){
                    for (var i = 0; i < con.length; i++) {
                        // console.log(fieldId == con[i].metaInfo.fieldId)
                        if (fieldId == con[i].metaInfo.fieldId) {
                            var enumList = con[i].enumInfo;
                            for (var j = 0; j < enumList.length; j++) {
                                // console.log(enumCode.indexOf(enumList[j].enumCode))
                                if(enumCode.indexOf(enumList[j].enumCode)!=-1){
                                    chineseValue=chineseValue+","+enumList[j].enumValue;
                                }
                            }
                            return chineseValue.substr(1)
                        }
                    }
                    return chineseValue
                }else {
                    for (var i = 0; i < con.length; i++) {
                        // console.log(fieldId == con[i].metaInfo.fieldId)
                        if (fieldId == con[i].metaInfo.fieldId) {
                            var enumList = con[i].enumInfo;
                            for (var j = 0; j < enumList.length; j++) {
                                if (enumCode == enumList[j].enumCode) {
                                    chineseValue = enumList[j].enumValue;
                                    break;
                                }
                            }
                            return chineseValue;
                        }
                    }
                }
            },
            queryInfo(row,index){
                var taskId=row.taskId
                var status=row.status
                var precisionId=this.precisionId;
                var company=this.company;
                var userId=this.userId;
                console.log(this.userId)
                location.href=baseUrl+'task.jsp?taskId='+taskId+'&status='+status+'&precisionId='+precisionId+'&company='+company+'&userId='+userId;
            },
            getAllTask:function () {
                this.$http.post(
                    baseUrl+'precision.do',
                    {
                        'type':'getAllTask',
                        "userId":this.userId,
                        "company":this.company,
                        "pageSize":this.pageSize,
                        "pageIndex":this.pageIndex
                    },
                    {emulateJSON:true}
                ).then(function (res) {
                        this.info(res.data)
                        if(res.data.code==200){
                            console.log(res.data)
                            this.taskList=res.data.data.content;
                            this.total=res.data.data.totalElements;
                            for(var i=0;i<this.taskList.length;i++){
                                this.taskList[i].insertDate=dateFtt('yyyy-MM-dd hh-mm',new Date(this.taskList[i].insertDate))
                                this.taskList[i].lastModified=dateFtt('yyyy-MM-dd hh-mm',new Date(this.taskList[i].lastModified))
                            }
                        }
                    },function (e) {
                        this.$Message.error("网络错误或者系统出错!"+e.toString())
                    })
            },
            startCount:function(){
                console.log("原始对象:"+JSON.stringify(this.selectedValue))
                var x=new Array()
                for(var i=0;i<this.selectedValue.length;i++){
                    console.log(this.selectedValue[i].hasOwnProperty('fieldCode'))
                    if(this.selectedValue[i].hasOwnProperty('fieldCode')&&
                    this.selectedValue[i].hasOwnProperty('enumCode')&&
                    this.selectedValue[i].hasOwnProperty('comparisonOperator')&&
                    this.selectedValue[i].comparisonOperator!=''&&
                    this.selectedValue[i].enumCode!=''&&
                    this.selectedValue[i].enumCode!='[]'){
                        x.push(this.selectedValue[i])
                    }
                }
                console.log("去除空的元素:" + JSON.stringify(x))
                //连接enumcode
                for(var i=0;i<x.length;i++){
                    if(typeof(x[i].enumCode)=='string'){

                    }else {
                        x[i].enumCode = Object.values(x[i].enumCode).join(',')
                    }
                }
                console.log("将要传输到后台保存的对象:" + JSON.stringify(x))
                var str = JSON.stringify(x)
                console.log("转换为json字符串后:" + str)
                if(str=='[]'){
                    this.$Message.warning("请选择盘点条件!")
                    return;
                }
                this.$http.post(
                    baseUrl+'precision.do',
                    {
                        'type':'startCount',
                        "value": str,
                        "userId": this.userId,
                        "precisionId":this.precisionId,
                        "company":this.company
                    },
                    {emulateJSON: true}
                ).then(function (res) {
                    console.log(res)
                    this.info(res.data)
                    if(res.data.code==200){
                        location.href = baseUrl+'index.jsp?userId='+this.userId+'&precisionId='+this.precisionId+'&company='+this.company;
                    }
                },function () {
                    this.$Message.error("创建盘点请求失败!")
                });
            },
            changePage:function(index){
                this.pageIndex=index;
                this.getAllTask();
            },
            flushCondition:function(){
                var x=new Array();
                for(var i=0;i<this.selectedValue.length;i++) {
                    var o = this.selectedValue[i];
                    if(o.fieldCode==null
                        ||o.enumCode==null
                        ||o.comparisonOperator==null
                        ||o.enumCode.length==0){
                    }else{
                        x.push(o);
                    }
                }
                if(x.length==0){

                }else{
                    console.log(x)
                    this.showValue=x;
                }
            },
            getAllCondition:function(){
                this.$http.get(baseUrl+'precision.do?type=condition').then(function(res){
                    this.info(res.data)
                    if(res.data.code==200){
                        this.condition=res.data.data;
                        this.variableSize=res.data.data.length;
                        for(var i=0;i<this.variableSize;i++){
                            this.multiFlag[i]=false;
                            this.selectedValue[i]=new Object()
                        }
                    }
                },function(){
                    this.$Message.error('获取所有条件失败');
                });
            },
            operatorChange:function(v,fieldCode,fieldId,fieldName,fieldType,index){
                var o=this.selectedValue[index];
                console.log(this.$refs)
                this.$refs["enum"][index].reset();
                if(v=="in"){
                    if(!this.multiFlag[index]){
                        this.$set(this.multiFlag,index,true)
                    }
                    // o.enumCode=[]
                    // var newEnum=new Array()
                    // newEnum.push(o.enumCode)
                    // o.enumCode=newEnum;
                }else{
                    if(this.multiFlag[index]){
                        this.$set(this.multiFlag,index,false)
                    }
                    console.log(o.enumCode instanceof Array)
                    // if (o.enumCode instanceof Array){
                    //     o.enumCode=''
                    // }
                    // o.enumCode=null
                }
                //选择的值都保存在selectedValue中
                if (JSON.stringify(o)=="{}"){
                    o=new Object();
                    o.id=''
                    o.taskId=''
                    o.times=''
                    o.variableType=''
                    o.enumCode=''
                    o.fieldCstrValue=''
                }
                o.fieldCode=fieldCode
                o.fieldId=fieldId
                o.fieldName=fieldName
                o.fieldType=fieldType
                o.comparisonOperator=v;
                console.log("---"+JSON.stringify(o.enumCode))
                this.$set(this.selectedValue,index,o);
                // console.log(this.selectedValue)
            },
            changeValue:function (v,fieldCode,fieldId,fieldName,fieldType,index) {
                var o=this.selectedValue[index];
                if(JSON.stringify(o)=="{}"){
                    o=new Object();
                    o.id=''
                    o.taskId=''
                    o.times=''
                    o.variableType=''
                    o.fieldCstrValue=''
                }
                o.fieldCode=fieldCode
                o.fieldId=fieldId
                o.fieldName=fieldName
                o.fieldType=fieldType
                o.enumCode=v;
                console.log("enumCode:"+v)
                this.$set(this.selectedValue,index,o);
            },
            info:function (res) {
                if(res.code=='200'){
                    this.$Message.success(res.msg,3);
                }else if(res.code=='400'){
                    this.$Message.error(res.msg,3)
                }
            }
        },
    })
</script>

</body>
</html>