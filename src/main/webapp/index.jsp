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
        <i-table height="300" style="text-align:center;" :columns="taskColumns" :data="taskList" :no-data-text="noDataShow"></i-table>
        <Page :current="pageIndex" :page-size="pageSize" show-total :total="total"  @on-change="changePage"></Page>
    </div>
    <div style="width:100%;height:40px;border:1px solid yellow;"></div>
    <div style="text-align:center;">
        <div style="width:100%;text-align:center;height:150px;border:1px solid red;overflow:scroll;">
            <template v-for="(vo,index) in condition">
                <Row>
                    <i-col span="6">
                        {{ vo.metaInfo.fieldName }}
                    </i-col>
                    <i-col span="6">
                        <i-select  @on-change="operatorChange($event,vo.metaInfo.fieldCode,index)"  placeholder="请选择关系" clearable style="width:260px">
                            <i-option v-for="(operator,index) in vo.operators" :value="operator">{{ operator }}</i-option>
                        </i-select>
                    </i-col>
                    <i-col span="10">
                        <i-select  @on-change="changeValue($event,vo.metaInfo.fieldCode,index)"  :multiple="multiFlag[index]" placeholder="枚举值" style="width:260px">
                            <i-option v-for="(enumValue,index) in vo.enumInfo" :value="enumValue.enumCode">{{ enumValue.enumValue }}</i-option>
                        </i-select>
                    </i-col>
                </Row>
            </template>
        </div>
        <div style="width:100%;height:40px;text-align:center;border:1px solid green;">
            <Row style="text-align:center;">
                <i-button type="info" @click="save" :disabled="saveDisabled">保存</i-button>
                <i-button type="info" @click="startCount" :disabled="startCountDisabled">开始盘点</i-button>
            </Row>
        </div>
    </div>

<!--    下面的表格显示用户已经选择的条件-->
    <i-table height="200" :columns="selectTableHeader" :data="waitForCountValue" :no-data-text="noDataShow"></i-table>
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
                        key:'status'
                    },
                    {
                        title:"分公司",
                        key:'company'
                    },
                    {
                        title: '操作',
                        slot: 'action',
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


                //按钮禁用
                startCountDisabled:false,
                saveDisabled:false,
                cancelDisabled:false,

                //当前任务状态
                status:0,

                selectTableHeader:[
                    {
                        "title":"变量名",
                        "key":"fieldCode"
                    },
                    {
                        "title":"条件",
                        "key":"operator"
                    },
                    {
                        "title":"枚举值",
                        "key":"enumValue"
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
                //用户上一次选完之后还没开始盘点的值
                waitForCountValue:[],
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
                result:[]
            }
        },
        mounted:function () {
            this.getAllCondition()
            this.getAllTask()
        },
        methods: {
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
                        console.log("获取用户所有可视的任务成功!")
                        this.taskList=res.data;
                    },function (e) {
                        alert("网络错误或者系统出错!"+e)
                    })
            },
            startCount:function(){
                console.log("开始盘点")
                console.log("将要传输到后台保存的对象:" + this.selectedValue)
                var str = JSON.stringify(this.selectedValue)
                console.log("转换为json字符串后:" + str)
                this.$http.post(
                    'http://localhost:8080/startCount',
                    {
                        "value": str,
                        "userId": this.userId,
                        "precision":this.precisionId,
                        "company":this.company
                    },
                    {emulateJSON: true}
                ).then(function (res) {
                    alert("创建盘点任务成功!")
                    this.startCountDisabled=true;
                    this.saveDisabled=true
                },function () {
                    console.log("开始盘点请求失败!")
                    alert("创建盘点请求失败!")
                });
            },
            getAllSelected:function(){
              this.$http.get('http://localhost:8080/selectedCondition?userId='+this.userId+'&taskId='+this.taskId)
              .then(function(res){
                  console.log(res.data)
                  this.waitForCountValue=res.data;
                },function () {
                  console.log("获取用户已经选择条件失败!")
              });
            },
            getAllCondition:function(){
                this.$http.get('http://localhost:8080/data').then(function(res){
                    console.log(res.data)
                    this.condition=res.data;
                    this.variableSize=res.data.length;
                    for(var i=0;i<this.variableSize;i++){
                        this.multiFlag[i]=false;
                        this.selectedValue[i]=new Object()
                    }
                },function(){
                    console.log('请求失败处理');
                });
            },
            //保存条件
            save:function(){
                //先要检测是否有完整的一个对象
                console.log("将要传输到后台保存的对象:" + this.selectedValue)
                var str = JSON.stringify(this.selectedValue)
                console.log("转换为json字符串后:" + str)
                this.$http.post(
                    'http://localhost:8080/save/',
                    {
                        "value": str,
                        "userId": this.userId,
                        "taskId": this.taskId
                    },
                    {emulateJSON: true}
                ).then(function (res) {
                    alert("保存成功")
                    location.href="http://localhost:8080/index";
                })
            },
            operatorChange:function(v,fieldCode,index){
                if(v=="in"){
                    if(!this.multiFlag[index]){
                        this.$set(this.multiFlag,index,true)
                    }
                }else{
                    if(this.multiFlag[index]){
                        this.$set(this.multiFlag,index,false)
                    }
                }
                this.selectedValue[index].fieldCode=fieldCode;
                this.selectedValue[index].operator=v;
                console.log(index+" "+fieldCode+"  "+v)
                console.log(this.selectedValue)
            },
            changeValue:function (v,fieldCode,index) {
                console.log(index+"  "+fieldCode+"  "+v)
                this.selectedValue[index].fieldCode=fieldCode;
                this.selectedValue[index].enumValue=v;
                console.log(this.selectedValue)
            }
        },
    })
</script>

</body>
</html>