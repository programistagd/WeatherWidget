package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.PollutionData;
import com.radeusgd.java.weatherwidget.event.UpdateStatusEvent;
import com.radeusgd.java.weatherwidget.network.datasources.PollutionDataSource;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Simple implementation of DataProvider that fetches pollution data from a single source and giving two streams of values for each of types of pollution.
 */
public class PollutionProxy extends DataProvider<PollutionData> {
    private final PollutionDataSource source;

    private final PublishSubject<String> pm25 = PublishSubject.create();
    private final PublishSubject<String> pm10 = PublishSubject.create();

    /**
     * Creates a provider using specified DataSource
     * @param source
     */
    public PollutionProxy(PollutionDataSource source){
        this.source = source;
        source.getDataStream().subscribe(this::onIncomingData);
        getUpdateStream().subscribe(pollutionEvent -> {
           pm25.onNext(pollutionEvent.pm25);
           pm10.onNext(pollutionEvent.pm10);
        });
    }

    @Override
    protected void onUpdateRequested() {
        statusEvents.onNext(UpdateStatusEvent.UPDATE_IN_PROGRESS);
        source.makeRequest();
    }

    /**
     * @return stream of PM2.5 levels
     */
    public Observable<String> getPM25(){
        return prepareSubjectForFX(pm25);
    }

    /**
     * @return stream of PM10 levels
     */
    public Observable<String> getPM10(){
        return prepareSubjectForFX(pm10);
    }
}
