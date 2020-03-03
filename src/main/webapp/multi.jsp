<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>利用EasyUI实现多选下拉框</title>
    <link rel="stylesheet" type="text/css" href="easyui.css" />
    <script type="text/javascript" src="jquery.min.js"></script>
    <script type="text/javascript" src="jquery.easyui.min.js"></script>
    <script type="text/javascript">
        $(function () {
            $('#ddlLine').combotree({
                valueField: "id", //Value字段
                textField: "text", //Text字段
                multiple: true,
                data: [{ "id": 1, "text": "All", "children": [{ "id": 13, "text": "C1" }, { "id": 14, "text": "C2" }, { "id": 15, "text": "C3"}]}],
                //                url: "tree_data2.json", //数据源
                onCheck: function (node, checked) {
                    //让全选不显示
                    $("#ddlLine").combotree("setText", $("#ddlLine").combobox("getText").toString().replace("全选,", ""));
                },
                onClick: function (node, checked) {
                    //让全选不显示
                    $("#ddlLine").combotree("setText", $("#ddlLine").combobox("getText").toString().replace("全选,", ""));
                }
            });
        })
    </script>
</head>
<body>
多选：<select id="ddlLine" class="easyui-combotree" style="width: 205px; height: 24px;">
</select>
</body>
</html>
