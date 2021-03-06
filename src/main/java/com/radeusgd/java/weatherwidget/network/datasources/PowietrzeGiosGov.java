package com.radeusgd.java.weatherwidget.network.datasources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.radeusgd.java.weatherwidget.event.ErrorStream;
import com.radeusgd.java.weatherwidget.event.PollutionData;
import com.google.gson.JsonParser;

/**
 * PollutionDataSource fetching data from http://powietrze.gios.gov.pl/
 * Currently looks at levels in station at Marszałkowska (Warsaw), but that can be easily customized
 */
public class PowietrzeGiosGov extends PollutionDataSource {
    private static final int STATION_ID = 544;//station Marszałkowska

    private String formatPollutionValue(String value){
        float v = Float.parseFloat(value);
        return String.format("%.1f", v);
    }

    @Override
    protected PollutionData parseHtml(String html){
        try {
            JsonParser p = new JsonParser();
            JsonArray arr = p.parse(html).getAsJsonArray();

            for (JsonElement e : arr) {
                JsonObject o = e.getAsJsonObject();
                if (o.get("stationId").getAsInt() == STATION_ID) {
                    JsonObject values = o.get("values").getAsJsonObject();
                    return new PollutionData(
                            formatPollutionValue(values.get("PM2.5").getAsString()),
                            formatPollutionValue(values.get("PM10").getAsString()));
                }
            }

            ErrorStream.getInstance().notifyAboutError(createRequestError(new Exception("Couldn't find station Marszałkowska ("+STATION_ID+") in server's response")));
            return null;
        }
        catch(ClassCastException | IllegalStateException e){
            ErrorStream.getInstance().notifyAboutError(createRequestError(e));
            return null;
        }
    }

    @Override
    protected String getURL(){
        return "http://powietrze.gios.gov.pl/pjp/current/getAQIDetailsList?param=AQI";
    }
}
