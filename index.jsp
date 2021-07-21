<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.hanwhalife.nbm.util.StringUtil"%>
<%
  String mpno = (String)session.getAttribute("MPNO");
%>
<html>
<head>
<script language="JavaScript" src="online/cm/js/hli_ssoutil.js"></script>  
<script type="text/javascript">
<%if (!StringUtil.isBlank(mpno)){%>
<jsp:forward page="/main.do"/>
<%} else {%>
<jsp:forward page="/login.do"/>
<%}%>
</script>
</head>
<body>
</body>
</html>
