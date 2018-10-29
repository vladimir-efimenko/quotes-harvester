package com.yefymenko.quotes_harvester;

import org.junit.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.*;

public class QuoteBufferTest {
    private final int flushPeriod = 1;
    private QuoteWriter quoteWriter;
    private QuoteBuffer quoteBuffer;

    @Before
    public void setUp() {
        quoteWriter = Mockito.mock(QuoteWriter.class);
        quoteBuffer = new QuoteBuffer(quoteWriter, flushPeriod);
    }

    @Test
    public void flushAfterFlushPeriodEnded() throws Exception {
        quoteBuffer.add(getTestQuote());
        Thread.sleep(flushPeriod + 1000);
        Mockito.verify(quoteWriter).writeQuotes(Mockito.anyListOf(Quote.class));
    }

    @Test
    public void willNotFlushWhenQuoteLengthZero() throws Exception {
        Thread.sleep(flushPeriod + 100);
        Mockito.verifyZeroInteractions(quoteWriter);
    }

    @Test
    public void willNotFlushBeforeFlushPeriodEnded() throws Exception {
        quoteBuffer.add(getTestQuote());
        Thread.sleep(10);
        Mockito.verifyZeroInteractions(quoteWriter);
    }

    private Quote getTestQuote() {
        return new Quote(BigDecimal.ONE, BigDecimal.ONE, Instrument.parse("B/U"), "Exc", new Date());
    }
}
