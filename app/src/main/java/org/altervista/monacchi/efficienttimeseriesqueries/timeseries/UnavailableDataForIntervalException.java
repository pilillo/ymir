package org.altervista.monacchi.efficienttimeseriesqueries.timeseries;

public class UnavailableDataForIntervalException extends Exception {
    public UnavailableDataForIntervalException(){
        super("No consumption data available for the selected time interval.");
    }

    public UnavailableDataForIntervalException(String message){
        super(message);
    }
}
