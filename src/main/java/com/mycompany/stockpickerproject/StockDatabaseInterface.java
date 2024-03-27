/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.stockpickerproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author alexelliott
 */
public class StockDatabaseInterface {
    
    private final String databaseURL = "jdbc:mysql://localhost:3306/stocks?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private final String appuser = "appuser";
    private final String apppassword = "apppassword";
    
    public StockDatabaseInterface() throws SQLException, ClassNotFoundException {
        
    }
    
    public void insertTickers(List<Stock> results)  {
        for (Stock stock : results) {
            //restrict stock list
            this.insertTicker(stock.getTicker(), stock.getType(), stock.getActive(),
                stock.getCik(), stock.getCurrencyName(), stock.getLastUpdatedUTC(),
                stock.getLocale(), stock.getMarket(), stock.getName(),
                stock.getPrimaryExchange());
        }
        
    }
    
    public void clearTickerTable() {
        Connection con = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL,appuser,apppassword);
            String query = "DELETE FROM polygonstocktickers";
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private Date getDateFromString(String s) throws ParseException {
        if (s == null) {
            return null;
        }
        Calendar lastUpdatedCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        lastUpdatedCalendar.setTime(sdf.parse(s));
        return new Date(lastUpdatedCalendar.getTime().getTime());
    }
    
    private void insertTicker(String ticker, String type, Boolean active, String cik, String currency_name, String last_updated_utc, String locale, String market, String name, String primary_exchange) {
        String query = "INSERT INTO polygonstocktickers " +
                    "(ticker,   type,   active, cik,    currency_name,  last_updated_utc,   locale, market, name,   primary_exchange) values" +
                    "(?,        ?,      ?,      ?,      ?,              ?,                  ?,      ?,      ?,      ?)";
        Connection con = null;
        PreparedStatement preparedStmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL,appuser,apppassword);
            
            Date lastUpdatedDate = getDateFromString(last_updated_utc);
        
            preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, ticker);
            preparedStmt.setString (2, type);
            preparedStmt.setBoolean(3, active);
            preparedStmt.setString (4, cik);
            preparedStmt.setString (5, currency_name);
            preparedStmt.setDate (6, lastUpdatedDate);
            preparedStmt.setString (7, locale);
            preparedStmt.setString (8, market);
            preparedStmt.setString(9, name);
            preparedStmt.setString(10, primary_exchange);

            preparedStmt.execute();
        } catch (SQLException | ParseException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                preparedStmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /*public List<Stock> getStockList() {
        Connection con = null;
        List<Stock> output = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ResultSet divRs =null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL,appuser,apppassword);
            String query = "SELECT ticker, active, cik, currency_name, last_updated_utc, locale, market, name, primary_exchange, type, "
                    + " open, close, high, low, agg_open, agg_close, agg_high, agg_low, has_dividends "
                    + "FROM polygonstocktickers ORDER BY ticker";
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Stock stock = getStockFromResultSet(rs);
                output.add(stock);
                //get dividend payments for this stock
                String divQuery = "SELECT cash_amount, currency, declaration_date, dividend_type, ex_dividend_date, frequency, pay_date, "
                        + "record_date "
                        + "FROM polygondividendpayments "
                        + "WHERE ticker=?";
                pstmt = con.prepareCall(divQuery);
                pstmt.setString(1, stock.getTicker());
                divRs = pstmt.executeQuery();
                List<DividendPayment> divList = new ArrayList<>();
                while (divRs.next()) {
                    DividendPayment dividendPayment = getDividendPaymentFromResultSet(stock.getTicker(), divRs);
                    divList.add(dividendPayment);
                }
                stock.setDividendPaymentList(divList);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (divRs != null) {
                try {
                    divRs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return output;
    }*/
    
    public List<Stock> getStockList(boolean getDividends, boolean limitResults) {
        Connection con = null;
        List<Stock> output = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ResultSet divRs =null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL,appuser,apppassword);
            var query = "SELECT ticker, active, cik, currency_name, last_updated_utc, locale, market, name, primary_exchange, type, "
                + " open, close, high, low, agg_open, agg_close, agg_high, agg_low, has_dividends "
                + "FROM polygonstocktickers "
                + "ORDER BY ticker";
            if (limitResults) {
                query += " LIMIT 100";
            }
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Stock stock = getStockFromResultSet(rs);
                output.add(stock);
                if (getDividends) {
                    //get dividend payments for this stock
                    String divQuery = "SELECT cash_amount, currency, declaration_date, dividend_type, ex_dividend_date, frequency, pay_date, "
                            + "record_date "
                            + "FROM polygondividendpayments "
                            + "WHERE ticker=?";
                    pstmt = con.prepareCall(divQuery);
                    pstmt.setString(1, stock.getTicker());
                    divRs = pstmt.executeQuery();
                    List<DividendPayment> divList = new ArrayList<>();
                    while (divRs.next()) {
                        DividendPayment dividendPayment = getDividendPaymentFromResultSet(stock.getTicker(), divRs);
                        divList.add(dividendPayment);
                    }
                    stock.setDividendPaymentList(divList);
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (divRs != null) {
                try {
                    divRs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return output;
    }
    
    private Stock getStockFromResultSet(ResultSet rs) throws SQLException {
        List<String> columnNames = getColumnNameListFromResultSet(rs);
        var tickerIndex = columnNames.indexOf("ticker");
        String ticker = rs.getString(tickerIndex + 1);
        
        var activeIndex = columnNames.indexOf("active");
        Boolean active = rs.getBoolean(activeIndex + 1);
        
        var cikIndex = columnNames.indexOf("cik");
        String cik = rs.getString(cikIndex + 1);
        
        var currencyNameIndex = columnNames.indexOf("currency_name");
        String currencyName = rs.getString(currencyNameIndex + 1);
        
        var lastUpdatedUTCIndex = columnNames.indexOf("last_updated_utc");
        Date lastUpdatedUTC = rs.getDate(lastUpdatedUTCIndex + 1);
        Calendar c = (Calendar)Calendar.getInstance();
        c.setTimeInMillis(lastUpdatedUTC.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastUpdatedUTCString = sdf.format(c.getTime());
        
        var localeIndex = columnNames.indexOf("locale");
        String locale = rs.getString(localeIndex + 1);
        
        var marketIndex = columnNames.indexOf("market");
        String market = rs.getString(marketIndex + 1);
        
        var nameIndex = columnNames.indexOf("name");
        String name = rs.getString(nameIndex + 1);
        
        var primaryExchangeIndex = columnNames.indexOf("primary_exchange");
        String primaryExchange = rs.getString(primaryExchangeIndex + 1);
        
        var typeIndex = columnNames.indexOf("type");
        String type = rs.getString(typeIndex + 1);
        
        var openIndex = columnNames.indexOf("open");
        Double open = rs.getDouble(openIndex + 1);
        
        var aggOpenIndex = columnNames.indexOf("agg_open");
        Double aggOpen = 0.0;
        if (aggOpenIndex > 0) {
            aggOpen = rs.getDouble(aggOpenIndex + 1);
        }
        
        var closeIndex = columnNames.indexOf("close");
        Double close = rs.getDouble(closeIndex + 1);
        
        var aggCloseIndex = columnNames.indexOf("agg_close");
        Double aggClose = 0.0;
        if (aggCloseIndex > 0) {
            aggClose = rs.getDouble(aggCloseIndex + 1);
        }
        
        var highIndex = columnNames.indexOf("high");
        Double high = rs.getDouble(highIndex);
        
        var aggHighIndex = columnNames.indexOf("agg_high");
        Double aggHigh = 0.0;
        if (aggHighIndex > 0) {
            aggHigh = rs.getDouble(aggHighIndex + 1);
        }
        
        var lowIndex = columnNames.indexOf("low");
        Double low = rs.getDouble(lowIndex);
        
        var aggLowIndex = columnNames.indexOf("agg_low");
        Double aggLow = 0.0;
        if (aggLowIndex > 0) {
            aggLow = rs.getDouble(aggLowIndex + 1);
        }
        
        var noteIndex = columnNames.indexOf("note");
        String note = "";
        if (noteIndex > 0) {
            note = rs.getString("note");
        }
        
        var avgDivPaymentAmountIndex = columnNames.indexOf("avg_div_payment_amount");
        Double avgDivPaymentAmount = 0.0;
        if (avgDivPaymentAmountIndex > 0) {
            avgDivPaymentAmount = rs.getDouble("avg_div_payment_amount");
        }
        
        var lastDivPaymentAmountIndex = columnNames.indexOf("last_div_payment_amount");
        Double lastDivPaymentAmount = 0.0;
        if (lastDivPaymentAmountIndex > 0) {
            lastDivPaymentAmount = rs.getDouble("last_div_payment_amount");
        }
        
        var lastDivPaymentDateIndex = columnNames.indexOf("last_div_payment_date");
        String lastDivPaymentDate = "";
        if (lastDivPaymentDateIndex > 0) {
            lastDivPaymentDate = rs.getString("last_div_payment_date");
        }
        
        var hasDividendsIndex = columnNames.indexOf("has_dividends");
        int hasDividends = rs.getInt(hasDividendsIndex);
        
        Stock stock = new Stock(ticker, type, active, cik, currencyName, lastUpdatedUTCString, locale, market, name,
                primaryExchange, open, close, high, low, hasDividends == 0 ? "Y" : "N");
        stock.setAggOpen(aggOpen);
        stock.setAggClose(aggClose);
        stock.setAggHigh(aggHigh);
        stock.setAggLow(aggLow);
        stock.setNote(note);
        stock.setAvgDivPaymentAmount(avgDivPaymentAmount);
        stock.setLastDivPaymentAmount(lastDivPaymentAmount);
        stock.setLastDivPaymentDate(lastDivPaymentDate);
        
        var priceToDivRatioIndex = columnNames.indexOf("pricetodivratio");
        if (priceToDivRatioIndex >= 0) {
            stock.setPriceToDivRatio(rs.getDouble(priceToDivRatioIndex + 1));
        }

        return stock;
    }
    
    private DividendPayment getDividendPaymentFromResultSet(String ticker, ResultSet rs) throws SQLException {
        double cashAmount = rs.getDouble("cash_amount");
        String currency = rs.getString("currency");
        String declarationDate = rs.getString("declaration_date");
        String dividendType = rs.getString("dividend_type");
        String exDividendDate = rs.getString("ex_dividend_date");
        int frequency = rs.getInt("frequency");
        String payDate = rs.getString("pay_date");
        String recordDate = rs.getString("record_date");
        return new DividendPayment(ticker, cashAmount, currency, declarationDate, dividendType, exDividendDate, frequency,
                payDate, recordDate);
    }
    
    /**
     * Returns a stock object for the given ticker.
     * @param ticker
     * 
     * @return 
     */
    public Stock getStock(String ticker) {
        Connection con = null;
        Stock output = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL,appuser,apppassword);
            String query = "SELECT ticker, active, cik, currency_name, last_updated_utc, locale, market, name, primary_exchange, type, " +
                    "open, close, high, low, has_dividends, last_retrieved_open_value FROM polygonstocktickers WHERE ticker = ? LIMIT 1";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, ticker);
            
            ResultSet rs = preparedStmt.executeQuery();
            if (rs.next()) {
                output = getStockFromResultSet(rs);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return output;
    }

    /**
     * Updates a record in polygonstocktickers table.
     * @param st
     * @return number of updated rows
     * @throws java.text.ParseException
     */
    public int updateStock(Stock st) throws ParseException {
        Connection con = null;
        PreparedStatement pstmt = null;
        int output = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL,appuser,apppassword);
            String query = "UPDATE polygonstocktickers SET active=?, cik=?, currency_name=?, last_updated_utc=?, locale=?, "
                    + "market=?, name=?, primary_exchange=?, type=?, open=?, close=?, high=?, low=?, agg_open=?, "
                    + "agg_close=?, agg_high=?, agg_low=?, has_dividends=? " +
                    "WHERE ticker=?";
            
            Date lastUpdatedDate = getDateFromString(st.getLastUpdatedUTC());
            
            pstmt = con.prepareStatement(query);
            pstmt.setBoolean(1, st.getActive());//active=?
            pstmt.setString(2, st.getCik());//cik=?
            pstmt.setString(3, st.getCurrencyName());//currency_name=?
            pstmt.setDate(4, lastUpdatedDate);//last_updated_utc=?
            pstmt.setString(5, st.getLocale());//locale=?
            pstmt.setString(6, st.getMarket());//market=?
            pstmt.setString(7, st.getName());//name=?
            pstmt.setString(8, st.getPrimaryExchange());//primary_exchange=?
            pstmt.setString(9, st.getType());//type=?
            pstmt.setDouble(10, st.getOpen());//open=?
            pstmt.setDouble(11, st.getClose());//close=?
            pstmt.setDouble(12, st.getHigh());//high=?
            pstmt.setDouble(13, st.getLow());//low=?
            pstmt.setDouble(14, st.getAggOpen());//agg_open=?
            pstmt.setDouble(15, st.getAggClose());//agg_close=?
            pstmt.setDouble(16, st.getAggHigh());//agg_high=?
            pstmt.setDouble(17, st.getAggLow());//agg_low=?
            pstmt.setString(18, st.getHasDividends());//has_dividends=?
            pstmt.setString(19, st.getTicker());//ticker=?
            
            output = pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return output;
    }

    public int updateHasDividendsFieldsForStockTickers(List<String> tickerList) {
        Connection con = null;
        int output = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL,appuser,apppassword);
            var query = String.format("UPDATE polygonstocktickers SET has_dividends=1 WHERE ticker in (%s)",
                             tickerList.stream()
                             .map(v -> "?")
                             .collect(Collectors.joining(", ")));
            PreparedStatement preparedStmt = con.prepareStatement(query);
            for (int ctr = 0; ctr < tickerList.size(); ctr++) {
                preparedStmt.setString(ctr + 1, tickerList.get(ctr));
            }
            output = preparedStmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return output;
    }

    public int insertDividendPayments(List<DividendPayment> dividendList) {
        Connection con = null;
        int output = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL,appuser,apppassword);
            String query = "INSERT INTO polygondividendpayments (ticker, cash_amount, currency, declaration_date, dividend_type, " +
                    "ex_dividend_date, frequency, pay_date, record_date) " +
                "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ? FROM DUAL " +
                "WHERE NOT EXISTS (SELECT * FROM polygondividendpayments " +
                "      WHERE ticker=? AND DATE_FORMAT(ex_dividend_date, '%Y-%m-%d')=? LIMIT 1) " +
                "AND EXISTS (SELECT * FROM polygonstocktickers " +
                "      WHERE ticker=?)";
            for (DividendPayment dp : dividendList) {
                Date declarationDate = getDateFromString(dp.getDeclarationDate());
                Date payDate = getDateFromString(dp.getExDividendDate());
                Date recordDate = getDateFromString(dp.getExDividendDate());
                Date exDividendDate = getDateFromString(dp.getExDividendDate());
                
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.setString(1, dp.getTicker());
                preparedStmt.setDouble(2, dp.getCashAmount());
                preparedStmt.setString(3, dp.getCurrency());
                preparedStmt.setDate(4, declarationDate);
                preparedStmt.setString(5, dp.getDividendType());
                preparedStmt.setDate(6, exDividendDate);
                preparedStmt.setInt(7, dp.getFrequency());
                preparedStmt.setDate(8, payDate);
                preparedStmt.setDate(9, recordDate);
                preparedStmt.setString(10, dp.getTicker());
                preparedStmt.setString(11, dp.getExDividendDate());
                preparedStmt.setString(12, dp.getTicker());
                
                output += preparedStmt.executeUpdate();
            }
        } catch (SQLException | ClassNotFoundException | ParseException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return output;
    }
    
    public void clearDividendPaymentTable() {
        Connection con = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL,appuser,apppassword);
            String query = "DELETE FROM polygondividendpayments";
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public List<Stock> getSmartStockList() {
        List<Stock> output = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL, appuser, apppassword);
            String query = "SELECT t.ticker, active, cik, currency_name, last_updated_utc, locale, market, name, "
                    + " primary_exchange, type, open, close, high, low, agg_open, agg_close, agg_high, agg_low, "
                    + " has_dividends, note, avg_div_payment_amount, last_div_payment_amount, "
                    + " last_div_payment_date, sum(p.cash_amount) / t.close as pricetodivratio "
                    + "FROM polygonstocktickers t "
                    + " INNER JOIN polygondividendpayments p "
                    + "WHERE t.ticker=p.ticker AND p.pay_date>=DATE_SUB(CURDATE(), INTERVAL 5 YEAR) AND EXISTS( "
                    + " SELECT * FROM polygondividendpayments p2 WHERE p.ticker=p2.ticker AND ( "
                    + "     (p2.frequency=1 AND p2.pay_date > DATE_SUB(CURDATE(), INTERVAL 408 DAY)) OR "
                    + "     (p2.frequency=2 AND p2.pay_date > DATE_SUB(CURDATE(), INTERVAL 225 DAY)) OR "
                    + "     (p2.frequency=4 AND p2.pay_date > DATE_SUB(CURDATE(), INTERVAL 134 DAY)) OR "
                    + "     (p2.frequency=12 AND p2.pay_date > DATE_SUB(CURDATE(), INTERVAL 73 DAY)) "
                    + " ) "
                    + ") "
                    + "GROUP BY t.ticker "
                    + "ORDER BY pricetodivratio DESC";
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Stock s = getStockFromResultSet(rs);
                output.add(s);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return output;
    }
    
    private List<String> getColumnNameListFromResultSet(ResultSet rs) throws SQLException {
        List<String> output = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int x = 1; x <= rsmd.getColumnCount(); x++) {
            output.add(rsmd.getColumnName(x));
        }
        return output;
    }

    public void setLastPaymentValues(Stock st) {
        System.out.println("alexmark st.getTicker():" + st.getTicker());
        Connection con = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        PreparedStatement pstmt2 = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(databaseURL, appuser, apppassword);
            
            String query1 = "SELECT DATE_FORMAT(MAX(ex_dividend_date), '%Y-%m-%d') AS date FROM polygondividendpayments WHERE ticker=?";
            pstmt1 = con.prepareStatement(query1);
            pstmt1.setString(1, st.getTicker());
            rs = pstmt1.executeQuery();
            
            if (rs.next()) {
                String date = rs.getString("date");
                String query2 = "UPDATE polygonstocktickers t SET avg_div_payment_amount=( "
                    + " SELECT AVG(cash_amount) "
                    + " FROM polygondividendpayments "
                    + " WHERE ticker=t.ticker "
                    + "), last_div_payment_amount=( "
                    + " SELECT cash_amount "
                    + " FROM polygondividendpayments "
                    + " WHERE DATE_FORMAT(ex_dividend_date, '%Y-%m-%d')=? AND ticker=t.ticker "
                    + "), last_div_payment_date=( "
                    + " SELECT ex_dividend_date "
                    + " FROM polygondividendpayments "
                    + " WHERE DATE_FORMAT(ex_dividend_date, '%Y-%m-%d')=? AND ticker=t.ticker "
                    + ") WHERE t.ticker=? ";
                System.out.println("alexmark query2:" + query2);
                pstmt2 = con.prepareStatement(query2);
                pstmt2.setString(1, date);
                pstmt2.setString(2, date);
                pstmt2.setString(3, st.getTicker());
                pstmt2.executeUpdate();
            }
            
            
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (pstmt1 != null) {
                try {
                    pstmt1.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (pstmt2 != null) {
                try {
                    pstmt2.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StockDatabaseInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
