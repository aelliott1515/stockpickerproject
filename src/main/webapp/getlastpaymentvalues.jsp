<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.mycompany.stockpickerproject.Stock" %>
<%@ page import="com.mycompany.stockpickerproject.DividendPayment" %>
<jsp:useBean id="smartStockView" scope="page" class="com.mycompany.stockpickerproject.PolygonSmartStockView" />
<%
List<Stock> smartStockList = smartStockView.setLastPaymentValues();
%>
[<%
    for (int x = 0; x < smartStockList.size() && x < 100; x++) {
        Stock s = smartStockList.get(x);
    %>{
    "ticker": "<%=s.getTicker()%>",
    "active": <%=s.getActive()%>,
    "cik": "<%=s.getCik()%>",
    "currencyName": "<%=s.getCurrencyName()%>",
    "locale": "<%=s.getLocale()%>",
    "market": "<%=s.getMarket()%>",
    "name": "<%=s.getName()%>",
    "primaryExchange": "<%=s.getPrimaryExchange()%>",
    "type": "<%=s.getType()%>",
    "open": <%=s.getOpen()%>,
    "close": <%=s.getClose()%>,
    "high": <%=s.getHigh()%>,
    "low": <%=s.getLow()%>,
    "note": "<%=s.getNote()%>",
    "lastDivPaymentAmount": <%=s.getLastDivPaymentAmount()%>,
    "lastDivPaymentDate": '<%=s.getLastDivPaymentDate()%>'
    }<%if (x < smartStockList.size() - 1 && x < 99) {%>,<%}%><%
    }
%>]