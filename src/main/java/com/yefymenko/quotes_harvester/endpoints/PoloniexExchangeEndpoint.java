package com.yefymenko.quotes_harvester.endpoints;

import com.yefymenko.quotes_harvester.QuoteBuffer;
import info.bitrich.xchangestream.core.*;
import info.bitrich.xchangestream.poloniex.PoloniexStreamingExchange;

public class PoloniexExchangeEndpoint extends ExchangeEndpoint {

    public PoloniexExchangeEndpoint(QuoteBuffer quoteBuffer) {
        super(quoteBuffer);
    }

    @Override
    protected StreamingExchange getExchange() {
        return StreamingExchangeFactory.INSTANCE.createExchange(PoloniexStreamingExchange.class.getName());
    }

    @Override
    protected String getExchangeName() {
        return "Poloniex";
    }
}
