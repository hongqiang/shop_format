<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>商品分类管理</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/treetable.jsp"%>
<style type="text/css">
.brands label {
	width: 150px;
	display: block;
	float: left;
	padding-right: 6px;
}
</style>
<script type="text/javascript">
	$().ready(function() {

		var $inputForm = $("#inputForm");

		// 表单验证
		$inputForm.validate({
			rules : {
				name : "required",
				order : "digits"
			}
		});

	});
</script>
</head>
<body>
	<div class="path">
		<a href="">首页</a> &raquo; 添加商品分类
	</div>
	<form id="inputForm" action="update" method="post">
		<input type="hidden" name="id" value="${productCategory.id}" />
		<table class="input">
			<tr>
				<th><span class="requiredField">*</span>名称:</th>
				<td><input type="text" id="name" name="name" class="text"
					value="${productCategory.name}" maxlength="200" /></td>
			</tr>
			<tr>
				<th>上级分类:</th>
				<td><select name="parentId">
						<option value="">顶级分类</option>
						<c:forEach items="${productCategoryTree}" var="category">
							<%-- 							<c:if --%>
							<%-- 								test="category != productCategory && !children?seq_contains(category)"> --%>
							<%-- 								<option value="${category.id}" <c:if test="category== --%>
							<!-- 									productCategory.parent" selected="selected"/>></option> -->
							<option value="${category.id}">
								<c:if test="category.grade != 0">
									<c:forEach begin="1" end="${category.grade}">
										&nbsp;&nbsp;
									</c:forEach>
								</c:if> ${category.name}
							</option>
							<%-- 							</c:if> --%>
						</c:forEach>
				</select></td>
			</tr>
			<tr class="brands">
				<th>筛选品牌:</th>
				<td><c:forEach items="${brands}" var="brand">
						<label> <input type="checkbox" name="brandIds"
							value="${brand.id}" />${brand.name}
						</label>
					</c:forEach></td>
			</tr>
			<tr>
				<th>页面标题:</th>
				<td><input type="text" name="seoTitle" class="text"
					value="${productCategory.seoTitle}" maxlength="200" /></td>
			</tr>
			<tr>
				<th>页面关键词:</th>
				<td><input type="text" name="seoKeywords"
					value="${productCategory.seoKeywords}" class="text" maxlength="200" /></td>
			</tr>
			<tr>
				<th>页面描述:</th>
				<td><input type="text" name="seoDescription"
					value="${productCategory.seoDescription}" class="text"
					maxlength="200" /></td>
			</tr>
			<tr>
				<th>排序:</th>
				<td><input type="text" name="order" class="text" maxlength="9"
					value="${productCategory.order}" /></td>
			</tr>
			<tr>
				<th>&nbsp;</th>
				<td><input type="submit" class="button" value="确定" /> <input
					type="button" id="backButton" class="button" value="返回" /></td>
			</tr>
		</table>
	</form>
</body>
</html>