package com.radeusgd.java.weatherwidget.network.datasources;

import com.radeusgd.java.weatherwidget.event.PollutionData;
import com.radeusgd.java.weatherwidget.network.DataSource;
import com.radeusgd.java.weatherwidget.network.PollutionNotFoundException;

/**
 * Data source fetching pollution data
 */
public abstract class PollutionDataSource extends DataSource<PollutionData> {
    @Override
    protected Exception createRequestError(Throwable cause) {
        return new PollutionNotFoundException(cause);
    }
}
