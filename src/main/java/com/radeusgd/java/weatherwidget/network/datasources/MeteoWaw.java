package com.radeusgd.java.weatherwidget.network.datasources;

import com.radeusgd.java.weatherwidget.event.WeatherEvent;
import com.radeusgd.java.weatherwidget.event.WeatherNotFoundException;
import com.radeusgd.java.weatherwidget.network.WeatherDataSource;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import rx.Observable;
import rx.exceptions.Exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Programistagd on 05.06.2017.
 */
public class MeteoWaw extends WeatherDataSource {

    private static final String URL = "http://www.meteo.waw.pl/";

    private String findSpan(String html, String id){
        Pattern pattern = Pattern.compile("<span id=\\\""+id+"\\\">([\\d,NEWS]+)<\\/span>");
        Matcher m = pattern.matcher(html);
        if (m.find()) {
            return m.group(1).trim();
        }
        //TODO consider throwing an exception here -> corrupted data?
        return null;
    }

    @Override
    public void makeRequest(){
        RxNetty.createHttpRequest(HttpClientRequest.createGet(URL))
                .compose(this::unpackResponse)
                .map(html ->
                    new WeatherEvent(
                            findSpan(html, "PARAM_0_TA"),
                            findSpan(html, "PARAM_0_PR"),
                            null,
                            findSpan(html, "PARAM_0_WV"),
                            findSpan(html, "PARAM_0_WDABBR"),
                            findSpan(html, "PARAM_0_RH"),
                            null)
                ).subscribe(d -> dataStream.onNext(d));
    }
}
