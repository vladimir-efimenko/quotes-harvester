package com.yefymenko.quotes_harvester;

import java.util.*;

public class SyntheticInstrument extends Instrument {

    private Collection<Instrument> dependencyList;

    public SyntheticInstrument(Instrument i) {
        super(i.getBase(), i.getCounter());
        setName(i.getName());
        dependencyList = new ArrayList<>();
    }

    public boolean addDependency(Instrument instrument) {
        return dependencyList.contains(instrument) ? false : dependencyList.add(instrument);
    }
}
