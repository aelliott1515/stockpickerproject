<%-- 
    Document   : newjsp
    Created on : Sep 13, 2023, 4:15:24 PM
    Author     : alexelliott
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="stockCaller" scope="page" class="com.mycompany.stockpickerproject.PolygonStockAPICaller" />
<%
String listDate = request.getParameter("listDate");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View Stock List</title>
        <script src="lib/feather-min.js"></script>
        <script src="lib/jquery.js"></script>
        <script src="viewstocklist.js"></script>
        <style>
        table, td, th {
          border: 1px solid;
        }
        th {
            padding: 0 3px 0 3px;
        }

        table {
          border-collapse: collapse;
        }
        button {
            margin-right: 5px;
        }
        span {
            padding-right: 5px;
            padding-top: 5px;
        }
        .dividendTableContainer {
            padding: 0;
        }
        .dividendTable {
            margin-left: 5px;
            background-color: #ffffcc;
        }
        </style>
        <script>
            var listDate = "<%=request.getParameter("listDate")%>";
        </script>
    </head>
    <body>
        <a href="smartstockview.jsp?listDate=<%=listDate%>">Smart Stock List</a><br />
        <button type="button" id="populateStockListButton" onclick="populateStockList()">Populate stock list</button>
        <button type="button" id="getStockListOHLCValuesButton" onclick="getStockListOHLCValues()">Get stock list OHLC values</button>
        <button type="button" id="getDividendsButton" onclick="getDividends()">Get dividends</button>
        <button type="button" id="clearDividendsButton" onclick="clearDividends()">Clear dividends</button>
        <span id="loadingMessage" style="visibility: hidden">Loading...</span>
        <span id="timer">Time until resuming:</span>
        <span id="listSize">List Size:</span>
        <table id="stockListTable" style="margin-top: 10px">
            <tr>
                <th>&nbsp;</th>
                <th>Ticker</th>
                <th>Active</th>
                <th>Currency Name</th>
                <th>Locale</th>
                <th>Market</th>
                <th>Name</th>
                <th>Primary Exchange</th>
                <th>Type</th>
                <th>Open</th>
                <th>Close</th>
                <th>High</th>
                <th>Low</th>
                <th>Has Dividends</th>
            </tr>
        </table>
    </body>
</html>
