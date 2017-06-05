package com.radeusgd.java.weatherwidget.event;

/**
 * Created by Programistagd on 05.06.2017.
 */
public class PollutionEvent {
    public final String pm25;
    public final String pm10;

    public PollutionEvent(String pm25, String pm10){
        this.pm25 = pm25;
        this.pm10 = pm10;
    }
}
