package com.yefymenko.quotes_harvester.endpoints;

import com.yefymenko.quotes_harvester.QuoteBuffer;
import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.core.*;

public class BitfinexExchangeEndpoint extends ExchangeEndpoint {

    public BitfinexExchangeEndpoint(QuoteBuffer quoteBuffer) {
        super(quoteBuffer);
    }

    @Override
    protected StreamingExchange getExchange() {
        return StreamingExchangeFactory.INSTANCE.createExchange(BitfinexStreamingExchange.class.getName());
    }

    @Override
    protected String getExchangeName() {
        return "Bitfinex";
    }
}
