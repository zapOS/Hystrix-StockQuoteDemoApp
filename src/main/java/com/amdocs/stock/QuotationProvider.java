package com.amdocs.stock;

import com.amdocs.YahooStockQuote;
import com.google.gson.Gson;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.apache.log4j.Logger;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class QuotationProvider implements QuotationService {
    private static final Logger logger = Logger.getLogger(QuotationProvider.class.getName());
    private static QuotationProvider instance;
    private int successCount = 0;
    private int fallbackCount = 0;
    private int shortCircuitedCount = 0;

    private QuotationProvider() {

    }

    public static synchronized QuotationProvider getInstance() {
        if (instance == null) {
            instance = new QuotationProvider();
        }
        return instance;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFallbackCount() {
        return fallbackCount;
    }

    public void setFallbackCount(int fallbackCount) {
        this.fallbackCount = fallbackCount;
    }

    public int getShortCircuitedCount() {
        return shortCircuitedCount;
    }

    public void setShortCircuitedCount(int shortCircuitedCount) {
        this.shortCircuitedCount = shortCircuitedCount;
    }

    public void resetCounts() {
        fallbackCount = 0;
        successCount = 0;
        shortCircuitedCount = 0;
    }

    @Override
    public Stock getQuote(String symbol, int delay) {
        QuoteHystrixCommand quoteHystrixCommand = new QuoteHystrixCommand(symbol, delay);
        Stock stock = quoteHystrixCommand.execute();
        if (quoteHystrixCommand.isResponseFromFallback()) {
            fallbackCount++;
        } else {
            successCount++;
        }

        if (quoteHystrixCommand.isResponseShortCircuited()) {
            shortCircuitedCount++;
        }
        return stock;
    }


    class QuoteHystrixCommand extends HystrixCommand<Stock> {
        private final String symbol;
        WebTarget webTarget;
        private String baseUri = "http://finance.yahoo.com/webservice/v1/symbols/";
        private int delay = 0;


        public QuoteHystrixCommand(String symbol, int delay) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Quote"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("YahooQuote"))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                            .withExecutionTimeoutInMilliseconds(1500)));
            this.symbol = symbol;
            this.delay = delay;

            webTarget = ClientBuilder.newClient().target(baseUri);
        }

        @Override
        protected Stock run() throws Exception {
            try {
                logger.info("Hystrix command key ::" + this.getCommandKey().name());

                if (delay == 666 || delay == 999) {

                    throw new RuntimeException("failure trigger");
                }

                if (delay > 0) {
                    Thread.sleep(delay);
                }

                Response response = webTarget
                        .path(symbol + "." + QuotationService.EXCHANGE)
                        .path("quote")
                        .queryParam("format", "json")
                        .queryParam("view", "detail")
                        .request(MediaType.APPLICATION_JSON)
                        .get();


                if (response == null) {
                    throw new RuntimeException("No response for yahooo quote");
                } else {
                    String json = response.readEntity(String.class);
                    YahooStockQuote quote = new Gson().fromJson(json, YahooStockQuote.class);
                    return Stock.fromYahoo(quote);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("caught exception " + e);
                throw e;
            }
        }

        @Override
        protected Stock getFallback() {
            return new QuoteFallbackHystrixCommand(symbol, delay).execute();
        }
    }

    class QuoteFallbackHystrixCommand extends HystrixCommand<Stock> {
        private final String symbol;
        WebTarget webTarget;
        private String baseUri = "http://finance.google.com/finance/info";
        private int delay = 0;

        public QuoteFallbackHystrixCommand(String symbol, int delay) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Quote"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("GoogleQuote"))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                            .withExecutionTimeoutInMilliseconds(1500)));
            this.symbol = symbol;
            this.delay = delay;

            webTarget = ClientBuilder.newClient().target(baseUri);
        }

        @Override
        protected Stock run() throws Exception {
            try {
                logger.info("Hystrix command key ::" + this.getCommandKey().name());
                if (delay == 999) {

                    throw new RuntimeException("failure trigger");
                }

                Response response = webTarget
                        .queryParam("client", "ig")
                        .queryParam("q", "NSE:" + symbol)
                        .request(MediaType.APPLICATION_JSON)
                        .get();

                if (response == null) {
                    throw new RuntimeException("No response for google quote");
                } else {
                    String json = response.readEntity(String.class);
                    String replacedJson = json.replaceAll("// ", "").replaceAll("\\]", "").replaceAll("\\[", "");
                    GoogleQuote quote = new Gson().fromJson(replacedJson, GoogleQuote.class);
                    return Stock.fromGoogle(quote);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("caught exception " + e);
                throw e;
            }
        }
    }
}
