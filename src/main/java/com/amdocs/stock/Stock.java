package com.amdocs.stock;

import com.amdocs.Fields;
import com.amdocs.YahooStockQuote;

/**
 * Created by sapank on 3/16/2016.
 */
public class Stock {

    String name;
    String symbol;
    double currentPrice;
    double high52Week;
    double low52Week;
    double highDaily;
    double lowDaily;
    String quoteProvider;

    public static Stock fromYahoo(YahooStockQuote yahooStockQuote) {
        if (yahooStockQuote != null) {
            Stock stock = new Stock();
            if (yahooStockQuote.getList() != null && yahooStockQuote.getList().getResources().length > 0) {
                Fields fields = yahooStockQuote.getList().getResources()[0].getResource().getFields();
                double price = Double.parseDouble(fields.getPrice());
                stock.setCurrentPrice(price);
                stock.setSymbol(fields.getSymbol());
                stock.setName(fields.getName());
                stock.setLowDaily(Double.parseDouble(fields.getDay_low()));
                stock.setHighDaily(Double.parseDouble(fields.getDay_high()));
                stock.setHigh52Week(Double.parseDouble(fields.getYear_high()));
                stock.setLow52Week(Double.parseDouble(fields.getYear_low()));
                stock.setQuoteProvider("YAHOO");
                return stock;
            }
            return null;
        } else
            return null;
    }

    public static Stock fromGoogle(GoogleQuote quote) {
        Stock stock = new Stock();
        if (quote != null) {

            stock.setCurrentPrice(Double.parseDouble(quote.getL_fix()));
            stock.setSymbol(quote.getT());
            stock.setQuoteProvider("Google");
        }
        return stock;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stock{");
        sb.append("name='").append(name).append('\'');
        sb.append(", symbol='").append(symbol).append('\'');
        sb.append(", currentPrice=").append(currentPrice);
        sb.append(", high52Week=").append(high52Week);
        sb.append(", low52Week=").append(low52Week);
        sb.append(", highDaily=").append(highDaily);
        sb.append(", lowDaily=").append(lowDaily);
        sb.append(", quoteProvider='").append(quoteProvider).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getQuoteProvider() {
        return quoteProvider;
    }

    public void setQuoteProvider(String quoteProvider) {
        this.quoteProvider = quoteProvider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getHigh52Week() {
        return high52Week;
    }

    public void setHigh52Week(double high52Week) {
        this.high52Week = high52Week;
    }

    public double getLow52Week() {
        return low52Week;
    }

    public void setLow52Week(double low52Week) {
        this.low52Week = low52Week;
    }

    public double getHighDaily() {
        return highDaily;
    }

    public void setHighDaily(double highDaily) {
        this.highDaily = highDaily;
    }

    public double getLowDaily() {
        return lowDaily;
    }

    public void setLowDaily(double lowDaily) {
        this.lowDaily = lowDaily;
    }
}
