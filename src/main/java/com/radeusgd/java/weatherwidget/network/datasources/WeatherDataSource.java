package com.radeusgd.java.weatherwidget.network.datasources;

import com.radeusgd.java.weatherwidget.event.WeatherData;
import com.radeusgd.java.weatherwidget.network.DataSource;
import com.radeusgd.java.weatherwidget.network.WeatherNotFoundException;

/**
 * Data source fetching weather data
 */
public abstract class WeatherDataSource extends DataSource<WeatherData> {

    @Override
    protected Exception createRequestError(Throwable cause) {
        return new WeatherNotFoundException(cause);
    }
}
