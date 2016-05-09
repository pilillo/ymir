package com.github.pilillo.ymir.model;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public interface TimeserieSerializer {
    public void serializeTimeserie(String name, SortedMap<Long,Double> timeserie);
    public TreeMap<Long,Double> getTimeserieFromDatabase(String name);
    public List<String> getTimeseriesNames();
}
