package com.radeusgd.java.weatherwidget.event;

/**
 * Created by Programistagd on 05.06.2017.
 */
public class WeatherNotFoundException extends Exception {
    public WeatherNotFoundException(Throwable cause){
        super("There was an error fetching weather data", cause);
    }
}
