<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="stockCaller" scope="page" class="com.mycompany.stockpickerproject.PolygonStockAPICaller" />
<%
stockCaller.clearDividends();
%>
