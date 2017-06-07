package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.WeatherEvent;
import com.radeusgd.java.weatherwidget.event.WeatherNotFoundException;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Programistagd on 05.06.2017.
 */
public abstract class WeatherDataSource extends DataSource<WeatherEvent> {

    @Override
    protected Exception createRequestError(Throwable cause) {
        return new WeatherNotFoundException(cause);
    }
}
