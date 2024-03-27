
/* global smartStockList */

function populateTable(list) {
    console.log('alexmark populateTable list:', list);
    $('#smartStockListTable tr').not(':first').remove();
    for (let i = 0; i < list.length; i++) {
        let stock = list[i];
        
        let tickerTd = $(`<td class="tickerColumn">${stock.ticker}</td>`);
        
        let nameTd = $(`<td class="nameColumn">${stock.name}</td>`);
        
        let aggCloseTd = $(`<td class="aggCloseColumn">${stock.aggClose}</td>`);
        
        let closeTd = $(`<td class="closeColumn">${stock.close}</td>`);
        
        let closeDiffTd = $(`<td class="closeDiffColumn">${stock.closeDiff}</td>`);
        
        let closeDiffPercentTd = $(`<td class="closeDiffPercentColumn">${stock.closeDiffPercent}</td>`);
        
        let avgDivPaymentAmount = Math.round(stock.avgDivPaymentAmount * 100) / 100;
        let avgDivPaymentAmountTd = $(`<td>${avgDivPaymentAmount}</td>`);
        
        let lastDivPaymentAmountTd = $(`<td class="lastDivPaymentAmountColumn">${stock.lastDivPaymentAmount}</td>`);
        
        let diffDivPaymentAmount = Math.round((stock.lastDivPaymentAmount - avgDivPaymentAmount) * 100) / 100;
        let diffDivPaymentAmountTd = $(`<td class="diffDivPaymentAmountColumn">${diffDivPaymentAmount}</td>`);
        
        let lastDivPaymentDateTd = $(`<td class="lastDivPaymentDateColumn">${stock.lastDivPaymentDate}</td>`);
        
        let noteTd = $(`<td class="noteColumn">${stock.note}</td>`);
        
        let priceToDivRatio = Math.round(stock.priceToDivRatio * 1000) / 1000;
        let ratioDiv = $(`<td>${priceToDivRatio}</td>`);
        
        let tr  = $('<tr></tr>');
        tr.append(tickerTd);
        tr.append(nameTd);
        tr.append(aggCloseTd);
        tr.append(closeTd);
        tr.append(closeDiffTd);
        tr.append(closeDiffPercentTd);
        tr.append(ratioDiv);
        tr.append(avgDivPaymentAmountTd);
        tr.append(lastDivPaymentAmountTd);
        tr.append(diffDivPaymentAmountTd);
        tr.append(lastDivPaymentDateTd);
        tr.append(noteTd);
        
        $('#smartStockListTable').append(tr);
    }
}

async function getAggCloseValues() {
    let data = await $.get(`/stockpickerproject/getaggvalues.jsp?listDate=${listDate}`);
    smartStockList = JSON.parse(data);
    populateTable(smartStockList);
}

function resetSortIcons() {
    $('#smartStockListTable svg:not(.circleSortIcon)').hide();
    $('#smartStockListTable svg.circleSortIcon').show();
}

async function getLastPaymentValues() {
    $('.getLastPaymentValueButton').attr('disabled', true);
    let data = await $.get(`/stockpickerproject/getlastpaymentvalues.jsp`);
    smartStockList = JSON.parse(data);
    populateTable(smartStockList);
    $('.getLastPaymentValueButton').removeAttr('disabled');
}

let defaultColumn = 'ticker';
let sortColumn = defaultColumn;

$(document).ready(function() {
    populateTable(smartStockList);
    
    $('.checkboxRow input').click(event => {
        let column = $(event.target).data('column');
        console.log('alexmark checkbox input column:', column);
        let value = $(event.target).is(':checked');
        console.log('alexmark checkbox input value:', value);
        if (value) {
            $(`.${column}Column`).show();
        } else {
            $(`.${column}Column`).hide();
        }
    });
    
    $('.circleSortIcon').click(event => {
        console.log('alexmark circleSortIcon');
        resetSortIcons();
        let target = event.target;
        if (target.tagName.toLowerCase() === 'use') {
            target = target.parentElement;
        }
        sortColumn = $(target).data('column');
        $('#smartStockListTable .' + sortColumn + 'Th svg.circleSortIcon').hide();
        $('#smartStockListTable .' + sortColumn + 'Th svg.downCircleSortIcon').show();
        smartStockList.sort((a, b) => {
            if (a[sortColumn] < b[sortColumn]) {
                return -1;
            } else if (a[sortColumn] === b[sortColumn]) {
                return 0;
            }
            return 1;
        });
        populateTable(smartStockList);
    });
    
    $('.upCircleSortIcon').click(event => {
        resetSortIcons();
        let target = event.target;
        if (target.tagName.toLowerCase() === 'use') {
            target = target.parentElement;
        }
        let column = $(target).data('column');
        $('#smartStockListTable .' + column + 'Th svg.upCircleSortIcon').hide();
        $('#smartStockListTable .' + column + 'Th svg.circleSortIcon').show();
        //sort by default column
        smartStockList.sort((a, b) => {
            if (a[defaultColumn] < b[defaultColumn]) {
                return -1;
            } else if (a[defaultColumn] === b[defaultColumn]) {
                return 0;
            }
            return 1;
        });
        populateTable(smartStockList);
    });
    
    $('.downCircleSortIcon').click(event => {
        resetSortIcons();
        let target = event.target;
        if (target.tagName.toLowerCase() === 'use') {
            target = target.parentElement;
        }
        sortColumn = $(target).data('column');
        
        $('#smartStockListTable .' + sortColumn + 'Th svg.circleSortIcon').hide();
        $('#smartStockListTable .' + sortColumn + 'Th svg.downCircleSortIcon').hide();
        $('#smartStockListTable .' + sortColumn + 'Th svg.upCircleSortIcon').show();
        //sort by default column
        smartStockList.sort((a, b) => {
            if (a[sortColumn] < b[sortColumn]) {
                return 1;
            } else if (a[sortColumn] === b[sortColumn]) {
                return 0;
            }
            return -1;
        });
        populateTable(smartStockList);
    });
});

