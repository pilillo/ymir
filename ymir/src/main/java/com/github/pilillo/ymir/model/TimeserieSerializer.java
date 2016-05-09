package com.github.pilillo.ymir.model;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public interface TimeserieSerializer {
    void serializeTimeserie(String name, SortedMap<Long, Double> timeserie);
    TreeMap<Long,Double> getTimeserieFromDatabase(String name);
    List<String> getTimeseriesNames();
}
