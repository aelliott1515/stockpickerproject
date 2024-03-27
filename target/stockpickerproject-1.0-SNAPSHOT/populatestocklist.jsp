<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.mycompany.stockpickerproject.Stock" %>
<jsp:useBean id="stockCaller" scope="page" class="com.mycompany.stockpickerproject.PolygonStockAPICaller" />
[<%
List<Stock> stockList = stockCaller.buildTickerList(session);

for (int ctr = 0; ctr < stockList.size(); ctr++) {
    Stock s = stockList.get(ctr);
%>
    {
        "ticker": "<%=s.getTicker()%>",
        "active": "<%=s.getActive()%>",
	"cik": "<%=s.getCik()%>",
	"currencyName": "<%=s.getCurrencyName()%>",
	"locale": "<%=s.getLocale()%>",
	"market": "<%=s.getMarket()%>",
	"name": "<%=s.getName()%>",
	"primaryExchange": "<%=s.getPrimaryExchange()%>",
	"type": "<%=s.getType()%>",
	"open": "<%=s.getOpen()%>",
	"close": "<%=s.getClose()%>",
	"high": "<%=s.getHigh()%>",
	"low": "<%=s.getLow()%>",
        "hasDividends": "?"
    }<%if(ctr < stockList.size() - 1) {%>,<%}%>
<%
}
%>
]
