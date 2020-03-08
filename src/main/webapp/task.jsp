<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cpiclife.precisionmarketing.precision.Model.*"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="com.cpiclife.precisionmarketing.precision.service.*" %>
<%@ page import="java.util.*" %>
<%
    String taskId=request.getParameter("taskId");
    String status=request.getParameter("status");
    String company=request.getParameter("company");
    String userId=request.getParameter("userId");
    String precisionId=request.getParameter("precisionId");
    if(taskId==null||status==null||precisionId==null||company==null){
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
    <script src="iview/precision.js"></script>
</head>
<body>
<div id="task">
    <div style="text-align:center;">
        <div style="width:100%;text-align:center;height:250px;overflow:scroll;">
            <template v-for="(vo,index) in condition">
                <Row>
                    <i-col span="6">
                        {{ vo.metaInfo.fieldName }}
                    </i-col>
                    <i-col span="6">
                        <i-select  @on-change="operatorChange($event,vo.metaInfo,index)"  placeholder="请选择关系" clearable style="width:260px">
                            <i-option v-for="(operator,index) in vo.operators" :value="operator">{{ computeOperator(operator) }}</i-option>
                        </i-select>
                    </i-col>
                    <i-col span="10">
                        <i-select  filterable  @on-change="changeValue($event,vo.metaInfo,vo.enumInfo,index)"  :multiple="multiFlag[index]" placeholder="枚举值" style="width:260px">
                            <i-option v-for="(enumValue,index) in vo.enumInfo" :value="enumValue.enumCode">{{ enumValue.enumValue }}</i-option>
                        </i-select>
                    </i-col>
                </Row>
            </template>
        </div>
        <div style="width:100%;height:40px;text-align:center;">
            <Row style="text-align:center;">
<%--                <Icon type="ios-heart"></Icon>--%>
                <i-button type="status" >{{ statusName }}</i-button>
                <i-button type="primary" @click="back">
                    <Icon type="chevron-left"></Icon>
                    上一级
                </i-button>
                <i-button type="info" :disabled="startSampleDisable" @click="startSampling" >开始抽样</i-button>
                <i-button type="info" :disabled="startCountDisable" @click="updateCount" >重新盘点</i-button>
                <i-button type="info" :disabled="flushDisable" @click="flushCondition" >刷新条件</i-button>
            </Row>
        </div>
    </div>
    <%--下面的表格显示用户已经选择的条件--%>
    <i-table stripe border height="300" :columns="selectedTableHeader" :data="showValue" :no-data-text="noDataShow"></i-table>
<%--    下面的表格显示用户重新选择的条件--%>
<%--    <i-table stripe border height="300" :columns="selectTableHeader" :data="reSelectValue" :no-date-text="noDataShow"></i-table>--%>
    <%--下面的表格显示盘点结果--%>
    <i-table stripe border height="200" ref="selection"  :columns="resultColumns" :data="result" :no-data-text="noDataShow">
    </i-table>
</div>
<script>
    var baseUrl='http://localhost:8080/'
    var vue=new Vue({
        el:"#task",
        data(){
            return {
                loading:false,
                startCountDisable:true,
                startSampleDisable:true,
                flushDisable:true,
                noDataShow:'无数据',
                userId:<%=userId%>,
                taskId:<%=taskId%>,
                status:<%=status%>,
                company:<%=company%>,
                precisionId:<%=precisionId%>,
                statusName:'',
                currentTask:{},
                //用户的盘点结果
                resultColumns:[
                    {
                        title:"条件描述",
                        key:"descartesfields",
                        sortable:true,
                    },
                    {
                        title:"数量",
                        key:"amount",
                        sortable:true,
                    },
                    {
                        title: '选择数量',
                        key: 'usedAmount',
                        sortable:true,
                        render: (h, params) => {
                            if (params.row.$isEdit) {
                                return h('input', {
                                    domProps: {
                                        value: params.row.usedAmount
                                    },
                                    on: {
                                        input: function (event) {
                                            params.row.usedAmount = event.target.value
                                        }
                                    }
                                });
                            } else  {
                                return h('div', params.row.usedAmount);
                            }
                        }
                    },
                    {
                        title: '客群名称',
                        key: 'guestName',
                        sortable:true,
                        render: (h, params) => {
                            if (params.row.$isEdit) {
                                return h('input', {
                                    domProps: {
                                        value: params.row.guestGroupName
                                    },
                                    on: {
                                        input: function (event) {
                                            params.row.guestGroupName = event.target.value
                                        }
                                    }
                                });
                            } else  {
                                return h('div', params.row.guestGroupName);
                            }
                        }
                    },
                    {
                        title: 'Action',
                        key: 'action',
                        render: (h, params) => {
                            return h('Button', {
                                props: {
                                    type: 'text',
                                    size: 'small'
                                },
                                on: {
                                    click: () => {
                                        if (params.row.$isEdit) {
                                            this.handleSave(params.row)
                                        } else {
                                            this.handleEdit(params.row)
                                        }
                                    }
                                }
                            }, params.row.$isEdit ? '保存' : '编辑')
                        }
                    }
                ],
                result:[],
                sampleResult:[],
                selectedTableHeader:[
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
                //后台的所有条件
                condition:[],
                //第三个是否是多选框
                multiFlag:[],
                //所有变量的个数
                variableSize:0,
                //用户当前已经选择的值
                selectedValue:[],
                //用户重新选择的条件
                reSelectValue:[],
                //在前台页面显示的数据
                showValue:[],
            }
        },
        mounted:function () {
            this.$Message.loading("正在加载数据!");
            this.getAllCondition()
            this.getCurrentTask();
            this.getAllResult();
            this.getAllSelected();

        },
        methods:{
            handleEdit (row) {
                this.$set(row, '$isEdit', true)
            },
            handleSave (row) {
                var flag=true
                console.log(row)
                var reg = new RegExp("^[0-9]*$");
                if(row.usedAmount==null||row.usedAmount==''||!reg.test(row.usedAmount)){
                    this.$Message.warning("配置量不合理!")
                    flag=false
                }
                if(row.guestGroupName==null||row.guestGroupName==''){
                    this.$Message.warning("请输入客群名称!")
                    flag=false
                }
                if(flag){
                    this.$set(row, '$isEdit', false)
                    this.sampleResult.push(row);
                }
            },
            startSampling:function(){
                var result=new Array();
                for(var i=0;i<this.sampleResult.length;i++){
                    var o=this.sampleResult[i];
                    if(o.usedAmount!=null&&o.usedAmount!=''&&
                    o.guestGroupName!=null&&o.guestGroupName!=''){
                        result.push(o)
                    }
                }
                var str=JSON.stringify(result)
                if(str=='[]'){
                    this.$Message.warning("请选择抽样数量和条件!")
                    return;
                }
                console.log(str)
                this.$http.post(baseUrl+'precision.do',{
                    'type':'startSample',
                    'value':str,
                    'taskId':this.taskId,
                    'userId':this.userId
                },{
                    emulateJSON:true
                }).then(function (res) {
                    this.info(res.data);
                    this.flushPage();
                },function (e) {
                    this.$Message.error("抽样失败!"+e,3)
                });
            },
            getAllResult:function(){
                this.$http.get(baseUrl+'/precision.do?type=getAllResult&taskId='+this.taskId)
                    .then(function (res) {
                        // this.info(res.data)
                        if(res.data.code==200){
                            this.result=res.data.data;
                        }
                    },function () {

                    })
            },
            back:function(){
                location.href = baseUrl+'/index.jsp?userId='+this.userId+'&precisionId='+this.precisionId+'&company='+this.company;
            },
            flushCondition:function(){
                var x=new Array();
                for(var i=0;i<this.reSelectValue.length;i++) {
                    var o = this.reSelectValue[i];
                    if(o.fieldCode==null
                        ||o.enumCode==null
                        ||o.comparisonOperator==null
                        ||o.enumCode.length==0){
                    }else{
                        x.push(o);
                    }
                }
                if(x.length==0){
                    this.$Message.error("请先选择盘点条件!")
                }else{
                    console.log(x)
                    this.showValue=x;
                }
            },
            forbidOperate:function(){
                this.startCountDisable=true
                this.startSampleDisable=true;
                this.flushDisable=true;
                this.$Notice.success({
                    title: '通知',
                    desc: '只能查看其他用户的盘点',
                    duration:30
                });
            },
            getCurrentTask:function(){
                this.$http.get(baseUrl+'/precision.do?type=getCurrentTask&taskId='+this.taskId)
                    .then(function (res) {
                        // this.info(res.data)
                        if(res.data.code==200) {
                            this.currentTask = res.data.data;
                            this.flushStatusName(res.data.data);
                            if(res.data.data.userId!=this.userId){
                                this.forbidOperate()
                            }else{
                                this.flushPageStatus(res.data.data);
                            }
                        }
                    },function () {
                        this.$Message.error("获取任务状态失败!");
                    })
            },
            flushStatusName(data){
                this.statusName=computeStatus(data.status);
            },
            flushPageStatus:function(data){
                this.status=data.status
                if (this.status==0){//已取消
                  this.startCountDisable=true
                  this.startSampleDisable=true;
                  this.flushDisable=true;
              }else if (this.status==1){//待盘点
                  this.startCountDisable=true;
                  this.startSampleDisable=true;
                  this.flushDisable=true;
              }else if(this.status==2){//盘点中
                  this.startCountDisable=true;
                  this.startSampleDisable=true;
                  this.flushDisable=true;
                  this.loading=true
              }else if(this.status==3){//盘点完成
                  this.startCountDisable=false;
                  this.startSampleDisable=false;
                  this.flushDisable=false;
                  this.loading=false
                }else if(this.status==4){//待抽样
                  this.startCountDisable=true;
                  this.startSampleDisable=true;
                  this.flushDisable=true;
              }else if(this.status==5){//抽样中
                  this.startCountDisable=true;
                  this.startSampleDisable=true;
                  this.flushDisable=true;
              }else if (this.status==6){//上载完成
                  this.startCountDisable=true;
                  this.startSampleDisable=true;
                  this.flushDisable=true;
              }
            },
            updateCount:function(){
                this.flushCondition();
                console.log("原始对象:"+JSON.stringify(this.reSelectValue))
                var x=new Array()
                for(var i=0;i<this.reSelectValue.length;i++){
                    console.log(this.reSelectValue[i].hasOwnProperty('fieldCode'))
                    if(this.reSelectValue[i].hasOwnProperty('fieldCode')&&
                        this.reSelectValue[i].hasOwnProperty('enumCode')&&
                        this.reSelectValue[i].hasOwnProperty('comparisonOperator')){
                        x.push(this.reSelectValue[i])
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
                if(str=='[]'){
                    this.$Message.warning("请先选择盘点条件!")
                    return;
                }
                var taskId=this.taskId
                this.$http.post(
                    baseUrl+'precision.do', {
                        'type':'updateCount',
                        "value": str,
                        "taskId":taskId
                    }, {emulateJSON: true}
                ).then(function (res) {
                    this.info(res.data)
                    if(res.data.code==200) {
                        this.flushPage();
                    }
                },function () {
                    this.$Message.error("创建盘点请求失败!")
                });
            },
            flushPage:function(){
                location.href = baseUrl+'index.jsp?userId='+this.userId+'&precisionId='+this.precisionId+'&company='+this.company;
            },
            getAllSelected:function(){
                this.$http.get(baseUrl+'precision.do?type=getLatestCondition&taskId='+this.taskId)
                    .then(function(res){
                        // this.info(res.data)
                        if(res.data.code=='200'){
                            this.selectedValue=res.data.data;
                            this.showValue=this.selectedValue;
                        }
                    },function () {
                        console.log("获取用户已经选择条件失败!")
                    });
            },
            getAllCondition:function(){
                this.$http.get(baseUrl+'precision.do?type=condition').then(function(res){
                    // this.info(res.data);
                    if(res.data.code==200){
                        this.condition=res.data.data;
                        this.variableSize=res.data.data.length;
                        for(var i=0;i<this.variableSize;i++){
                            this.multiFlag[i]=false;
                            this.reSelectValue[i]=new Object()
                        }
                    }
                },function(){
                    this.$Message.error('获取所有条件失败');
                });
            },
            operatorChange:function(v,metaInfo,index){
                if(v=="in"){
                    if(!this.multiFlag[index]){
                        this.$set(this.multiFlag,index,true)
                    }
                }else{
                    if(this.multiFlag[index]){
                        this.$set(this.multiFlag,index,false)
                    }
                }

                var o=this.reSelectValue[index];
                if (JSON.stringify(o)=="{}"){
                    o=new Object();
                    o.id=null
                    o.taskId=null
                    o.times=null
                    o.variableType=null
                    o.enumCode=null
                }
                o.fieldCode=metaInfo.fieldCode
                o.fieldId=metaInfo.fieldId
                o.fieldName=metaInfo.fieldName
                o.fieldType=metaInfo.fieldType
                o.comparisonOperator=v;
                console.log(o.enumCode)
                this.$set(this.reSelectValue,index,o);
            },
            changeValue:function (v,metaInfo,enumInfo,index) {
                var o=this.reSelectValue[index];
                if(JSON.stringify(o)=="{}"){
                    o=new Object();
                    o.id=''
                    o.taskId=''
                    o.times=''
                    o.variableType=''
                    o.fieldCstrValue=''
                }
                o.fieldCode=metaInfo.fieldCode
                o.fieldId=metaInfo.fieldId
                o.fieldName=metaInfo.fieldName
                o.fieldType=metaInfo.fieldType
                o.enumCode=v
                this.$set(this.reSelectValue,index,o);
            },
            info:function (res) {
                if(res.code=='200'){
                    this.$Message.success(res.msg,3);
                }else if(res.code=='400'){
                    this.$Message.error(res.msg,3)
                }
            }
        }
    })
</script>

</body>
</html>