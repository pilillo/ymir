package monacchi.altervista.org.ymir.timeseries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import monacchi.altervista.org.ymir.model.TimeserieSerializer;

public class TSManager {

    private static TSManager instance;

    private HashMap<String, TreeMap<Long, Double>> timeseries;

    private SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private TSManager(){
        timeseries = new HashMap<>();
    }

    public static TSManager getInstance(){
        if(instance == null) instance = new TSManager();
        return instance;
    }

    public HashMap<String, TreeMap<Long, Double>> getTimeseries(){
        return timeseries;
    }

    public void addToDataStore(String timeserieKey, Long time, Double value){
        // retrieve the series for the given key
        TreeMap<Long,Double> series = timeseries.get(timeserieKey);
        // append the new value
        series.put(time, value);
        // update the general datastore
        timeseries.put(timeserieKey, series);
    }

    public String[] getAvailableTimeseries(){
        String[] result = new String[timeseries.keySet().size()];
        return timeseries.keySet().toArray(result);
    }

    public long getTimeserieBeginning(String timeserieKey){
        long beginning = 0;

        if(timeseries.containsKey(timeserieKey)                                     // a series exist for the tariff
                && timeseries.get(timeserieKey).entrySet().size() > 0){             // there are data in the series
            beginning = timeseries.get(timeserieKey).keySet().iterator().next();    // get the first key in the hashmap
        }
        return beginning;
    }

    public void serializeToDatabase(TimeserieSerializer serializer){
        // loop on available timeseries
        for(String k : timeseries.keySet()){
            // save the timeserie as a series of objects
            System.out.println("Serializing "+k);
            serializer.serializeTimeserie(k,  timeseries.get(k) );
        }
    }

    public void serializeFromDatabase(TimeserieSerializer serializer){
        timeseries = new HashMap<>();
        for(String k : serializer.getTimeseriesNames()){
            // retrieve the timeserie k from the database
            TreeMap<Long, Double> ts = serializer.getTimeserieFromDatabase(k);
            // add the timeserie to the dictionary
            timeseries.put(k, ts);
        }
    }

    public SortedMap<Long, Double> seekTimeInterval(String tariff,
                                                           int year,
                                                           int month,          // always between 0 and 11
                                                           int day,
                                                           int hour,           // time provided in the local time zone
                                                           int durationInHours
                                                           ) throws UnavailableDataForIntervalException { // include or not the interval end
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);          // given year
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long beginning = c.getTimeInMillis();
        c.add(Calendar.HOUR_OF_DAY, durationInHours);
        long end = c.getTimeInMillis();

        //System.out.println("*** Beginning: "+dt.format(beginning)+", End: "+dt.format(end));

        // at default the end value is excluded by the returned sortedmap
        // so the only way to include it is to increase the number of milliseconds in the future

        SortedMap<Long,Double> filteredSeries = timeseries.get(tariff).subMap(beginning, end);

        if(filteredSeries.keySet().size() == 0)
            throw new UnavailableDataForIntervalException();

        return filteredSeries;
    }

    public double[] aggregateDataByHour(String tariff, int yearNumber, int monthNumber, int dayNumber) throws UnavailableDataForIntervalException {
        double[] consumption = new double[24];

        // pick a day interval from the datastore
        SortedMap<Long, Double> interval = seekTimeInterval(tariff, yearNumber, monthNumber, dayNumber,
                0, 24  // from midnight for 24 hours
                );

        Calendar t = Calendar.getInstance();
        for(Long l : interval.keySet()){
            t.setTimeInMillis(l);   // set to visited time
            //System.out.println("Adding "+interval.get(l)+" to "+dt.format(l)+", H:"+t.get(Calendar.HOUR));
            consumption[t.get(Calendar.HOUR_OF_DAY)] += interval.get(l);
        }

        return consumption;
    }

    public double[] aggregateDataByDay(String tariff, int yearNumber, int monthNumber) throws UnavailableDataForIntervalException {
        Calendar t = new GregorianCalendar(yearNumber, monthNumber, 1);

        double[] consumption = new double[t.getActualMaximum(Calendar.DAY_OF_MONTH)];

        // pick a monthly interval from the datastore
        SortedMap<Long, Double> interval = seekTimeInterval(tariff, yearNumber, monthNumber,
                1,      // first day of the month
                0,      // from midnight
                24 * t.getActualMaximum(Calendar.DAY_OF_MONTH)  // for (24 * no_days) hours
                );

        // loop on the interval and fill the daily array
        for(Long l : interval.keySet()){
            t.setTimeInMillis(l);
            consumption[t.get(Calendar.DAY_OF_MONTH)-1] += interval.get(l);
        }

        return consumption;
    }

    public double[] aggregateDataByMonth(String tariff, int yearNumber) throws UnavailableDataForIntervalException {
        double[] consumption = new double[12];

        Calendar t = new GregorianCalendar(yearNumber, Calendar.JANUARY, 1); // 1 Jan Year

        // select 1 year data starting from the given year beginning
        SortedMap<Long, Double> interval = seekTimeInterval(tariff,
                yearNumber,
                Calendar.JANUARY,      // january is always 0
                1,                     // first day of the month
                0,                     // from midnight
                24 * t.getActualMaximum(Calendar.DAY_OF_YEAR)  // for (24 * no_days) hours
                );

        //System.out.println("Lookup for year "+yearNumber+" returned "+interval.keySet().size()+" entries");

        // loop on the interval and fill the monthly array
        for(Long l : interval.keySet()){
            t.setTimeInMillis(l);
            consumption[t.get(Calendar.MONTH)] += interval.get(l);
        }

        return consumption;
    }

}
