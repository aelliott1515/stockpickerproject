/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.stockpickerproject;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexelliott
 */
public class PolygonSmartStockView {

    private StockDatabaseInterface db = null;
    
    private void initializeDB() {
        if (db == null) {
            try {
                db = new StockDatabaseInterface();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(PolygonStockAPICaller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public List<Stock> getSmartStockList() {
        initializeDB();
        Calendar c = Calendar.getInstance();
        var year = c.get(Calendar.YEAR);
        return db.getSmartStockList();
    }
    
    public List<Stock> setAggValues(HttpSession session, String listDate) throws ParseException, IOException {
        initializeDB();
        
        System.out.println("Before getting stock list:" + System.currentTimeMillis());
        List<Stock> stockList = db.getStockList(false, false);
        System.out.println("After getting stock list:" + System.currentTimeMillis());
        
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        c.setTime(sdf.parse(listDate));
        c.add(Calendar.MONTH, -6);
        String listDateHalfYearAgo = sdf.format(c.getTime());
        System.out.println("alexmark listDateHalfYearAgo:" + listDateHalfYearAgo);
        
        var url = "https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/" + listDateHalfYearAgo + "?adjusted=true&include_otc=true";
        
        boolean shouldDelay = true;
        while (shouldDelay) {
            Map<String, Object> map = PolygonStockUtil.getMapFromURL(url);
            shouldDelay = PolygonStockUtil.shouldDelayPolygonCall(map);
            
            if (shouldDelay) {
                PolygonStockUtil.waitOneMinute(session);
            } else {
                setAggOHLCValuesForStockList(stockList, map);
            }
        }
        for (Stock st : stockList) {
            db.updateStock(st);
        }

        return db.getSmartStockList();
    }
    
    private void setAggOHLCValuesForStockList(List<Stock> stockList, Map<String, Object> map) {
        ArrayList results = (ArrayList) map.get("results");
        for (int ctr = 0; ctr < stockList.size(); ctr++) {
            Stock st = stockList.get(ctr);
            for (int ctr2 = 0; ctr2 < results.size(); ctr2++) {
                Map<String, Object> stockOHLCValues = (Map<String, Object>) results.get(ctr2);
                //only set values for zero value stocks
                if (stockOHLCValues.get("T").equals(st.getTicker())) {
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
                    st.setAggOpen(open);
                    st.setAggHigh(high);
                    st.setAggLow(low);
                    st.setAggClose(close);
                    break;
                }
            }
        }
    }
    
    public List<Stock> setLastPaymentValues() {
        initializeDB();
        System.out.println("Before getting stock list:" + System.currentTimeMillis());
        List<Stock> stockList = db.getStockList(false, false);
        System.out.println("After getting stock list:" + System.currentTimeMillis());

        for (Stock st : stockList) {
            db.setLastPaymentValues(st);
        }

        return db.getSmartStockList();
    }
}
