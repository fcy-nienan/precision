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
                <i-button type="info" @click="back">返回上一级</i-button>
                <i-button type="info" :disabled="startSampleDisable" @click="startSampling" >开始抽样</i-button>
                <i-button type="info" :disabled="startCountDisable" @click="updateCount" >重新盘点</i-button>
                <i-button type="info" :disabled="flushDisable" @click="flushCondition" >刷新条件</i-button>
            </Row>
        </div>
    </div>
    <%--下面的表格显示用户已经选择的条件--%>
    <i-table height="200" :columns="selectTableHeader" :data="showValue" :no-data-text="noDataShow"></i-table>
    <%--下面的表格显示盘点结果--%>
    <i-table height="200" ref="selection" @on-select="selectResult" :columns="resultColumns" :data="result" :no-data-text="noDataShow">
        <template slot-scope="{ row, index }" slot="usedAmount">
            <Input v-model="selectedAmount[index]" size="large" placeholder="请输入抽样数量" />
        </template>
        <template slot-scope="{ row, index }" slot="guestName">
            <Input v-model="guestName[index]" size="large" placeholder="请输入客群名称" />
        </template>
    </i-table>
</div>
<script>
    var vue=new Vue({
        el:"#task",
        data(){
            return {
                startCountDisable:true,
                startSampleDisable:true,
                flushDisable:true,
                noDataShow:'无数据',
                userId:<%=userId%>,
                taskId:<%=taskId%>,
                status:<%=status%>,
                company:<%=company%>,
                precisionId:<%=precisionId%>,
                //用户的盘点结果
                resultColumns:[
                    {
                        type:'selection',
                        width:60,
                        align:'center'
                    },
                    {
                        title:"ID",
                        key:'id'
                    },
                    {
                        title:"条件描述",
                        key:"descartesfields"
                    },
                    {
                        title:"数量",
                        key:"amount"
                    },
                    {
                        title: '选择数量',
                        key: 'usedAmount',
                        render: (h, params) => {
                            console.log(h)
                            console.log(params)

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
                    // {
                    //     title:"选择数量",
                    //     slot:'usedAmount'
                    // },
                    // {
                    //     title:'客群名称',
                    //     slot:'guestName',
                    // },
                    {
                        title: '客群名称',
                        key: 'guestName',
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

                startIndex:0,
                endIndex:0,
                selectIndex:[],
                selectedAmount:[],
                guestName:[],

                sampleResult:[],

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
                selectedValue:[],
                //用户重新选择的条件
                reSelectValue:[],
                //在前台页面显示的数据
                showValue:[]
            }
        },
        mounted:function () {
            this.getAllCondition()
            this.getAllSelected();
            this.getCurrentTask();
            this.getAllResult();
        },
        methods:{
            handleEdit (row) {
                console.log(row)
                this.$set(row, '$isEdit', true)

            },
            handleSave (row) {
                console.log(row)
                this.$set(row, '$isEdit', false)
                this.sampleResult.push(row);
            },
            startSampling:function(){

                console.log(this.sampleResult)
            },
            selectResult:function(selection,row){
                console.log("selectResult" )
                console.log(selection)
                console.log(row)

                console.log(this.sampleResult)
                // console.log(this.$refs.selection)
            },
            getAllResult:function(){
                this.$http.get('http://localhost:8080/getAllResult?taskId='+this.taskId)
                    .then(function (res) {
                        console.log(res.data)
                        this.result=res.data;

                        for(var i=0;i<res.data.length;i++){
                            this.selectedAmount[i]='';
                            this.guestName[i]='';
                            this.selectIndex[i]='';
                        }
                    },function () {

                    })
            },
            back:function(){
                location.href = 'http://localhost:8080/index.jsp?userId='+this.userId+'&precisionId='+this.precisionId+'&company='+this.company;
            },
            flushCondition:function(){
                var x=new Array();
                for(var i=0;i<this.reSelectValue.length;i++) {
                    var o = this.reSelectValue[i];
                    console.log("flush:" + o.fieldCode == '' + o.hasOwnProperty('fieldCode'))
                    if(o.fieldCode==null||o.enumCode==null||o.comparisonOperator==null){

                    }else{
                        x.push(o);
                    }
                }
                console.log(x)
                this.showValue=x;
                console.log(this.showValue)


            },
            getCurrentTask:function(){
                console.log("start get current status!")
                this.$http.get('http://localhost:8080/getCurrentStatus?taskId='+this.taskId)
                    .then(function (res) {
                        console.log(res.data)
                        this.flushPageStatus(res.data);
                    },function () {
                        this.$Message.error("获取任务状态失败!");
                    })
            },
            flushPageStatus:function(status){
                this.status=status
              if (this.status==1){//盘点中
                  this.startCountDisable=true
                  this.startSampleDisable=true;
                  this.cancelCountDisable=false;
                  this.flushDisable=true;
              }else if (this.status==2){//盘点完成
                  this.startCountDisable=false;
                  this.startSampleDisable=false;
                  this.cancelCountDisable=true;
                  this.flushDisable=false;
              }else if(this.status==0){//取消
                  this.startCountDisable=true;
                  this.startSampleDisable=true;
                  this.cancelCountDisable=true;
                  this.flushDisable=true;

              }
            },
            updateCount:function(){
                this.flushCondition();
                console.log("原始对象:"+JSON.stringify(this.selectedValue))
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
                var taskId=this.taskId
                this.$http.post(
                    'http://localhost:8080/updateCount',
                    {
                        "value": str,
                        "taskId":taskId
                    },
                    {emulateJSON: true}
                ).then(function (res) {
                    console.log(res)
                    if(res.status==200) {
                        this.$Message.success("创建盘点任务成功!")
                        location.href = 'http://localhost:8080/index.jsp?userId='+this.userId+'&precisionId='+this.precisionId+'&company='+this.company;
                    }else{
                        this.$Message.error("创建盘点任务失败!")
                    }
                },function () {
                    this.$Message.error("创建盘点请求失败!")
                });
            },

            getAllSelected:function(){
                this.$http.get('http://localhost:8080/getLastestCondition?taskId='+this.taskId)
                    .then(function(res){
                        console.log(res.data)
                        this.selectedValue=res.data;
                        this.showValue=this.selectedValue;
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
                        this.reSelectValue[i]=new Object()
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
                var o=this.reSelectValue[index];
                if (JSON.stringify(o)=="{}"){
                    o=new Object();
                    o.id=null
                    o.taskId=null
                    o.times=null
                    o.variableType=null
                    o.enumCode=null
                }
                o.fieldCode=fieldCode
                o.fieldId=fieldId
                o.fieldName=fieldName
                o.fieldType=fieldType
                o.comparisonOperator=v;
                this.$set(this.reSelectValue,index,o);
                console.log(this.reSelectValue)
            },
            changeValue:function (v,fieldCode,fieldId,fieldName,fieldType,index) {
                var o=this.reSelectValue[index];
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
                this.$set(this.reSelectValue,index,o);
                console.log(this.reSelectValue)
            }
        }
    })
</script>

</body>
</html>