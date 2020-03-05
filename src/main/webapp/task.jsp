<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cpiclife.precisionmarketing.precision.Model.*"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="com.cpiclife.precisionmarketing.precision.service.*" %>
<%@ page import="java.util.*" %>
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
    <i-table size="small" :columns="columns1" :data="data1"></i-table>
</div>
<script>
    new Vue({
        el:"#task",
        data(){
            return {
                noDataText:"无数据",

            }
        }
    })
</script>

</body>
</html>