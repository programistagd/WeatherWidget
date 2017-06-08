package com.radeusgd.java.weatherwidget.event;

/**
 * Collects various apsects of current weather
 */
public class WeatherData {
    public final String temperature;
    public final String pressure;
    public final String clouds;
    public final String windSpeed;
    public final String windDir;
    public final String humidity;
    public final String icon;

    private String addSufix(String v, String suf){
        if(v == null){
            return null;
        }
        return v + suf;
    }

    public WeatherData(String temperature, String pressure, String clouds, String windSpeed, String windDir, String humidity, String icon){
        this.temperature = addSufix(temperature,"°C");
        this.pressure = addSufix(pressure,"hPa");
        this.clouds = addSufix(clouds,"%");
        this.windSpeed = addSufix(windSpeed,"m/s");
        this.windDir = windDir;
        this.humidity = addSufix(humidity,"%");
        this.icon = icon;
    }

    public WeatherData(String temperature, String pressure, String clouds, String windSpeed, String windDir, String humidity){
        this(temperature, pressure, clouds, windSpeed, windDir, humidity, null);
    }
}
