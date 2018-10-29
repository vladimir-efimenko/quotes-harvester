package com.yefymenko.quotes_harvester;

import java.util.Collection;

public interface QuoteWriter {

    void writeQuotes(Collection<Quote> quotes);
}
