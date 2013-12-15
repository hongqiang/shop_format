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

	<div class="path">
		<a href="/admin/common/index.jhtml">首页</a> &raquo; 商品分类列表
	</div>
	<div class="bar">
		<a href="add.jhtml" class="iconButton">
			<span class="addIcon">&nbsp;</span>添加
		</a>
		<a href="javascript:;" id="refreshButton" class="iconButton">
			<span class="refreshIcon">&nbsp;</span>刷新
		</a>
	</div>
	<table id="listTable" class="list">
		<tr>
			<th>
				<span>名称</span>
			</th>
			<th>
				<span>排序</span>
			</th>
			<th>
				<span>操作</span>
			</th>
		</tr>
		<c:forEach items ="${productCategoryTree}" var = "productCategory">
			<tr>
				<td>
					<span style="margin-left: ${productCategory.grade * 20}px;">
						${productCategory.name}
					</span>
				</td>
				<td>
					${productCategory.order}
				</td>
				<td>
					<a href="${base}${productCategory.path}" target="_blank">查看</a>
					<a href="edit.jhtml?id=${productCategory.id}">编辑</a>
					<a href="javascript:;" class="delete"  val="${productCategory.id}">删除</a>
				</td>
			</tr>
			</c:forEach>
	</table>
</body>
</html>