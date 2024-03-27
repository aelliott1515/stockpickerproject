/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

let repeatStockListPoll = false;
async function populateStockList() {
    $('#populateStockListButton').attr('disabled', true);
    $('#getAggregatesButton').attr('disabled', true);
    $('#loadingMessage').show();
    repeatStockListPoll = true;
    pollStockList();
    
    let data = await $.get("/stockpickerproject/populatestocklist.jsp");
    
    repeatStockListPoll = false;
    window.data = JSON.parse(data);
}

$(document).ready(function() {
    pollStockList();
    //feather.replace();
});

function pollStockList() {
    $.get("/stockpickerproject/pollstocklist.jsp", (data) => {
        window.polldata = JSON.parse(data);
        console.log('alexmark polldata:', polldata);
        
        if (polldata.waitingTime < 0) {
            $('#timer').html('Time until resuming:');
        } else {
            $('#timer').html('Time until resuming:' + polldata.waitingTime);
            return;
        }
        
        $('#stockListTable tr').not(':first').remove();
        let stocks = polldata.stocks;
        if (stocks.length === 0) {
            $('#listSize').html('List Size:');
        } else {
            $('#listSize').html('List Size:' + stocks.length);
        }
        
        for(let ctr = 0; ctr < stocks.length; ctr++) {
            let stock = stocks[ctr];
            addMainRow(stock);
            addDividendTable(stock);
        }
        if (repeatStockListPoll) {
            window.setTimeout(pollStockList, 5000);
        }
    });
}

function addMainRow(stock) {
    let tr = $(`<tr class="stockRow_${stock.ticker}"></tr>`);
    let iPlus = $(`<div class="plusIcon_${stock.ticker}">
            <svg
                width="24"
                height="24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
            >
                <use href="feather-sprite.svg#plus-circle" />
            </svg>
    </div>`);
    $(iPlus).click(() => {
        $(`.plusIcon_${stock.ticker}`).hide();
        $(`.minusIcon_${stock.ticker}`).show();
        $(`.dividendRow_${stock.ticker}`).show();
    });
    let iMinus = $(`<div class="minusIcon_${stock.ticker}">
        <svg
            width="24"
            height="24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
        >
            <use href="feather-sprite.svg#minus-circle" />
        </svg>
    </i></div>`);
    
    $(iMinus).css('display', 'none')
            .click(() => {
        $(`.plusIcon_${stock.ticker}`).show();
        $(`.minusIcon_${stock.ticker}`).hide();
        $(`.dividendRow_${stock.ticker}`).hide();
    });
    let tdIcon = $('<td></td>');
    tdIcon.append(iPlus);
    tdIcon.append(iMinus);
    tr.append(tdIcon);
    tr.append(`<td>${stock.ticker}</td>`);
    tr.append(`<td>${stock.active}</td>`);
    tr.append(`<td>${stock.currencyName}</td>`);
    tr.append(`<td>${stock.locale}</td>`);
    tr.append(`<td>${stock.market}</td>`);
    tr.append(`<td>${stock.name}</td>`);
    tr.append(`<td>${stock.primaryExchange}</td>`);
    tr.append(`<td>${stock.type}</td>`);
    tr.append(`<td class="stockOpenCell">${stock.open}</td>`);
    tr.append(`<td>${stock.close}</td>`);
    tr.append(`<td>${stock.high}</td>`);
    tr.append(`<td>${stock.low}</td>`);
    tr.append(`<td>${stock.hasDividends}</td>`);
    $('#stockListTable').append(tr);
}

function addDividendTable(stock) {
    let table = $(`<table class="dividendTable"><tr>
        <th>Cash Amount</th>
        <th>Currency</th>
        <th>Declaration Date</th>
        <th>Div Type</th>
        <th>Ex Date</th>
        <th>Frequency</th>
        <th>Pay Date</th>
        <th>Record Date</th>
    </tr></table>`);
    for(let i = 0; i < stock.dividends.length; i++) {
        let dividend = stock.dividends[i];
        addDividendRow(table, dividend);
    }
    
    let td = $(`<td class="dividendTableContainer" colspan="14"></td>`);
    td.append(table);
    
    let tr = $(`<tr class="dividendRow_${stock.ticker}" style="display: none"></tr>`);
    tr.append(td);
    
    $('#stockListTable').append(tr);
}

function addDividendRow(table, dividend) {
    let tr = $(`<tr>
        <td>${dividend.cashAmount}</td>
        <td>${dividend.currency}</td>
        <td>${dividend.declarationDate}</td>
        <td>${dividend.dividendType}</td>
        <td>${dividend.exDividendDate}</td>
        <td>${dividend.frequency}</td>
        <td>${dividend.payDate}</td>
        <td>${dividend.recordDate}</td>
    </tr>`);
    table.append(tr);
}

async function getStockListOHLCValues() {
    let url = window.location.href;
    let matchArray = url.match(/listDate=([0-9-]+)($|&)/);
    if (matchArray && matchArray[1]) {
        repeatStockListPoll = true;
        let data = await $.get(`/stockpickerproject/getstocklistohlc.jsp?listDate=${matchArray[1]}`);
        window.data2 = JSON.parse(data);
        $('#populateStockListButton').removeAttr('disabled');
        $('#getAggregatesButton').removeAttr('disabled');
        $('#loadingMessage').hide();
        repeatStockListPoll = false;
    } else {
        console.error('listDate is missing');
    }
}

async function getDividends() {
    let url = window.location.href;
    let matchArray = url.match(/listDate=([0-9-]+)($|&)/);
    if (matchArray && matchArray[1]) {
        if (!window.polldata || !polldata.stocks) {
            console.error('polldata does not exist');
        }
        repeatStockListPoll = true;
        pollStockList();
        await $.get(`/stockpickerproject/getdividends.jsp?listDate=${matchArray[1]}`);
        repeatStockListPoll = false;
    }
}

async function clearDividends() {
    await $.get('/stockpickerproject/clearstockdividends.jsp');
    pollStockList();
}
