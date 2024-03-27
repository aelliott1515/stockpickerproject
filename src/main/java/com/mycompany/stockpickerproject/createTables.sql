/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  alexelliott
 * Created: Mar 27, 2024
 */

DROP TABLE IF EXISTS polygonstocktickers;

DROP TABLE IF EXISTS polygondividendpayments;

DROP TABLE IF EXISTS bannedstocktickers;

CREATE TABLE if not exists polygonstocktickers (
	id INT AUTO_INCREMENT PRIMARY KEY,
	ticker VARCHAR(255),
	active TINYINT UNSIGNED,
	cik VARCHAR(255),
	currency_name VARCHAR(255),
	last_updated_utc DATE,
	locale VARCHAR(255),
	market VARCHAR(255),
	name  VARCHAR(3000),
	primary_exchange VARCHAR(255),
	type VARCHAR(255),
	agg_open DOUBLE DEFAULT 0,
	open DOUBLE DEFAULT 0,
	agg_close DOUBLE DEFAULT 0,
	close DOUBLE DEFAULT 0,
	agg_high DOUBLE DEFAULT 0,
	high DOUBLE DEFAULT 0,
	agg_low DOUBLE DEFAULT 0,
	low DOUBLE DEFAULT 0,
	note VARCHAR(3000),
	avg_div_payment_date DOUBLE DEFAULT 0,
	last_div_payment_amount DOUBLE DEFAULT 0,
	last_div_payment_date DATE,
	has_dividends VARCHAR(31)
);

CREATE TABLE if not exists polygondividendpayments (
	id INT AUTO_INCREMENT PRIMARY KEY,
	ticker VARCHAR(255),
	cash_amount DOUBLE,
	currency VARCHAR(255),
	declaration_date DATE,
	dividend_type VARCHAR(255),
	ex_dividend_date DATE,
	frequency TINYINT,
	pay_date DATE,
	record_date DATE
);

CREATE TABLE if not exists bannedstocktickers (
	id INT AUTO_INCREMENT PRIMARY KEY,
	ticker VARCHAR(255),
	reason VARCHAR(3000)
);
