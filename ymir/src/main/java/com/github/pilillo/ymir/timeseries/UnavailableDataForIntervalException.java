package com.github.pilillo.ymir.timeseries;

public class UnavailableDataForIntervalException extends Exception {
    public UnavailableDataForIntervalException(){
        super("No consumption data available for the selected time interval.");
    }

    public UnavailableDataForIntervalException(String message){
        super(message);
    }
}
