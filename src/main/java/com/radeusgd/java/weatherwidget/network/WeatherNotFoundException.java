package com.radeusgd.java.weatherwidget.network;

public class WeatherNotFoundException extends Exception {
    public WeatherNotFoundException(Throwable cause){
        super("There was an error fetching weather data", cause);
    }
}
