package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.PollutionEvent;
import com.radeusgd.java.weatherwidget.event.StatusEvent;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by Programistagd on 05.06.2017.
 */
public class PollutionProxy extends DataProvider<PollutionEvent> {
    private PollutionDataSource source;

    private SerializedSubject<String,String> pm25 = new SerializedSubject<>(PublishSubject.create());
    private SerializedSubject<String,String> pm10 = new SerializedSubject<>(PublishSubject.create());

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

    private void onIncomingData(PollutionEvent event){
        dataEvents.onNext(event);
        statusEvents.onNext(StatusEvent.UPDATE_COMPLETED);
    }

    public Observable<String> getPM25(){
        return prepareSubjectForFX(pm25);
    }

    public Observable<String> getPM10(){
        return prepareSubjectForFX(pm10);
    }
}
