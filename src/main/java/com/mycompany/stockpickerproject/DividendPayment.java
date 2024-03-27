/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.stockpickerproject;

/**
 *
 * @author alexelliott
 */
public class DividendPayment {

    private String ticker;
    private double cashAmount;
    private String currency;
    private String declarationDate;
    private String dividendType;
    private String exDividendDate;
    private int frequency;
    private String payDate;
    private String recordDate;
    
    public DividendPayment(String ticker, double cashAmount, String currency, String declarationDate,
            String dividendType, String exDividendDate, int frequency, String payDate, String recordDate) {
        this.ticker = ticker;
        this.cashAmount = cashAmount;
        this.currency = currency;
        this.declarationDate = declarationDate;
        this.dividendType = dividendType;
        this.exDividendDate = exDividendDate;
        this.frequency = frequency;
        this.payDate = payDate;
        this.recordDate = recordDate;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDeclarationDate() {
        return declarationDate;
    }

    public void setDeclarationDate(String declarationDate) {
        this.declarationDate = declarationDate;
    }

    public String getDividendType() {
        return dividendType;
    }

    public void setDividendType(String dividendType) {
        this.dividendType = dividendType;
    }

    public String getExDividendDate() {
        return exDividendDate;
    }

    public void setExDividendDate(String exDividendDate) {
        this.exDividendDate = exDividendDate;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
}
