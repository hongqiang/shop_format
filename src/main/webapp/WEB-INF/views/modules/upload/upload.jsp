<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>商品分类管理</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/treetable.jsp"%>
<script type="text/javascript">
	$(document).ready(function() {
		$("#treeTable").treeTable({
			expandLevel : 5
		});
	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	$(document).ready(function() {

		var $delete = $("#listTable a.delete");
		$delete.click(function() {
			var $this = $(this);
			$.dialog({
				type: "warn",
				content: "删除 "+ $this.attr("val2")+"?",
				onOk: function() {
					$.ajax({
						url: "delete",
						type: "POST",
						data: {id: $this.attr("val")},
						dataType: "json",
						cache: false,
						success: function(message) {
							$.message(message);
							if (message.type == "success") {
								$this.closest("tr").remove();
							}
						}
					});
				}
			});
			return false;
		});

	});
</script>
</head>
<body>
   
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/area/">商品分类</a></li>
	</ul>
	<tags:message content="${message}" />
  <h1>Please upload a file</h1>  
        <form method="POST" action="${ctx}/product_category/form" enctype="multipart/form-data">  
<%--          <form method="POST" action="${ctx}/form" enctype="multipart/form-data">  --%>
            <input type="text" name="name"/>  
            <input type="file" name="file"/>  
<%--            <shiro:hasPermission name="sys:area:edit"> <input type="submit"/>  </shiro:hasPermission> --%>
           <input type="submit"/>
        </form>  
</body>
</html>
