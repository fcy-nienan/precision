<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cpiclife.precisionMarketing.model.*"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="com.cpiclife.precisionMarketing.service.*" %>
<%@ page import="java.util.*" %>
<%
    String company=request.getParameter("company");
    String userId=request.getParameter("userId");
    String precisionId=request.getParameter("precisionId");
    if(precisionId==null||company==null){
        System.out.println("非法访问!"+new Date());
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
<div id="task">
    <i-table stripe border height="400" ref="result"  :columns="resultSimpleColumns" :data="result" :no-data-text="noDataShow">
	</i-table>
    <div style="text-align:center;">
        <div style="width:100%;height:40px;text-align:center;">
            <Row style="text-align:center;">
                <i-button type="info" :disabled="addGroup" @click="showModal=true">添加客群</i-button>
                <i-button type="info" :disabled="startDispatcherDisable" @click="startConfirm">开始下发</i-button>
            </Row>
        </div>
    </div>
    <i-table stripe border height="400" ref="selection"  :columns="resultColumns"  :no-data-text="noDataShow">
    </i-table>
    <Modal :value.sync="showModal" width="90%" height="100%" title="筛选条件" @on-ok="ok" @on-cancel="cancel" scrollable title="筛选条件">
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
                    			@on-change="dateChange($event,index)"
                    			type="date"
                    			placement="bottom-end" placeholder="选择日期"
                    			style="width:250px"
                    		>
                    		</Date-picker>
                    		<Date-picker v-model="selectedValue[index].distanceValue" 
                    			:style="selectedValue[index].distanceVisible"
                    			@on-change="dateChange($event,index)"
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
        el:"#task",
        data(){
            return {
            	showModal:false,

                loading:false,
                
                startDispatcherDisable:true,
                showConditionDisable:true,
                addGroup:true,
                
                noDataShow:'无数据',
                userId:"<%=userId%>",
                taskId:"",
                status:"",
                
                company:"<%=company%>",
                precisionId:"<%=precisionId%>",
                intoPrecisionId:"<%=precisionId%>",
                currentTask:{},
                resultSimpleColumns:[
                               {
                                   title:"条件描述",
                                   key:"fieldsName",
                               },
                               {
                                   title:"数量",
                                   key:"amount",
                               }
                           ],                   
                //用户的切分配置
                resultColumns:[
                    {
                        title:"条件描述",
                        key:"fieldsName",
                        sortable:true,
                    },
                    {
                        title:"实际客群数量",
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
                        title: '操作',
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

              //后台的所有条件
                condition:[],
                //第三个是否是多选框
                multiFlag:[],
                //所有变量的个数
                variableSize:0,
                //用户当前已经选择的值
                selectedValue:[],
				//盘点结果的总数量
                amount:0
                            
            }
        },
        mounted:function () {                                                                                                    
            this.$Message.loading("正在加载数据!");
            this.getCurrentTask();
            this.getAllResult();
        },
        methods:{
            ok(){
                this.showModal=false;
            	 //这个方法需要筛选出selectedValue中真正选择了的数据
                var x=flushValidCondition(this.selectedValue);
                if(x.length==0){
                    this.$Message.error("请先选择切分条件!")
                }else{
                    console.log("选择的条件:"+x);
                    var split=new Object();
                    var fieldDesc='';
                    for(var i=0;i<x.length;i++){
						var fieldName=x[i].fieldName;
						var fieldId=x[i].fieldId;
						var enumCode=x[i].enumCode;
						var chinese=getFieldAndEnumString(fieldId,enumCode);
						var str=fieldName+"-"+chinese+","
						fieldDesc+=str;
                    }
                    
                }
            },
            cancel(){

            },
        	addGroupAction(){
				this.$refs["selection"].data=this.result
        	},
            handleEdit (row) {
                this.$set(row, '$isEdit', true)
            },
            handleSave (row) {
                var flag=true
                if(row.amount==0){
                    this.$Message.warning("数量为0,无法配置!");
                    flag=false;
                }
                var reg = new RegExp("^[0-9]*$");
                if(row.usedAmount==null||row.usedAmount==''||!reg.test(row.usedAmount)){
                    this.$Message.warning("配置量不合理!")
                    flag=false
                }
                if(row.usedAmount>row.amount){
					this.$Message.warning("配置量过多");
					flag=false;
                }
                if(!flag){
                	row.usedAmount='';
                }
                if(row.guestGroupName==null||row.guestGroupName==''){
                    this.$Message.warning("请输入客群名称!")
                    flag=false
                }
                this.$set(row, '$isEdit', false)
                if(flag){
                    //如果已经存在了直接替换
                    for(var i=0;i<this.sampleResult.length;i++){
						var o=this.sampleResult[i];
						if(o.id==row.id){
							this.sampleResult[i]=row;
							return;
						}
                    }
                    this.sampleResult.push(row);
                }
            },
            initCondition:function(data){
            	this.condition=data;
                this.variableSize=data.length;
                for(var i=0;i<this.variableSize;i++){
                    this.multiFlag[i]=false;
                    this.selectedValue[i]=new Object()
                    this.selectedValue[i].equalVisible='';
                    this.selectedValue[i].distanceVisible='display:none;';
                    this.selectedValue[i].inVisible='display:none;';
                    this.selectedValue[i].equalValue='';
                    this.selectedValue[i].distanceValue=[];
                    this.selectedValue[i].inValue=[];
                    
                }
                this.startCountDisable=false;
                this.selectConditionDisable=false;
            },
            getAllCondition:function(){
                var x=localStorage.getItem("conditionString");
                if(x!=null){
					var data=JSON.parse(x);
					this.initCondition(data);
					return;
                }
                this.$http.get(baseUrl+'/precisionServlet.do?type=condition').then(function(res){
                    this.info(res.data)
                    if(res.data.code==200){
                        this.initCondition(res.data.data);
                        localStorage.setItem("conditionString",JSON.stringify(res.data.data));
                    }
                },function(){
                    this.$Message.error('获取所有条件失败');
                });
            },
            startConfirm(){
				if(confirm("确认开始下发?")){
					this.startDispatcher();
				}
            },
        	startDispatcher:function(){
                var result=new Array();
                for(var i=0;i<this.sampleResult.length;i++){
                    var o=this.sampleResult[i];
                    if(o.usedAmount!=null&&o.usedAmount!=''&&
                            o.usedAmount!=0&&o.usedAmount!='0'&&
                    o.guestGroupName!=null&&o.guestGroupName!=''){
                        result.push(o)
                    }
                }
                var str=JSON.stringify(result)
                if(str=='[]'){
                    this.$Message.warning("请选择下发数量和条件!")
                    return;
                }
                console.log(str)
                this.$http.post(baseUrl+'/precisionServlet.do',{
                    'type':'startSample',
                    'value':str,
                    'precisionId':this.precisionId,
                    'userId':this.userId
                },{
                    emulateJSON:true
                }).then(function (res) {
                    this.info(res.data);
                    this.flushPage();
                },function (e) {
                    this.$Message.error("下发请求失败!"+e)
                });
            },
            getAllResult:function(){
                this.$http.get(baseUrl+'/precisionServlet.do?type=getAllResult&precisionId='+this.precisionId)
                    .then(function (res) {
                        if(res.data.code==200){
                            this.result=res.data.data;
                            for(var i=0;i<this.result.length;i++){
								this.count+=this.result[i].amount;
                            }
                            console.log("切分的总数量:"+this.count);
                            this.flushPageStatus();
                        }
                    },function () {

                    })
            },
            getCurrentTask:function(){
                this.$http.get(baseUrl+'/precisionServlet.do?type=getCurrentTask&precisionId='+this.precisionId)
                    .then(function (res) {
                        if(res.data.code==200) {
                            this.currentTask = res.data.data;
                            this.taskId=this.currentTask.taskId;
                            this.precisionId=this.currentTask.precisionId;
                            if(res.data.data.userId!=this.userId){
                                this.forbidOperate()
                            }
                        }
                    },function () {
                        this.$Message.error("获取任务状态失败!");
                    })
            },
            flushPageStatus:function(){
              var data=this.currentTask;
              console.log("开始刷新页面状态:")
              console.log(this.currentTask)
              this.status=data.status
              if (this.status==1){//待盘点
                  this.startDispatcherDisable=true;
                  this.addGroup=true;
              }else if(this.status==2){//盘点中
                  this.startDispatcherDisable=true;
                  this.addGroup=true;
                  this.loading=true
              }else if(this.status==3){//盘点完成
                  this.startDispatcherDisable=false;
                  this.addGroup=false;
                  this.loading=false
              }else if(this.status==10){//待抽样
                  this.startDispatcherDisable=true;
                  this.addGroup=true;
                  this.addGroupAction()
              }else if(this.status==11){//抽样中
                  this.startDispatcherDisable=true;
                  this.addGroup=true;
                  this.addGroupAction()
              }else if (this.status==12){//上载完成
                  this.startDispatcherDisable=true;
                  this.addGroup=true;
                  this.addGroupAction()
              }
            },
            flushPage:function(){
                location.href = baseUrl+'/precisionMarketing/task.jsp?userId='+this.userId+'&precisionId='+this.precisionId+'&company='+this.company;
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
        }
    })
</script>

</body>
</html>