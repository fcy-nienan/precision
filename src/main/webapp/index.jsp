<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cpiclife.precisionMarketing.model.*"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="com.cpiclife.precisionMarketing.service.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%
    String userId=request.getParameter("userId");
    String precisionId=request.getParameter("precisionId");
    String company=request.getParameter("company");
    System.out.println("前台页面访问参数:"+userId+":"+precisionId+":"+company);
    if (userId!=null&&precisionId!=null&&company!=null){
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletConfig().getServletContext());
    }else{
        System.out.println("非法请求!"+new Date());
    }
%>
<html>
<head>
    <meta charset="UTF-8"/>
    <script src="<%=request.getContextPath()%>/iview/vue.min.js"></script>
    <!-- 引入样式 -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/iview/iview.css">
    <!-- 引入组件库 -->
    <script src="<%=request.getContextPath()%>/iview/iview.min.js"></script>
    <script src="<%=request.getContextPath()%>/iview/vue-resource.min.js"></script>
    <script src="<%=request.getContextPath()%>/iview/precision.js"></script>
</head>
<body>
<div id="div">
    <div style="width:100%;text-align:center;">
        <i-table stripe border show-header highlight-row  height="300" style="text-align:center;"
                 :columns="taskColumns"
                 :data="taskList"
                 :no-data-text="noDataShow">
        </i-table>
    </div>
    <div style="text-align:center;">
        <div style="width:100%;height:40px;text-align:center;">
            <Row style="text-align:center;">
            	<i-button type="info" :disabled="selectConditionDisable" @click="showModal=true">选择条件</i-button>
                <i-button type="info" :disabled="startCountDisable" @click="startConfirm" >开始盘点</i-button>
                <i-button type="info" :disabled="restartCountDisable" @click="restartConfirm" >重新盘点</i-button>
                
            </Row>
        </div>
    </div>
    <%--下面的表格显示用户当前已经选择的条件--%>
    <i-table stripe border height="400" :columns="selectTableHeader" :data="showValue" :no-data-text="noDataShow"></i-table>
    <%--显示用户的盘点结果--%>
    <i-table stripe border height="400" :columns="resultSimpleColumns" :data="result" :no-data-text="noDataShow"></i-table>

    <Modal :value.sync="showModal" width="90%" height="100%" title="筛选条件"
           @on-ok="ok"
           @on-cancel="cancel" scrollable title="筛选条件">
    	<div style="width:100%;text-align:center;height:450px;overflow:scroll;">
            <template v-for="(vo,index) in condition">
                <Row>
                    <i-col span="8">
                        {{ vo.metaInfo.fieldName }}
                    </i-col>
                    <i-col span="6">
                        <i-select  @on-change="operatorChange($event,vo.metaInfo,index)"  placeholder="请选择关系" clearable style="width:260px">
                            <i-option v-for="(operator,index) in vo.operators" :value="operator">{{ computeOperator(operator) }}</i-option>
                        </i-select>
                    </i-col>
                    <template v-if="vo.metaInfo.fieldType=='enum'">
	                    <i-col span="6">
	                        <i-select  filterable ref="enum" @on-change="changeValue($event,vo.metaInfo,index)"  :multiple="multiFlag[index]" placeholder="枚举值" style="width:260px">
	                            <i-option v-for="(enumValue,index) in vo.enumInfo" :value="enumValue.enumCode">{{ enumValue.enumValue }}</i-option>
	                        </i-select>
	                    </i-col>
                    </template>
                    <template v-else>
                    	<i-col span="8">
                    		<Date-picker v-model="selectedValue[index].equalValue" 
                    			:style="selectedValue[index].equalVisible"
                    			<%--@on-change="dateChange($event,index)"--%>
                    			type="date"
                    			placement="bottom-end" placeholder="选择日期"
                    			style="width:250px"
                    		>
                    		</Date-picker>
                    		<Date-picker v-model="selectedValue[index].distanceValue" 
                    			:style="selectedValue[index].distanceVisible"
                    			<%--@on-change="dateChange($event,index)"--%>
                    			type="daterange"
                    			placement="bottom-end" placeholder="选择日期"
                    			style="width:250px"
                    		>
                    		</Date-picker>
                    		<i-input
                    			v-model="selectedValue[index].inValue"
                    			:style="selectedValue[index].inVisible"
                    			placeholder="请输入合格的日期yyyy-MM-dd"                   		
                    			@on-blur="blurChange(index)"
                    		>
                    		</i-input>
                    	</i-col>
                    </template>
                </Row>
            </template>
        </div>
    </Modal>
