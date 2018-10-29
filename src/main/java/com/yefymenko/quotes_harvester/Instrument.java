package com.yefymenko.quotes_harvester;

import org.knowm.xchange.currency.*;

public class Instrument {
    private final Currency base;
    private final Currency counter;
    private String name;

    public Instrument(Currency base, Currency counter) {
        if (base == null) {
            throw new NullPointerException("Base currency cannot be null");
        }
        if (counter == null) {
            throw new NullPointerException("Counter currency cannot be null");
        }
        this.base = base;
        this.counter = counter;
    }

    public Currency getBase() {
        return base;
    }

    public Currency getCounter() {
        return counter;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public CurrencyPair getCurrencyPair() {
        return new CurrencyPair(base.toString(), counter.toString());
    }

    @Override
    public int hashCode() {
        return base.hashCode() * 31 + counter.hashCode();
    }

    @Override
    public String toString() {
        return base.toString() + "/" + counter.toString();
    }

    /**
     * Parses the specified string value in format 'CUR1/CUR2'
     */
    public static Instrument parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (value.indexOf("/") == -1) {
            throw new IllegalArgumentException("Value must be in correct format 'CUR1/CUR2'");
        }
        String[] currencies = value.split("/");
        if (currencies.length < 2) {
            throw new IllegalArgumentException("Value must be in correct format 'CUR1/CUR2'");
        }
        return new Instrument(new Currency(currencies[0]), new Currency(currencies[1]));
    }
}
