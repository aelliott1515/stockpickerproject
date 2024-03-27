<%@ page import="java.util.List" %>
<%@ page import="com.mycompany.stockpickerproject.Stock" %>
<%@ page import="com.mycompany.stockpickerproject.DividendPayment" %>
<jsp:useBean id="smartStockView" scope="page" class="com.mycompany.stockpickerproject.PolygonSmartStockView" />
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
List<Stock> smartStockList = smartStockView.getSmartStockList();
String listDate = request.getParameter("listDate");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Smart Stock List</title>
        <script src="lib/feather-min.js"></script>
        <script src="lib/jquery.js"></script>
        <script src="smartstockview.js"></script>
        <script>
            var listDate = '<%=listDate%>';
            var smartStockList = [<%
for (int i=0; i<smartStockList.size(); i++) {
    Stock s = smartStockList.get(i);
    %>{
        ticker: '<%=s.getTicker()%>',
        name: '<%=s.getName()%>',
        close: <%=s.getClose()%>,
        aggClose: <%=s.getAggClose()%>,
        closeDiff: '<%=s.getCloseDiff()%>',
        closeDiffPercent: '<%=s.getCloseDiffPercent()%>',
        priceToDivRatio: <%=s.getPriceToDivRatio()%>,
        note: '<%=s.getNote()%>',
        avgDivPaymentAmount: <%=s.getAvgDivPaymentAmount()%>,
        lastDivPaymentAmount: <%=s.getLastDivPaymentAmount()%>,
        lastDivPaymentDate: '<%=s.getLastDivPaymentDate()%>'
    }<%if (i < smartStockList.size() - 1) {%>,<%}%><%
}
            %>];
        </script>
        <style>
        .checkboxRow {
            padding: 5px 0 0 0;
        }
        .checkboxRow input {
            margin-left: 5px;
        }
        table, td, th {
          border: 1px solid;
        }
        th {
            padding: 0 3px 0 3px;
        }

        table {
          border-collapse: collapse;
        }
        </style>
    </head>
    <body>
        <div><a href="viewstocklist.jsp?listDate=<%=listDate%>">Stock List</a></div>
        <div style="padding-top: 5px">
            <button type="button" onclick="getAggCloseValues()">Get Agg Close Values</button>
            <button
                type="button"
                class="getLastPaymentValueButton"
                onclick="getLastPaymentValues()"
            >Get Last Payment Values</button>
        </div>
        <div class="checkboxRow">
            <!--
            Name 	Agg Close 	Close 	Close difference 	Close difference percent 	
            Price to dividend ratio 	Avg Div Amount 	Last Div Amount 	Diff Div Amount	Last Div Date 	Note
            -->
            <input type="checkbox" name="nameCheckbox" class="nameCheckbox" data-column="name" checked /> Name
            <input type="checkbox" name="aggCloseCheckbox" class="aggCloseCheckbox" data-column="aggClose" checked /> Agg Close
            <input type="checkbox" name="closeCheckbox" class="closeCheckbox" data-column="close" checked /> Close
            <input type="checkbox" name="closeDiffCheckbox" class="closeDiffCheckbox" data-column="closeDiff" checked /> Close Diff
            <input type="checkbox" name="closeDiffPercentCheckbox" class="closeDiffPercentCheckbox" data-column="closeDiffPercent" checked /> Close Diff Percent
        </div>
        <div class="checkboxRow">
            <input type="checkbox" name="priceToDivRatioCheckbox" class="priceToDivRatioCheckbox" data-column="priceToDivRatio" checked /> PriceTo Div Ratio
            <input type="checkbox" name="avgDivPaymentAmountCheckbox" class="avgDivPaymentAmountCheckbox" data-column="avgDivPaymentAmount" checked /> Avg Div Payment Amount
            <input type="checkbox" name="lastDivPaymentAmountCheckbox" class="lastDivPaymentAmountCheckbox" data-column="lastDivPaymentAmount" checked /> Last Div Payment Amount
            <input type="checkbox" name="diffDivPaymentAmountCheckbox" class="diffDivPaymentAmountCheckbox" data-column="diffDivPaymentAmount" checked /> Diff Div Payment Amount
            <input type="checkbox" name="lastDivPaymentDateCheckbox" class="lastDivPaymentDateCheckbox" data-column="lastDivPaymentDate" checked /> Last Div Payment Date
            <input type="checkbox" name="noteCheckbox" class="noteCheckbox" data-column="note" checked /> Note
        </div>
        <table id="smartStockListTable">
            <tr>
                <th class="tickerTh tickerColumn">Ticker
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="ticker"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="ticker"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="ticker"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="nameTh nameColumn">Name
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="name"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="name"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="name"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="aggCloseTh aggCloseColumn">Agg Close
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="aggClose"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="aggClose"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="aggClose"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="closeTh closeColumn">Close
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="close"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="close"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="close"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="closeDiffTh closeDiffColumn">Close difference
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="closeDiff"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="closeDiff"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="closeDiff"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="closeDiffPercentTh closeDiffPercentColumn">Close difference percent
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="closeDiffPercent"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="closeDiffPercent"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="closeDiffPercent"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="priceToDivRatioTh priceToDivRatioColumn">Price to dividend ratio
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="priceToDivRatio"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="priceToDivRatio"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="priceToDivRatio"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="avgDivPaymentAmountTh avgDivPaymentAmountColumn">Avg Div Amount
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="avgDivPaymentAmount"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="avgDivPaymentAmount"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="avgDivPaymentAmount"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="lastDivPaymentAmountTh lastDivPaymentAmountColumn">Last Div Amount
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="lastDivPaymentAmount"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="lastDivPaymentAmount"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="lastDivPaymentAmount"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="diffDivPaymentAmountTh diffDivPaymentAmountColumn">Diff Div Amount</th>
                <th class="lastDivPaymentDateTh lastDivPaymentDateColumn">Last Div Date
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="lastDivPaymentDate"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="lastDivPaymentDate"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="lastDivPaymentDate"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
                <th class="noteTh noteColumn">Note
                    <svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="circleSortIcon"
                        data-column="note"
                    >
                        <use href="feather-sprite.svg#circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="upCircleSortIcon"
                        data-column="note"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-up-circle" />
                    </svg><!--
                    --><svg
                        width="12" height="12"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        class="downCircleSortIcon"
                        data-column="note"
                        style="display: none"
                    >
                        <use href="feather-sprite.svg#arrow-down-circle" />
                    </svg><!--
                --></th>
            </tr>
        </table>
    </body>
</html>
