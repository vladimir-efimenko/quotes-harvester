package com.yefymenko.quotes_harvester.endpoints;

import com.yefymenko.quotes_harvester.QuoteBuffer;
import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import info.bitrich.xchangestream.core.*;

public class BinanceExchangeEndpoint extends ExchangeEndpoint {

    public BinanceExchangeEndpoint(QuoteBuffer quoteBuffer) {
        super(quoteBuffer);
    }

    @Override
    protected StreamingExchange getExchange() {
        return StreamingExchangeFactory.INSTANCE.createExchange(BinanceStreamingExchange.class.getName());
    }

    @Override
    protected String getExchangeName() {
        return "Binance";
    }
}
