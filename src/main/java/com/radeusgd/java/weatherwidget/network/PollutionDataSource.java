package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.PollutionEvent;
import com.radeusgd.java.weatherwidget.event.PollutionNotFoundException;

/**
 * Created by Programistagd on 05.06.2017.
 */
public abstract class PollutionDataSource extends DataSource<PollutionEvent> {
    @Override
    protected Exception createRequestError(Throwable cause) {
        return new PollutionNotFoundException(cause);
    }
}
