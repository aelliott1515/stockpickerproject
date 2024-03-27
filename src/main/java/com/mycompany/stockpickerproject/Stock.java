/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.stockpickerproject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexelliott˙
 */
public class Stock {

    private String ticker;
    private String type;
    private Boolean active;
    private String cik;
    private String currencyName;
    private String lastUpdatedUTC;
    private String locale;
    private String market;
    private String name;
    private String primaryExchange;
    private double aggOpen = 0.0;
    private double open = 0.0;
    private double aggClose = 0.0;
    private double close = 0.0;
    private double aggHigh = 0.0;
    private double high = 0.0;
    private double aggLow = 0.0;
    private double low = 0.0;
    private String hasDividends = "?";
    private List<DividendPayment> dividendPaymentList = new ArrayList<>();
    private double priceToDivRatio = 0.0;
    private String note = "";
    private double lastDivPaymentAmount = 0.0;
    private String lastDivPaymentDate = "";
    private double avgDivPaymentAmount = 0.0;
    
    public Stock(String ticker, String type, Boolean active, String cik, String currencyName,
            String lastUpdatedUTC, String locale, String market, String name, String primaryExchange) {
        this.ticker = ticker;
        this.type = type;
        this.active = active;
        this.cik = cik;
        this.currencyName = currencyName;
        this.lastUpdatedUTC = lastUpdatedUTC;
        this.locale = locale;
        this.market = market;
        this.name = name;
        this.primaryExchange = primaryExchange;
    }
    
    public Stock(String ticker, String type, Boolean active, String cik, String currencyName,
            String lastUpdatedUTC, String locale, String market, String name, String primaryExchange,
            Double open, Double close, Double high, Double low, String hasDividends) {
        this.ticker = ticker;
        this.type = type;
        this.active = active;
        this.cik = cik;
        this.currencyName = currencyName;
        this.lastUpdatedUTC = lastUpdatedUTC;
        this.locale = locale;
        this.market = market;
        this.name = name;
        this.primaryExchange = primaryExchange;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.hasDividends = hasDividends;
    }
    
    public String getTicker() {
        return ticker;
    }

    public String getType() {
        return type;
    }

    public Boolean getActive() {
        return active;
    }

    public String getCik() {
        return cik;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getLastUpdatedUTC() {
        return lastUpdatedUTC;
    }

    public String getLocale() {
        return locale;
    }

    public String getMarket() {
        return market;
    }

    public String getName() {
        return name;
    }

    public String getPrimaryExchange() {
        return primaryExchange;
    }

    public double getAggOpen() {
        return aggOpen;
    }

    public double getOpen() {
        return open;
    }

    public double getAggClose() {
        return aggClose;
    }

    public double getClose() {
        return close;
    }

    public double getAggHigh() {
        return aggHigh;
    }

    public double getHigh() {
        return high;
    }

    public double getAggLow() {
        return aggLow;
    }

    public double getLow() {
        return low;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public void setLastUpdatedUTC(String lastUpdatedUTC) {
        this.lastUpdatedUTC = lastUpdatedUTC;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrimaryExchange(String primaryExchange) {
        this.primaryExchange = primaryExchange;
    }

    public void setAggOpen(double aggOpen) {
        this.aggOpen = aggOpen;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public void setAggClose(double aggClose) {
        this.aggClose = aggClose;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public void setAggHigh(double aggHigh) {
        this.aggHigh = aggHigh;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public void setAggLow(double aggLow) {
        this.aggLow = aggLow;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public String getHasDividends() {
        return hasDividends;
    }

    public void setHasDividends(String hasDividends) {
        this.hasDividends = hasDividends;
    }

    public List<DividendPayment> getDividendPaymentList() {
        return dividendPaymentList;
    }

    public void setDividendPaymentList(List<DividendPayment> dividendPaymentList) {
        this.dividendPaymentList = dividendPaymentList;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        Stock s = (Stock) o;
        return this.name.equals(s.getName());
    }
    
    public double getPriceToDivRatio() {
        return priceToDivRatio;
    }

    public void setPriceToDivRatio(double priceToDivRatio) {
        this.priceToDivRatio = priceToDivRatio;
    }
    
    public String getCloseDiff() {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(aggClose - close);
    }
    
    public String getCloseDiffPercent() {
        DecimalFormat df = new DecimalFormat("0.00");
        if (aggClose == 0) {
            return "N/A";
        }
        return df.format((aggClose - close) / aggClose);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getLastDivPaymentAmount() {
        return lastDivPaymentAmount;
    }

    public void setLastDivPaymentAmount(double lastDivPaymentAmount) {
        this.lastDivPaymentAmount = lastDivPaymentAmount;
    }

    public String getLastDivPaymentDate() {
        return lastDivPaymentDate;
    }

    public void setLastDivPaymentDate(String lastDivPaymentDate) {
        this.lastDivPaymentDate = lastDivPaymentDate;
    }

    public double getAvgDivPaymentAmount() {
        return avgDivPaymentAmount;
    }

    public void setAvgDivPaymentAmount(double avgDivPaymentAmount) {
        this.avgDivPaymentAmount = avgDivPaymentAmount;
    }
}
