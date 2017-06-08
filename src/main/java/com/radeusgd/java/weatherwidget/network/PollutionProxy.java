package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.PollutionEvent;
import com.radeusgd.java.weatherwidget.event.StatusEvent;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Programistagd on 05.06.2017.
 */
public class PollutionProxy extends DataProvider<PollutionEvent> {
    private final PollutionDataSource source;

    private final PublishSubject<String> pm25 = PublishSubject.create();
    private final PublishSubject<String> pm10 = PublishSubject.create();

    public PollutionProxy(PollutionDataSource source){
        this.source = source;
        source.getEventStream().subscribe(this::onIncomingData);
        getUpdateStream().subscribe(pollutionEvent -> {
           pm25.onNext(pollutionEvent.pm25);
           pm10.onNext(pollutionEvent.pm10);
        });
    }

    @Override
    protected void onUpdateRequested() {
        statusEvents.onNext(StatusEvent.UPDATE_IN_PROGRESS);
        source.makeRequest();
    }

    public Observable<String> getPM25(){
        return prepareSubjectForFX(pm25);
    }

    public Observable<String> getPM10(){
        return prepareSubjectForFX(pm10);
    }
}
