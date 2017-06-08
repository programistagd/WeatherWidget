package com.radeusgd.java.weatherwidget.network.datasources;

import com.radeusgd.java.weatherwidget.event.ErrorStream;
import com.radeusgd.java.weatherwidget.event.WeatherData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WeatherDataSource fetching data from http://www.meteo.waw.pl/
 */
public class MeteoWaw extends WeatherDataSource {

    class HtmlParseException extends Exception{
        public HtmlParseException(String message){
            super(message);
        }
    }

    private String findSpan(String html, String id) throws HtmlParseException{
        Pattern pattern = Pattern.compile("<span id=\""+id+"\">([\\d,NEWS]+)</span>");
        Matcher m = pattern.matcher(html);
        if (m.find()) {
            return m.group(1).trim();
        }

        throw new HtmlParseException("Couldn't find field "+id+" in server's response");
    }

    @Override
    protected WeatherData parseHtml(String html){
        try {
            return new WeatherData(
                    findSpan(html, "PARAM_0_TA"),
                    findSpan(html, "PARAM_0_PR"),
                    null,
                    findSpan(html, "PARAM_0_WV"),
                    findSpan(html, "PARAM_0_WDABBR"),
                    findSpan(html, "PARAM_0_RH"));
        }
        catch (HtmlParseException e){
            ErrorStream.getInstance().notifyAboutError(createRequestError(e));
            return null;
        }
    }

    @Override
    protected String getURL(){
        return "http://www.meteo.waw.pl/";
    }
}
