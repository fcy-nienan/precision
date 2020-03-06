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
</head>
<body>
<div id="div">
    <div style="width:100%;text-align:center;">
        <i-table height="300" style="text-align:center;"  :columns="taskColumns" :data="taskList" :no-data-text="noDataShow">
            <template slot-scope="{ row, index }" slot="action">
                <i-button type="primary" size="small" @click="queryInfo(row,index)" style="margin-right: 5px" >查看盘点信息</i-button>
            </template>
        </i-table>
        <Page :current="pageIndex" :page-size="pageSize" show-total :total="total"  @on-change="changePage"></Page>
    </div>
    <div style="width:100%;height:40px;"></div>
    <div style="text-align:center;">
        <div style="width:100%;text-align:center;height:150px;overflow:scroll;">
            <template v-for="(vo,index) in condition">
                <Row>
                    <i-col span="6">
                        {{ vo.metaInfo.fieldName }}
                    </i-col>
                    <i-col span="6">
                        <i-select  @on-change="operatorChange($event,vo.metaInfo.fieldCode,vo.metaInfo.fieldId,vo.metaInfo.fieldName,vo.metaInfo.fieldType,index)"  placeholder="请选择关系" clearable style="width:260px">
                            <i-option v-for="(operator,index) in vo.operators" :value="operator">{{ operator }}</i-option>
                        </i-select>
                    </i-col>
                    <i-col span="10">
                        <i-select  @on-change="changeValue($event,vo.metaInfo.fieldCode,vo.metaInfo.fieldId,vo.metaInfo.fieldName,vo.metaInfo.fieldType,index)"  :multiple="multiFlag[index]" placeholder="枚举值" style="width:260px">
                            <i-option v-for="(enumValue,index) in vo.enumInfo" :value="enumValue.enumCode">{{ enumValue.enumValue }}</i-option>
                        </i-select>
                    </i-col>
                </Row>
            </template>
        </div>
        <div style="width:100%;height:40px;text-align:center;">
            <Row style="text-align:center;">
                <i-button type="info" @click="startCount" >开始盘点</i-button>
            </Row>
        </div>
    </div>
<!--    下面的表格显示用户当前已经选择的条件-->
    <i-table height="200" :columns="selectTableHeader" :data="selectedValue" :no-data-text="noDataShow"></i-table>
