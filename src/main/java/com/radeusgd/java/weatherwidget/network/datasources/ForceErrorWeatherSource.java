package com.radeusgd.java.weatherwidget.network.datasources;

import com.radeusgd.java.weatherwidget.event.ErrorStream;
import com.radeusgd.java.weatherwidget.event.WeatherEvent;
import com.radeusgd.java.weatherwidget.network.WeatherDataSource;

/**
 * Created by Programistagd on 07.06.2017.
 */
public class ForceErrorWeatherSource extends WeatherDataSource {

    @Override
    protected String getURL() {
        return null;
    }

    @Override
    protected WeatherEvent parseHtml(String html) {
        return null;
    }

    @Override
    public void makeRequest(){
        ErrorStream.getInstance().notifyAboutError(createRequestError(new Exception("This source always throws an exception to demonstrate error handling")));
        dataStream.onNext(null);
    }
}
