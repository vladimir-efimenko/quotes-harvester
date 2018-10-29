package com.yefymenko.quotes_harvester.endpoints;

import com.yefymenko.quotes_harvester.*;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import io.reactivex.Observable;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.slf4j.*;


public abstract class ExchangeEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeEndpoint.class);
    private final QuoteBuffer quoteBuffer;

    protected ExchangeEndpoint(QuoteBuffer quoteBuffer) {
        this.quoteBuffer = quoteBuffer;
    }

    public void subscribe() {
        StreamingExchange exchange = getExchange();
        ProductSubscription.ProductSubscriptionBuilder productSubscriptionBuilder = ProductSubscription.create();

        for (Instrument instrument : Settings.getInstance().getInstruments()) {
            if (instrument instanceof SyntheticInstrument)
                continue;
            productSubscriptionBuilder.addTicker(instrument.getCurrencyPair());
            LOGGER.trace("{} added ticker for pair: {}", getExchangeName(), instrument.getCurrencyPair());
        }

        exchange.connect(productSubscriptionBuilder.build()).blockingAwait();

        if (!exchange.isAlive()) {
            LOGGER.warn("{} StreamingExchange is not alive. Will not proceed.", getExchangeName());
            return;
        }

        LOGGER.info("Connected to {}", getExchange());

        for (Instrument instrument : Settings.getInstance().getInstruments()) {
            if (instrument instanceof SyntheticInstrument)
                continue;

            Observable<Ticker> ticker = exchange.getStreamingMarketDataService()
                    .getTicker(instrument.getCurrencyPair());

            if (ticker != null) {
                ticker.subscribe(t -> {
                    quoteBuffer.add(new Quote(t.getBid(), t.getAsk(), instrument, getExchangeName(), t.getTimestamp()));
                    LOGGER.info("{} New ticker: {}", getExchangeName(), t);
                });
            } else {
                LOGGER.error("Cannot retrieve a ticker for currency pair: {}", instrument.getCurrencyPair());
            }
        }
    }

    protected abstract StreamingExchange getExchange();

    protected abstract String getExchangeName();
}
