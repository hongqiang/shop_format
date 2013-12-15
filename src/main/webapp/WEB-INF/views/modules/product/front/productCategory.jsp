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
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/area/">商品分类</a></li>
	</ul>
	<tags:message content="${message}" />

	<div class="container productCategory">
		<div class="list">
			<table>
				<c:forEach items="${rootProductCategories}"
					var="rootProductCategory">
					<tr>
						<th><a href="">${rootProductCategory.name}</a></th>
						<td><c:forEach items="${rootProductCategory.children}"
								var="child">
								<a href="">${child.name}</a>
							</c:forEach></td>

					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>
</html>