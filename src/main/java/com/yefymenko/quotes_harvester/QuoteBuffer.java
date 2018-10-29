package com.yefymenko.quotes_harvester;

import java.util.*;
import java.util.concurrent.*;

public class QuoteBuffer {
    private final HashSet<Quote> quotes = new HashSet<>();
    private final QuoteWriter writer;

    public QuoteBuffer(QuoteWriter writer, int flushPeriod) {
        this.writer = writer;
        Executors.newScheduledThreadPool(4)
                .scheduleWithFixedDelay(() -> {
                    flush();
                }, flushPeriod, flushPeriod, TimeUnit.SECONDS);
    }

    public void add(Quote quote) {
        synchronized (quotes) {
            quotes.remove(quote);
            quotes.add(quote);
        }
    }

    private void flush() {
        Collection<Quote> quotesToWrite;

        synchronized (quotes) {
            quotesToWrite = new ArrayList<>(quotes);
            quotes.clear();
        }

        if (!quotesToWrite.isEmpty()) {
            writer.writeQuotes(quotesToWrite);
        }
    }
}
