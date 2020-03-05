<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cpiclife.precisionmarketing.precision.Model.*"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="com.cpiclife.precisionmarketing.precision.service.*" %>
<%@ page import="java.util.*" %>
<%
    String taskId=request.getParameter("taskId");
    if(taskId==null){
        System.out.println("非法访问!"+new Date());
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
<div id="task">
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
                <i-button type="info" @click="cancelCount" >取消盘点</i-button>
                <i-button type="info" @click="startSampling" >开始抽样</i-button>
                <i-button type="info" @click="startCount" >开始盘点</i-button>
            </Row>
        </div>
    </div>
    <%--下面的表格显示用户已经选择的条件--%>
    <i-table height="200" :columns="selectTableHeader" :data="selectedValue" :no-data-text="noDataShow"></i-table>
    <%--下面的表格显示盘点结果--%>
    <i-table height="200" :columns="resultColumns" :data="result" :no-data-text="noDataShow"></i-table>
</div>
<script>
    new Vue({
        el:"#task",
        data(){
            return {
                noDataShow:'无数据',
                taskId:<%=taskId%>,
                //用户的盘点结果
                resultColumns:[
                    {
                        type:'selection',
                        width:60,
                        align:'center'
                    },
                    {
                        title:"条件描述",
                        key:"fieldName"
                    },
                    {
                        title:"数量",
                        key:"amount"
                    },
                    {
                        title:'客群名称',
                        key:'guestGroupName',
                        render(row,column,index){
                            return '<input type="text"/>'
                        }
                    }
                ],
                result:[],

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
            this.getAllSelected();
        },
        methods:{
            cancelCount:function(){

            },
            startSampling:function(){

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
                    if(res.status==200) {
                        this.$Message.success("创建盘点任务成功!")
                        location.href = 'http://localhost:8080/index.jsp';
                    }else{
                        this.$Message.error("创建盘点任务失败!")
                    }
                },function () {
                    this.$Message.error("创建盘点请求失败!")
                });
            },
            getAllResult:function(){

            },
            getAllSelected:function(){
                this.$http.get('http://localhost:8080/getLastestCondition?taskId='+this.taskId)
                    .then(function(res){
                        console.log(res.data)
                        this.selectedValue=res.data;
                    },function () {
                        console.log("获取用户已经选择条件失败!")
                    });
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
        }
    })
</script>

</body>
</html>