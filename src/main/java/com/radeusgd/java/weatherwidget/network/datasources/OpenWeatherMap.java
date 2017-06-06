package com.radeusgd.java.weatherwidget.network.datasources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.radeusgd.java.weatherwidget.event.WeatherEvent;
import com.radeusgd.java.weatherwidget.network.WeatherDataSource;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;

/**
 * Created by Programistagd on 05.06.2017.
 */
public class OpenWeatherMap extends WeatherDataSource {

    private final String URL;

    public OpenWeatherMap(String API_KEY){
        URL = "http://api.openweathermap.org/data/2.5/weather?q=Warsaw,pl&units=metric&appid="+API_KEY;
    }

    private String formatWindDeg(float deg){
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
        if(deg > 360.0){
            return null;
        }
        int idx = 0;
        while(deg > 22.5){
            deg -= 45;
            idx += 1;
        }
        //TODO may want to test if it works correctly
        return directions[idx];
    }

    @Override
    public void makeRequest() {
        RxNetty.createHttpRequest(HttpClientRequest.createGet(URL))
                .compose(this::unpackResponse)
                .map(html -> {
                    JsonParser p = new JsonParser();
                    JsonObject o = p.parse(html).getAsJsonObject();
                    JsonObject m = o.get("main").getAsJsonObject();
                    JsonObject wind = o.get("wind").getAsJsonObject();
                    return new WeatherEvent(m.get("temp").getAsString(),
                            m.get("pressure").getAsString(),
                            o.get("clouds").getAsJsonObject().get("all").getAsString(),
                            wind.get("speed").getAsString(),
                            formatWindDeg(wind.get("deg").getAsFloat()),
                            m.get("humidity").getAsString(),
                            o.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString());
                }
                ).subscribe(d -> dataStream.onNext(d));
    }
}
