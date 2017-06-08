package com.radeusgd.java.weatherwidget.network.datasources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.radeusgd.java.weatherwidget.event.ErrorStream;
import com.radeusgd.java.weatherwidget.event.WeatherData;

/**
 * WeatherDataSource using OpenWeatherMap.
 * Needs an API key.
 */
public class OpenWeatherMap extends WeatherDataSource {

    private final String URL;

    /**
     * Creates a new source using specified API key, see: http://openweathermap.org/api
     * @param API_KEY
     */
    public OpenWeatherMap(String API_KEY){
        URL = "http://api.openweathermap.org/data/2.5/weather?q=Warsaw,pl&units=metric&appid="+API_KEY;
    }

    /**
     * Converts angular direction into a simple compass direction (NESW)
     * @param deg direction in degrees (0-360), 0 means North
     * @return returns a string representing a closest direction or null if the argument was invalid
     */
    private String formatWindDeg(float deg){
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};

        if(deg > 360.0 || deg < 0.0){
            return null;
        }

        int idx = 0;
        while(deg > 22.5){
            deg -= 45;
            idx += 1;
        }

        assert (idx < directions.length);
        if(idx >= directions.length){
            return null;
        }

        return directions[idx];
    }

    /**
     * Make commas as decimal place delimiters to be consistent across sources
     */
    private String formatWindSpeed(String s){
        return s.replace('.', ',');
    }

    @Override
    protected WeatherData parseHtml(String html) {
        try {
            JsonParser p = new JsonParser();
            JsonObject o = p.parse(html).getAsJsonObject();
            JsonObject m = o.get("main").getAsJsonObject();
            JsonObject wind = o.get("wind").getAsJsonObject();

            return new WeatherData(m.get("temp").getAsString(),
                    m.get("pressure").getAsString(),
                    o.get("clouds").getAsJsonObject().get("all").getAsString(),
                    formatWindSpeed(wind.get("speed").getAsString()),
                    formatWindDeg(wind.get("deg").getAsFloat()),
                    m.get("humidity").getAsString(),
                    o.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString());
        }
        catch(ClassCastException | IllegalStateException | NullPointerException e){
            ErrorStream.getInstance().notifyAboutError(createRequestError(e));
            return null;
        }
    }

    @Override
    protected String getURL(){
        return URL;
    }
}
