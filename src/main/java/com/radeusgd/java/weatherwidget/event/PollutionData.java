package com.radeusgd.java.weatherwidget.event;

/**
 * Collects levels of different types of pollution
 */
public class PollutionData {
    public final String pm25;
    public final String pm10;

    public PollutionData(String pm25, String pm10){
        this.pm25 = pm25;
        this.pm10 = pm10;
    }
}
