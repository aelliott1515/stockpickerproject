<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.mycompany.stockpickerproject.Stock" %>
<%@ page import="com.mycompany.stockpickerproject.DividendPayment" %>
<jsp:useBean id="stockCaller" scope="page" class="com.mycompany.stockpickerproject.PolygonStockAPICaller" />
<%
int waitingTime = stockCaller.getWaitingTime(session);
List<Stock> stockList = stockCaller.pollTickerList();
%>
{
    "waitingTime": <%=waitingTime%>,
    "stocks": [<%
        for (int x = 0; x < stockList.size() && x < 100; x++) {
            Stock s = stockList.get(x);
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
        "hasDividends": "?",
        "dividends": [<%
            List<DividendPayment> dpList = s.getDividendPaymentList();
            for (int y = 0; dpList != null && y < dpList.size(); y++) {
                DividendPayment dp = dpList.get(y);
                %>
                {
                    "cashAmount": <%=dp.getCashAmount()%>,
                    "currency": "<%=dp.getCurrency()%>",
                    "declarationDate": "<%=dp.getDeclarationDate()%>",
                    "dividendType": "<%=dp.getDividendType()%>",
                    "exDividendDate": "<%=dp.getExDividendDate()%>",
                    "frequency": <%=dp.getFrequency()%>,
                    "payDate": "<%=dp.getPayDate()%>",
                    "recordDate": "<%=dp.getRecordDate()%>"
                }<%if (y < dpList.size() - 1) {%>,<%}%>
                <%
            }
        %>]
        }<%if (x < stockList.size() - 1 && x < 99) {%>,<%}%><%
        }
    %>]
}
