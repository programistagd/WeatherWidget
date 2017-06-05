package com.radeusgd.java.weatherwidget.event;

/**
 * Created by Radek on 01.06.2017.
 */
public class WeatherEvent {
    public final String temperature;
    public final String pressure;
    public final String clouds;
    public final String wind;
    public final String humidity;
    public final String icon;

    public WeatherEvent(String temperature, String pressure, String clouds, String wind, String humidity, String icon){
        this.temperature = temperature;
        this.pressure = pressure;
        this.clouds = clouds;
        this.wind = wind;
        this.humidity = humidity;
        this.icon = icon;
    }

    public WeatherEvent(String temperature, String pressure, String clouds, String wind, String humidity){
        this(temperature, pressure, clouds, wind, humidity, "-");
    }
}
