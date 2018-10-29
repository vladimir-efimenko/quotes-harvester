package com.yefymenko.quotes_harvester;

import java.math.BigDecimal;
import java.time.*;
import java.util.Date;

public class Quote {
    private final BigDecimal bid;
    private final BigDecimal ask;
    private final Instrument instrument;
    private final LocalDateTime time;
    private final String exchange;
    private final Date timestamp;

    public Quote(BigDecimal bid, BigDecimal ask, Instrument instrument, String exchange, Date timestamp) {
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument cannot be null");
        }
        if (exchange == null) {
            throw new IllegalArgumentException("Exchange cannot be null");
        }
        this.bid = bid;
        this.ask = ask;
        this.instrument = instrument;
        this.exchange = exchange;
        this.timestamp = timestamp;
        this.time = this.timestamp != null
                ? LocalDateTime.ofInstant(this.timestamp.toInstant(), ZoneId.systemDefault())
                : LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
    }

    public BigDecimal getBid() {
        return bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public String getName() {
        return instrument.getName();
    }

    public String getExchange() {
        return exchange;
    }

    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public int hashCode() {
        return getInstrument().hashCode() * 31 + getExchange().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Quote) {
            Quote other = (Quote) obj;
            return instrument.equals(other.instrument) && exchange.equals(other.exchange);
        }
        return false;
    }
}
