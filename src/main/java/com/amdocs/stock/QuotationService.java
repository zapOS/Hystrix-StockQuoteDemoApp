package com.amdocs.stock;

/**
 * Created by sapank on 3/16/2016.
 */
public interface QuotationService {

    String EXCHANGE = "NS";

    public Stock getQuote(String symbol, int delay);
}
