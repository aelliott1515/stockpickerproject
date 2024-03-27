/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.stockpickerproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexelliott
 */
public class PolygonStockAPICaller {

    private final int nameLengthRestriction = 27;//used to restrict number of stocks queried to a manageable number
    private StockDatabaseInterface db = null;
    //number of years to go back when getting dividend data, needs to be a negative number
    final private int historyYearsLimit = -5;

    /**
     * Creates a new instance of NameHandler
     */
    public PolygonStockAPICaller() {
    }
    
    private void initializeDB() {
        if (db == null) {
            try {
                db = new StockDatabaseInterface();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(PolygonStockAPICaller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("SleepWhileInLoop")
    public List<Stock> buildTickerList(HttpSession session) throws IOException, SQLException, ClassNotFoundException {
        //curl -H "Authorization: OAuth <ACCESS_TOKEN>" http://www.example.com
        //String command = "curl -H \"Authorization:Bearer QlGEeuVRXMkpJ4iVqZWUeSooCoPJxpGK\" https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/2023-01-09/2023-01-09";
        //String[] command = {"curl", "-H", "Authorization:Bearer QlGEeuVRXMkpJ4iVqZWUeSooCoPJxpGK", "https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/2023-01-09/2023-01-09"};
        int resultSize = 1000;
        ArrayList<Stock> output = new ArrayList<>();
        String ticker = "";
        initializeDB();
        db.clearTickerTable();
        db.clearDividendPaymentTable();

        while(resultSize == 1000) {
            String url = "https://api.polygon.io/v3/reference/tickers?market=stocks&active=true&limit=1000&sort=ticker";
            if (!ticker.isEmpty()) {
                url += "&ticker.gt=" + ticker;
            }
            System.out.println("Making request to Polygon:" + url);
            Map<String, Object> map = PolygonStockUtil.getMapFromURL(url);
            
            boolean shouldDelay = PolygonStockUtil.shouldDelayPolygonCall(map);
            
            if (shouldDelay) {
                PolygonStockUtil.waitOneMinute(session);
            } else {
                List<Stock> stockList = getStockListFromMap(map);
                resultSize = stockList.size();
                if (resultSize > 0) {
                    Stock lastStock = stockList.get(resultSize - 1);
                    ticker = lastStock.getTicker();
                }
                for (int ctr = stockList.size() - 1; ctr >= 0; ctr--) {
                    Stock s = stockList.get(ctr);
                    //remove stocks with names that are too long, or tickers with lowercase letters
                    if (s.getName().length() >= nameLengthRestriction || s.getTicker().matches(".*[a-z\\.].*")) {
                        stockList.remove(s);
                    }
                }
                output.addAll(stockList);
                if (resultSize > 0) {
                    db.insertTickers(stockList);
                }
            }
        }
        return output;
    }

    private List<Stock> getStockListFromMap(Map<String, Object> map) {
        List results = (ArrayList) map.get("results");
        System.out.println("Results size:" + results.size());
        ArrayList<Stock> output = new ArrayList<>();
        for (var r : results) {
            LinkedHashMap result = (LinkedHashMap) r;
            String ticker = (String) result.get("ticker");
            String type = (String) result.get("type");
            Boolean active = (Boolean) result.get("active");
            String cik = (String) result.get("cik");
            String currency_name = (String) result.get("currency_name");
            //"2023-09-15T00:00:00Z"
            String last_updated_utc = (String) result.get("last_updated_utc");
            last_updated_utc = last_updated_utc.replaceAll("T.*$", "");
            
            String locale = (String) result.get("locale");
            String market = (String) result.get("market");
            String name = ((String) result.get("name")).replaceAll("[^a-zA-Z0-9 ]", "");
            String primary_exchange = (String) result.get("primary_exchange");
            
            Stock stock = new Stock(ticker, type, active, cik, currency_name, last_updated_utc, locale, market, name, primary_exchange);
            output.add(stock);
        }
        return output;
    }
    
    public int getWaitingTime(HttpSession session) {
        Double waitingTimeDouble = (Double) session.getAttribute("waitingTimeDouble");
        if (waitingTimeDouble == null || waitingTimeDouble < 0) {
            return -1;
        }
        double currentTime = System.currentTimeMillis();
        double differenceInSeconds = Math.floor(currentTime - waitingTimeDouble) / 1000;//difference in seconds
        return (int) differenceInSeconds;
    }
    
    public List<Stock> pollTickerList() {
        initializeDB();
        return db.getStockList(true, true);
    }
    
    public List<Stock> getStockListOHLC(HttpSession session, String listDate) throws IOException, ParseException {
        //check that listDate is in expected format
        checkListDate(listDate);
        
        initializeDB();
        String url = "https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/" + listDate + "?adjusted=true&include_otc=true";
        System.out.println("Before getting stock list:" + System.currentTimeMillis());
        List<Stock> output = db.getStockList(false, false);
        System.out.println("After getting stock list:" + System.currentTimeMillis());
        
        boolean shouldDelay = true;
        while (shouldDelay) {
            Map<String, Object> map = PolygonStockUtil.getMapFromURL(url);
            shouldDelay = PolygonStockUtil.shouldDelayPolygonCall(map);
            
            if (shouldDelay) {
                PolygonStockUtil.waitOneMinute(session);
            } else {
                setOHLCValuesForStockList(output, map);
            }
        }
        
        //for stocks that still have close values of zero, get aggregate values from six months ago
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        c.setTime(sdf.parse(listDate));
        c.add(Calendar.MONTH, -6);
        String listDateHalfYearAgo = sdf.format(c.getTime());
        System.out.println("alexmark listDateOneYearAgo:" + listDateHalfYearAgo);
        
        url = "https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/" + listDateHalfYearAgo + "?adjusted=true&include_otc=true";
        
        while (shouldDelay) {
            Map<String, Object> map = PolygonStockUtil.getMapFromURL(url);
            shouldDelay = PolygonStockUtil.shouldDelayPolygonCall(map);
            
            if (shouldDelay) {
                PolygonStockUtil.waitOneMinute(session);
            } else {
                setOHLCValuesForStockListZeroValues(output, map);
            }
        }
        
        for (Stock st : output) {
            db.updateStock(st);
        }
        return output;
    }
    
    private void checkListDate(String listDate) {
        if (listDate == null || listDate.length() != 10 || listDate.matches(".*[^0-9-].*")) {
            throw new RuntimeException("List date in improper format:" + listDate);
        }
    }

    private void setOHLCValuesForStockList(List<Stock> stockList, Map<String, Object> map) {
        ArrayList results = (ArrayList) map.get("results");
        for (int ctr = 0; ctr < stockList.size(); ctr++) {
            Stock st = stockList.get(ctr);
            if (st.getTicker().equals("POWL")) {
                System.out.println("alexmark POWL ticker");
            }
            for (int ctr2 = 0; ctr2 < results.size(); ctr2++) {
                Map<String, Object> stockOHLCValues = (Map<String, Object>) results.get(ctr2);
                if (stockOHLCValues.get("T").equals(st.getTicker())) {
                    if (st.getTicker().equals("POWL")) {
                        System.out.println("alexmark POWL close:" + stockOHLCValues.get("c"));
                    }
                    //correct pairing found, set values
                    Object openObject = stockOHLCValues.get("o");
                    Object highObject = stockOHLCValues.get("h");
                    Object lowObject = stockOHLCValues.get("l");
                    Object closeObject = stockOHLCValues.get("c");
                    double open;
                    double high;
                    double low;
                    double close;
                    if (openObject instanceof Integer) {
                        open = (Integer) openObject;
                    } else {
                        open = (Double) openObject;
                    }
                    if (highObject instanceof Integer) {
                        high = (Integer) highObject;
                    } else {
                        high = (Double) highObject;
                    }
                    if (lowObject instanceof Integer) {
                        low = (Integer) lowObject;
                    } else {
                        low = (Double) lowObject;
                    }
                    if (closeObject instanceof Integer) {
                        close = (Integer) closeObject;
                    } else {
                        close = (Double) closeObject;
                    }
                    st.setOpen(open);
                    st.setHigh(high);
                    st.setLow(low);
                    st.setClose(close);
                    break;
                }
            }
        }
    }
    
    private void setOHLCValuesForStockListZeroValues(List<Stock> stockList, Map<String, Object> map) {
        ArrayList results = (ArrayList) map.get("results");
        for (int ctr = 0; ctr < stockList.size(); ctr++) {
            Stock st = stockList.get(ctr);
            for (int ctr2 = 0; ctr2 < results.size(); ctr2++) {
                Map<String, Object> stockOHLCValues = (Map<String, Object>) results.get(ctr2);
                //only set values for zero value stocks
                if (stockOHLCValues.get("T").equals(st.getTicker()) && st.getOpen() == 0 && st.getClose() == 0 &&
                        st.getHigh() == 0 && st.getLow() == 0) {
                    System.out.println("alexmark setting value for zero value stock:" + st.getTicker());
                    //correct pairing found, set values
                    Object openObject = stockOHLCValues.get("o");
                    Object highObject = stockOHLCValues.get("h");
                    Object lowObject = stockOHLCValues.get("l");
                    Object closeObject = stockOHLCValues.get("c");
                    double open;
                    double high;
                    double low;
                    double close;
                    if (openObject instanceof Integer) {
                        open = (Integer) openObject;
                    } else {
                        open = (Double) openObject;
                    }
                    if (highObject instanceof Integer) {
                        high = (Integer) highObject;
                    } else {
                        high = (Double) highObject;
                    }
                    if (lowObject instanceof Integer) {
                        low = (Integer) lowObject;
                    } else {
                        low = (Double) lowObject;
                    }
                    if (closeObject instanceof Integer) {
                        close = (Integer) closeObject;
                    } else {
                        close = (Double) closeObject;
                    }
                    st.setOpen(open);
                    st.setHigh(high);
                    st.setLow(low);
                    st.setClose(close);
                    break;
                }
            }
        }
    }
    
    public List<Stock> getStockDividends(HttpSession session, String listDate) throws IOException {
        List<Stock> output = new ArrayList<>();
        List<String> uniqueTickerList = new ArrayList<>();
        initializeDB();
        db.clearDividendPaymentTable();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, historyYearsLimit);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String exDividendDate = sdf.format(c.getTime());
        
        boolean shouldContinueCallingPolygon = true;
        String ticker = "";
        while(shouldContinueCallingPolygon) {
            String url = "https://api.polygon.io/v3/reference/dividends?dividend_type=CD&limit=1000&sort=ticker&" +
                "ex_dividend_date.gte=" + exDividendDate + "&ex_dividend_date.lte=" + listDate;
            if (!ticker.isEmpty()) {
                url += "&ticker.gte=" + ticker;
            }
            System.out.println("Getting dividend list for ticker:" + ticker);
            boolean shouldDelay = true;
            while (shouldDelay) {
                Map<String, Object> map = PolygonStockUtil.getMapFromURL(url);
                shouldDelay = PolygonStockUtil.shouldDelayPolygonCall(map);

                if (shouldDelay) {
                    PolygonStockUtil.waitOneMinute(session);
                } else {
                    List<DividendPayment> dividendList = getDividendListFromMap(map);
                    //iterate through the list of dividends and get the list of stocks that have dividends
                    for (DividendPayment dp : dividendList) {
                        if (!uniqueTickerList.contains(dp.getTicker())) {
                            uniqueTickerList.add(dp.getTicker());
                        }
                    }

                    //insert dividend payments
                    db.insertDividendPayments(dividendList);

                    //save the ticker and record date of last dividend payment
                    //if saved ticker is same as ticker of last dividend payment, assume we have reached the end of the list
                    DividendPayment lastDP = dividendList.get(dividendList.size() - 1);
                    if (ticker.equalsIgnoreCase(lastDP.getTicker()) || dividendList.size() < 1000) {
                        shouldContinueCallingPolygon = false;
                    } else {
                        ticker = lastDP.getTicker();
                    }
                }
            }
        }
        db.updateHasDividendsFieldsForStockTickers(uniqueTickerList);
        return output;
    }

    private List<DividendPayment> getDividendListFromMap(Map<String, Object> map) {
        List results = (ArrayList) map.get("results");
        System.out.println("Results size:" + results.size());
        ArrayList<DividendPayment> output = new ArrayList<>();
        for (var r : results) {
            //"cash_amount":0.132,"currency":"USD","declaration_date":"2017-03-16","dividend_type":"CD",
            //"ex_dividend_date":"2017-03-31","frequency":4,"pay_date":"2017-04-26","record_date":"2017-04-04","ticker":"A"
            LinkedHashMap result = (LinkedHashMap) r;
            String ticker = (String) result.get("ticker");
            Object cashAmountObject = result.get("cash_amount");
            Double cashAmount;
            if (cashAmountObject instanceof Double) {
                cashAmount = (Double) cashAmountObject;
            } else {
                cashAmount = Double.valueOf((Integer) cashAmountObject);
            }
            String currency = (String) result.get("currency");
            String declarationDate = (String) result.get("declaration_date");
            String dividendType = (String) result.get("dividend_type");
            String exDividendDate = (String) result.get("ex_dividend_date");
            Integer frequency = (Integer) result.get("frequency");
            String payDate = (String) result.get("pay_date");
            String recordDate = (String) result.get("record_date");
            
            DividendPayment dp = new DividendPayment(ticker, cashAmount, currency, declarationDate, dividendType,
                    exDividendDate, frequency, payDate, recordDate);
            output.add(dp);
        }
        return output;
    }
}
