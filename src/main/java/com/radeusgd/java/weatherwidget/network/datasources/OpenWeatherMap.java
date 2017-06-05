package com.radeusgd.java.weatherwidget.network.datasources;

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
        URL = "http://api.openweathermap.org/data/2.5/weather?q=Warsaw,pl&appid="+API_KEY;
    }

    @Override
    public void makeRequest() {
        RxNetty.createHttpRequest(HttpClientRequest.createGet(URL))
                .compose(this::unpackResponse)
                .map(html ->
                        new WeatherEvent("","","","","","")
                ).subscribe(d -> dataStream.onNext(d));
    }
}
