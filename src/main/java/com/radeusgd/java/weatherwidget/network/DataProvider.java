package com.radeusgd.java.weatherwidget.network;


import com.radeusgd.java.weatherwidget.event.StatusEvent;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Radek on 01.06.2017.
 */
public abstract class DataProvider<T> {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DataProvider.class);

    private static final int POLL_INTERVAL = 60;
    private static final int INITIAL_DELAY = 3;
    private static final int TIMEOUT = 20;

    public DataProvider(){
        dataEvents = PublishSubject.create();
        statusEvents = PublishSubject.create();
        manualRefreshRequests = PublishSubject.create();
        Observable<Long> interval = Observable.interval(INITIAL_DELAY, POLL_INTERVAL, TimeUnit.SECONDS);
        refreshRequests = interval.map(ignore -> new RefreshRequest()).mergeWith(manualRefreshRequests)//łączymy prośby automatyczne (co minutę) z ręcznymi (manualRefreshRequest)
                            .throttleFirst(500, TimeUnit.MILLISECONDS);//nie akutalizujemy się cześciej niż 2 razy na sekundę
        refreshRequests.subscribe(ignore -> this.onUpdateRequested());
    }

    protected PublishSubject<T> dataEvents;
    protected PublishSubject<StatusEvent> statusEvents;

    class RefreshRequest{}

    public Observable<T> getUpdateStream(){
        return dataEvents.asObservable();
    }

    public Observable<StatusEvent> getStatusStream(){
        return statusEvents.asObservable();
    }

    protected abstract void onUpdateRequested();

    private PublishSubject<RefreshRequest> manualRefreshRequests;
    private Observable<RefreshRequest> refreshRequests;

    public void manualRefreshRequest(){
        manualRefreshRequests.onNext(new RefreshRequest());
    }
}