</div>
<script>
    new Vue({
        el:"#div",
        data(){
            return{
                noDataShow:'无数据',
                //用户任务信息
                taskList:[],
                taskColumns:[
                    {
                        title:"用户id",
                        key:'userId'
                    },
                    {
                        title:"营销id",
                        key:'precisionId'
                    },
                    {
                        title:"状态",
                        key:'statusName',
                    },
                    {
                        title:"分公司",
                        key:'company'
                    },
                    {
                        title: '操作',
                        slot:'action',
                        width: 480,
                        align: 'center'
                    },
                    {
                        title:'开始日期',
                        key:'insertDate'
                    },
                    {
                        title:'最后修改时间',
                        key:'lastModified'
                    }
                ],
                pageIndex:1,
                pageSize:5,
                total:20,
                selectTableHeader:[
                    {
                        "title":"变量名",
                        "key":"fieldName"
                    },
                    {
                        "title":"条件",
                        "key":"comparisonOperator"
                    },
                    {
                        "title":"枚举值",
                        "key":"enumCode"
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
                selectedValue:[]
            }
        },
        mounted:function () {
            this.getAllCondition()
            this.getAllTask()
        },
        methods: {
            queryInfo(row,index){
                var taskId=row.taskId
                var status=row.status
                var precisionId=this.precisionId;
                var company=this.company;
                var userId=this.userId;
                console.log(this.userId)
                location.href='http://localhost:8080/task.jsp?taskId='+taskId+'&status='+status+'&precisionId='+precisionId+'&company='+company+'&userId='+userId;
            },
            getAllTask:function () {
                this.$http.post(
                    'http://localhost:8080/getAllTask',
                    {
                        "userId":this.userId,
                        "company":this.company,
                        "pageSize":this.pageSize,
                        "pageIndex":this.pageIndex
                    },
                    {emulateJSON:true}
                ).then(function (res) {
                        this.$Message.success("获取用户所有可视任务成功!")
                        console.log(res.data)
                        this.taskList=res.data;
                        for(var i=0;i<this.taskList.length;i++){
                            // console.log(dateFtt('yyyy-mm-dd',this.taskList[i].insertDate))
                            this.taskList[i].insertDate=dateFtt('yyyy-MM-dd hh-mm',new Date(this.taskList[i].insertDate))
                            this.taskList[i].lastModified=dateFtt('yyyy-MM-dd hh-mm',new Date(this.taskList[i].lastModified))
                        }
                        console.log(this.taskList)
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
                    this.selectedValue[i].hasOwnProperty('comparisonOperator')){
                        x.push(this.selectedValue[i])
                    }
                }
                console.log("去除空的元素:" + JSON.stringify(x))
                for(var i=0;i<x.length;i++){
                    if(typeof(x[i].enumCode)=='string'){

                    }else {
                        console.log(x[i].enumCode)
                        x[i].enumCode = Object.values(x[i].enumCode).join(',')
                    }
                }
                console.log("将要传输到后台保存的对象:" + JSON.stringify(x))
                var str = JSON.stringify(x)
                console.log("转换为json字符串后:" + str)
                this.$http.post(
                    'http://localhost:8080/startCount',
                    {
                        "value": str,
                        "userId": this.userId,
                        "precisionId":this.precisionId,
                        "company":this.company
                    },
                    {emulateJSON: true}
                ).then(function (res) {
                    console.log(res)
                    if(res.data.code=='success'){
                        this.$Message.success("创建盘点任务成功!")
                        location.href = 'http://localhost:8080/index.jsp?userId='+this.userId+'&precisionId='+this.precisionId+'&company='+this.company;
                    }else if  (res.data.code='existed'){
                        this.$Message.warning("请重新退出并进入!")
                    }else{
                        this.$Message.error("创建盘点任务失败!")
                    }
                },function () {
                    this.$Message.error("创建盘点请求失败!")
                });
            },
            changePage:function(){

            },
            getAllCondition:function(){
                this.$http.get('http://localhost:8080/condition').then(function(res){
                    console.log(res.data)
                    this.condition=res.data;
                    this.variableSize=res.data.length;
                    for(var i=0;i<this.variableSize;i++){
                        this.multiFlag[i]=false;
                        this.selectedValue[i]=new Object()
                    }
                    this.$Message.success("获取所有条件成功!")
                },function(){
                    this.$Message.error('获取所有条件失败');
                });
            },
            operatorChange:function(v,fieldCode,fieldId,fieldName,fieldType,index){
                if(v=="in"){
                    if(!this.multiFlag[index]){
                        this.$set(this.multiFlag,index,true)
                    }
                }else{
                    if(this.multiFlag[index]){
                        this.$set(this.multiFlag,index,false)
                    }
                }
                var o=this.selectedValue[index];
                if (JSON.stringify(o)=="{}"){
                    o=new Object();
                    o.id=null
                    o.taskId=null
                    o.times=null
                    o.variableType=null
                    o.enumCode=null
                    o.fieldCstrValue=null
                }
                o.fieldCode=fieldCode
                o.fieldId=fieldId
                o.fieldName=fieldName
                o.fieldType=fieldType
                o.comparisonOperator=v;
                this.$set(this.selectedValue,index,o);
                console.log(this.selectedValue)
            },
            changeValue:function (v,fieldCode,fieldId,fieldName,fieldType,index) {
                var o=this.selectedValue[index];
                if(JSON.stringify(o)=="{}"){
                    o=new Object();
                    o.id=null
                    o.taskId=null
                    o.times=null
                    o.variableType=null
                    o.fieldCstrValue=null
                }
                o.fieldCode=fieldCode
                o.fieldId=fieldId
                o.fieldName=fieldName
                o.fieldType=fieldType

                o.enumCode=v;
                this.$set(this.selectedValue,index,o);
                console.log(this.selectedValue)
            }
        },
    })
    function dateFtt(fmt,date)
    { //author: meizz
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
</script>

</body>
</html>