</div>
<script>
    var vue=new Vue({
        el:"#div",
        data(){
            return{
                noDataShow:'无数据',

                startCountDisable:true,
            	selectConditionDisable:true,
            	restartCountDisable:true,

                showModal:false,
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




                result:[],
                resultSimpleColumns: [
                    {
                        title: "条件描述",
                        key: "fieldsName",
                    },
                    {
                        title: "数量",
                        key: "amount",
                    }
                ],

                userId:"<%=userId%>",
                precisionId:"<%=precisionId%>",
                company:"<%=company%>",

                //后台的所有条件
                condition:[],
                //第三个是否是多选框
                multiFlag:[],
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
                            var x="";
                            x=getString(params.row,vue.condition);
                            return h('strong',x)
                        }
                    }
                ],
                //用户当前已经选择的值
                selectedValue:[],
            //    前台显示选择的值(有这个是因为刷新条件的时候operateChange方法会报错,空的对象)
                showValue:[]
            }
        },
        mounted:function () {
            this.getAllCondition()
            this.getCurrentTask()
            this.getAllResult();
        },
        methods: {
        	blurChange(index){//输入框失去焦点
            	var o=this.selectedValue[index];
            	if(o.inValue!=null&&o.inValue!=''){
                	var str=o.inValue;
                	var reg=new RegExp("^\\d{4}-[0-1][0-9]-[0-3][0-9](,\\d{4}-[0-1][0-9]-[0-3][0-9])*$");
                	if(!reg.test(str)){
                    	this.$Message.error("请输入合格的日期!");
                    	o.inValue='';
                    	this.$set(this.selectedValue,index,o);
                	}
            	}
        	},
            ok(){//保存条件
				this.showModal=false;
				this.flushCondition();
            },
            cancel(){
				this.showModal=false;
            },
            restartConfirm(){
                if(confirm("确认重新开始盘点!")){
                    this.updateCount();
                }
            },
            updateCount:function(){
                var str=getValidCondition(this.selectedValue);
                console.log("转换为json字符串后:" + str)
                if(str=='[]'){
                    this.$Message.warning("请选择盘点条件!")
                    return;
                }
                var precisionId=this.precisionId
                this.$http.post(
                    baseUrl+'/precisionServlet.do', {
                        'type':'updateCount',
                        "value": str,
                        "precisionId":precisionId
                    }, {emulateJSON: true}
                ).then(function (res) {
                    this.info(res.data)
                    if(res.data.code==200) {
                        location.href = baseUrl+'/precisionMarketing/index.jsp?userId='+this.userId+'&precisionId='+this.precisionId+'&company='+this.company;
                    }
                },function () {
                    this.$Message.error("创建盘点请求失败!")
                });
            },
            startConfirm(){
				if(confirm("确认开始盘点?")){
					this.startCount();
				}
            },
            startCount:function(){
                var str=getValidCondition(this.selectedValue);
                console.log("转换为json字符串后:" + str)
                if(str=='[]'){
                    this.$Message.warning("请选择盘点条件!")
                    return;
                }
                this.$http.post(
                    baseUrl+'/precisionServlet.do',
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
                        location.href = baseUrl+'/precisionMarketing/index.jsp?userId='+this.userId+'&precisionId='+this.precisionId+'&company='+this.company;
                    }
                },function () {
                    this.$Message.error("创建盘点请求失败!")
                });
            },
            getAllResult:function(){
                this.$http.get(baseUrl+'/precisionServlet.do?type=getAllResult&precisionId='+this.precisionId)
                    .then(function (res) {
                        if(res.data.code==200){
                            this.result=res.data.data;
                        }
                    },function () {

                    })
            },
            flushPageStatus(data){
                if(data==null){//第一次进入该页面,数据库是没有该任务的
                	this.startCountDisable=false;
                    this.restartCountDisable=true;
                    this.selectConditionDisable=false;
                }else{
	            	this.status=data[0].status
	                if (this.status==1){//待盘点
	                	this.startCountDisable=true;
	                    this.restartCountDisable=true;
	                    this.selectConditionDisable=false;
	                }else if(this.status==2){//盘点中
	                	this.startCountDisable=true;
	                    this.restartCountDisable=true;
	                    this.selectConditionDisable=false;
	                }else if(this.status==3){//盘点完成
	                	this.startCountDisable=true;
	                    this.restartCountDisable=false;
	                    this.selectConditionDisable=false;
	                  }else if(this.status==10){//待抽样
	                	this.startCountDisable=true;
	                    this.restartCountDisable=true;
	                    this.selectConditionDisable=false;
	                }else if(this.status==11){//抽样中
	                	this.startCountDisable=true;
	                    this.restartCountDisable=true;
	                    this.selectConditionDisable=false;
	                }else if (this.status==12){//上载完成
	                	this.startCountDisable=true;
	                    this.restartCountDisable=true;
	                    this.selectConditionDisable=false;
	                }
                }
            },
            getCurrentTask:function(){
                this.$http.get(baseUrl+'/precisionServlet.do?type=getCurrentTask&precisionId='+this.precisionId)
                    .then(function (res) {
                        if(res.data.data!=null){
                            this.taskList=res.data.data;
                            for(var i=0;i<res.data.data.length;i++){
                                this.taskList[i].insertDate=dateFtt("yyyy-MM-dd hh-mm",new Date(res.data.data[i].insertDate))
                                this.taskList[i].lastModified=dateFtt("yyyy-MM-dd hh-mm",new Date(res.data.data[i].lastModified))
                            }
                        }
                        this.flushPageStatus(res.data.data);
                    },function () {
                        this.$Message.error("获取任务状态失败!");
                    })
            },
            flushCondition:function(){
                //这个方法需要筛选出selectedValue中真正选择了的数据
                var x=flushValidCondition(this.selectedValue);
                if(x.length==0){
                    this.$Message.error("请先选择盘点条件!")
                }else{
                    console.log(x)
                    this.showValue=x;
                }
            },
            getAllCondition:function(){
                initCondition(this)
            },
            operatorChange:function(v,metaInfo,index){
                var o=this.selectedValue[index];
                if (JSON.stringify(o)=="{}"){
                    o=initField();
                }
                o.fieldCode=metaInfo.fieldCode
                o.fieldId=metaInfo.fieldId
                o.fieldName=metaInfo.fieldName
                o.fieldType=metaInfo.fieldType
                o.comparisonOperator=v;
                console.log(o);
                if(o.fieldType=='enum'){
                	this.$refs["enum"][index].reset();
					if(v=="in"||v=='notin'){
	                    if(!this.multiFlag[index]){
	                        this.$set(this.multiFlag,index,true)
	                    }
	                }else{
	                    if(this.multiFlag[index]){
	                        this.$set(this.multiFlag,index,false)
	                    }
	                }
                }else{
                	if(v=='='){
						o.equalVisible='';
						o.distanceVisible='display:none';
						o.inVisible='display:none;';
					}else if(v=='[]'){
						o.equalVisible='display:none';
						o.distanceVisible='';
						o.inVisible='display:none;';
					}else if(v=='in'){
						o.equalVisible='display:none;';
						o.distanceVisible='display:none';
						o.inVisible='';
					}
                }
                this.$set(this.selectedValue,index,o);
            },
            changeValue:function (v,metaInfo,index) {
                var o=this.selectedValue[index];
                if(JSON.stringify(o)=="{}"){
                    o=initField();
                }
                o.fieldCode=metaInfo.fieldCode
                o.fieldId=metaInfo.fieldId
                o.fieldName=metaInfo.fieldName
                o.fieldType=metaInfo.fieldType
                o.enumCode=v;
                this.$set(this.selectedValue,index,o);
                console.log("传进来的参数:"+metaInfo)
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