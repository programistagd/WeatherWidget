package com.radeusgd.java.weatherwidget.network.datasources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.radeusgd.java.weatherwidget.event.PollutionEvent;
import com.radeusgd.java.weatherwidget.network.PollutionDataSource;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import com.google.gson.JsonParser;

/**
 * Created by Programistagd on 05.06.2017.
 */
public class PowietrzeGiosGov extends PollutionDataSource {
    private static final String URL = "http://powietrze.gios.gov.pl/pjp/current/getAQIDetailsList?param=AQI";
    private static final int STATION_ID = 544;//station Marszałkowska

    @Override
    public void makeRequest() {
        RxNetty.createHttpRequest(HttpClientRequest.createGet(URL))
                .compose(this::unpackResponse)
                .map(html -> {
                    JsonParser p = new JsonParser();
                    JsonArray arr = p.parse(html).getAsJsonArray();
                    for(JsonElement e : arr){
                        JsonObject o = e.getAsJsonObject();
                        if(o.get("stationId").getAsInt() == STATION_ID){
                            JsonObject values = o.get("values").getAsJsonObject();
                            return new PollutionEvent(values.get("PM2.5").getAsString(), values.get("PM10").getAsString());
                        }
                    }
                    return new PollutionEvent("", "");//TODO throw new PollutionNotFoundException();
                        }
                ).subscribe(d -> dataStream.onNext(d));
    }
}
