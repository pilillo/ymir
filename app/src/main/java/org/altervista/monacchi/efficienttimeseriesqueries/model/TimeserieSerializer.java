package org.altervista.monacchi.efficienttimeseriesqueries.model;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by a.monacchi on 02.05.2016.
 */
public interface TimeserieSerializer {
    public void serializeTimeserie(String name, SortedMap<Long,Double> timeserie);
    public TreeMap<Long,Double> getTimeserieFromDatabase(String name);
    public List<String> getTimeseriesNames();
}
