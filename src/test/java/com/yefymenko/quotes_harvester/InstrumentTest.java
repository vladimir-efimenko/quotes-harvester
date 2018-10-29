package com.yefymenko.quotes_harvester;

import static org.junit.Assert.*;

import org.junit.*;
import org.knowm.xchange.currency.Currency;

public class InstrumentTest {

    Instrument instrument;

    @Before
    public void Setup() {
        instrument = new Instrument(new Currency("USD"), new Currency("BTC"));
    }

    @Test
    public void instrumentBaseCurrencyCorrect() {
        assertEquals(new Currency("USD"), instrument.getBase());
    }

    @Test
    public void instrumentCounterCurrencyCorrect() {
        assertEquals(new Currency("BTC"), instrument.getCounter());
    }

    @Test
    public void instrumentToStringCorrect() {
        assertEquals("USD/BTC", instrument.toString());
    }

    @Test
    public void instrumentParseValidPair() {
        Instrument i = Instrument.parse("BTC/ETH");

        assertNotNull(i);
        assertEquals(new Currency("btc"), i.getBase());
        assertEquals(new Currency("eth"), i.getCounter());
    }
}
