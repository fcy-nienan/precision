<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.cpiclife.precisionmarketing.precision.Model.*"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="com.cpiclife.precisionmarketing.precision.service.*" %>
<%@ page import="java.util.List" %>
<html>
  <head>
    <title>$Title$</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>precision</title>
    <link href="fSelect.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" type="text/css" href="demo.css">
  </head>
  <body>
  <%--该页面是用户点击开始盘点后选择筛选条件或者修改已存在的盘点的筛选条件--%>
  <%
    WebApplicationContext context=WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
    PrecisionMetaInfoService metaInfoService=(PrecisionMetaInfoService)context.getBean("precisionMetaInfoService");
    List<PrecisionSelectVO> precisionSelectVOS = metaInfoService.makeData();

  %>
  <%
    for(int i=0;i<precisionSelectVOS.size();i++){
      PrecisionSelectVO vo=precisionSelectVOS.get(i);
  %>
  <div class="condition">
<%--    第一列所有字段名--%>
    <div style="display:inline;"><%=vo.getMetaInfo().getFieldName()%></div>
<%--  第二列该字段名下支持的操作符--%>
    <select name="<%=vo.getMetaInfo().getFieldId()%>" class="demoOperator<%=vo.getMetaInfo().getFieldId()%>" >
      <%
        for(int j=0;j<vo.getMetaInfo().operatorsList().size();j++){
          String operator = vo.getMetaInfo().operatorsList().get(j);
      %>
      <option value="<%=operator%>"><%=operator%></option>
      <%}%>
    </select>
<%--第三列改字段下的所有枚举值--%>
    <select name="enum<%=vo.getMetaInfo().getFieldId()%>" class="demoEnum<%=vo.getMetaInfo().getFieldId()%>" multiple="multiple">
      <%
        for (int j=0;j<vo.getEnumInfo().size();j++){
          PrecisionMetaEnumInfo enumInfo=vo.getEnumInfo().get(j);
      %>
      <option value="<%=enumInfo.getEnumCode()%>"><%=enumInfo.getEnumValue()%></option>
      <%}%>
    </select>
  </div>
  <%}%>
  <div class="action">
    <button value="保存" class="save">保存</button>
    <button value="取消" class="cancel">取消</button>
  </div>
  <script src="jquery.min.js"></script>
  <script src="fSelect.js"></script>
  <script>
    $(function () {
      //为所有下拉多选框添加特效
      <%
      for(int i=0;i<precisionSelectVOS.size();i++){
        PrecisionSelectVO vo=precisionSelectVOS.get(i);
      %>
      $(".demoOperator<%=vo.getMetaInfo().getFieldId()%>").fSelect();
      <%
        for (int j=0;j<vo.getEnumInfo().size();j++){
      %>
      $(".demoEnum<%=vo.getMetaInfo().getFieldId()%>").fSelect();
      <%}%>
      <%}%>
      $(".cancel").click(function () {
        <%
                      for(int i=0;i<precisionSelectVOS.size();i++){
                        PrecisionSelectVO vo=precisionSelectVOS.get(i);
                      %>
        $(".demoOperator<%=vo.getMetaInfo().getFieldId()%>").closest('.fs-wrap').find('.fs-option').removeClass('selected');
        $(".demoOperator<%=vo.getMetaInfo().getFieldId()%>").closest('.fs-wrap').find('select').fSelect('reloadDropdownLabel');

        <%
          for (int j=0;j<vo.getEnumInfo().size();j++){
            PrecisionMetaEnumInfo enumInfo=vo.getEnumInfo().get(j);
        %>
        $(".demoEnum<%=vo.getMetaInfo().getFieldId()%>").closest('.fs-wrap').find('.fs-option').removeClass('selected');
        $(".demoEnum<%=vo.getMetaInfo().getFieldId()%>").closest('.fs-wrap').find('select').fSelect('reloadDropdownLabel');

        <%}%>
        <%}%>
      });
<%--      当点击保存时统计响应的选择条件--%>
      $(".save").click(function () {
        <%
              for(int i=0;i<precisionSelectVOS.size();i++){
                PrecisionSelectVO vo=precisionSelectVOS.get(i);
              %>
        console.log($(".demoOperator<%=vo.getMetaInfo().getFieldId()%>").closest('.fs-wrap').find('select').val())
        <%
          for (int j=0;j<vo.getEnumInfo().size();j++){
            PrecisionMetaEnumInfo enumInfo=vo.getEnumInfo().get(j);
        %>
        console.log($(".demoEnum<%=vo.getMetaInfo().getFieldId()%>").closest('.fs-wrap').find('select').val())
        <%}%>
        <%}%>
      });
    });
  </script>

  </body>
</html>
