package com.radeusgd.java.weatherwidget.network.datasources;

import com.radeusgd.java.weatherwidget.event.ErrorStream;
import com.radeusgd.java.weatherwidget.event.WeatherData;

/**
 * A dummy WeatherDataSource that immediately fails on each request.
 * Used to demonstrate error handling.
 */
public class ForceErrorWeatherSource extends WeatherDataSource {

    @Override
    protected String getURL() {
        return null;
    }

    @Override
    protected WeatherData parseHtml(String html) {
        return null;
    }

    @Override
    public void makeRequest(){
        ErrorStream.getInstance().notifyAboutError(createRequestError(new Exception("This source always throws an exception to demonstrate error handling")));
        dataStream.onNext(null);
    }
}
