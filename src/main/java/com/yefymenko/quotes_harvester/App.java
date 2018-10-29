package com.yefymenko.quotes_harvester;

import com.yefymenko.quotes_harvester.endpoints.BinanceExchangeEndpoint;
import com.yefymenko.quotes_harvester.endpoints.BitfinexExchangeEndpoint;
import com.yefymenko.quotes_harvester.endpoints.ExchangeEndpoint;
import com.yefymenko.quotes_harvester.endpoints.PoloniexExchangeEndpoint;

import java.util.concurrent.*;

public class App {

    public static void main(String[] args) throws Exception {
        QuoteBuffer quoteBuffer = new QuoteBuffer(PersistentStorage.getInstance(), Settings.getInstance().getFlushPeriod());
        subscribeToServices(quoteBuffer);
        Thread.sleep(Long.MAX_VALUE);
    }

    private static void subscribeToServices(QuoteBuffer quoteBuffer) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        executorService.submit(() -> {
            ExchangeEndpoint binanceEndpoint = new BinanceExchangeEndpoint(quoteBuffer);
            binanceEndpoint.subscribe();
        });
        executorService.submit(() -> {
            ExchangeEndpoint poloniex = new PoloniexExchangeEndpoint(quoteBuffer);
            poloniex.subscribe();
        });

        executorService.submit(() -> {
            ExchangeEndpoint bitfinex = new BitfinexExchangeEndpoint(quoteBuffer);
            bitfinex.subscribe();
        });

    }
}
