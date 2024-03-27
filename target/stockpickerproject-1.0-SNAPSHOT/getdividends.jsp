<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.mycompany.stockpickerproject.Stock" %>
<jsp:useBean id="stockCaller" scope="page" class="com.mycompany.stockpickerproject.PolygonStockAPICaller" />
<%
String listDate = request.getParameter("listDate");
List<Stock> stockList = stockCaller.getStockDividends(session, listDate);
%>