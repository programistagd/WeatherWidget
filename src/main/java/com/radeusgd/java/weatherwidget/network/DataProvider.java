package com.radeusgd.java.weatherwidget.network;


import com.radeusgd.java.weatherwidget.event.StatusEvent;
import rx.Observable;
import rx.schedulers.JavaFxScheduler;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Radek on 01.06.2017.
 */
public abstract class DataProvider<T> {

    private static final int POLL_INTERVAL = 60;
    private static final int INITIAL_DELAY = 2;

    public DataProvider(){
        dataEvents = PublishSubject.create();
        statusEvents = PublishSubject.create();
        manualRefreshRequests = PublishSubject.create();
        Observable<Long> interval = Observable.interval(INITIAL_DELAY, POLL_INTERVAL, TimeUnit.SECONDS);
        refreshRequests = interval.map(ignore -> new RefreshRequest()).mergeWith(manualRefreshRequests)//merge automatic refresh requests with the manual ones (on button clicks)
                            .throttleFirst(500, TimeUnit.MILLISECONDS);//don't update more frequently then 2 times per second
        refreshRequests.subscribe(ignore -> this.onUpdateRequested());
    }

    protected final PublishSubject<T> dataEvents;
    protected final PublishSubject<StatusEvent> statusEvents;

    class RefreshRequest{}

    public Observable<T> getUpdateStream(){
        return dataEvents.asObservable();
    }

    public Observable<StatusEvent> getStatusStream(){
        return statusEvents.asObservable();
    }

    protected void onIncomingData(T event){
        if(event == null){
            statusEvents.onNext(StatusEvent.UPDATE_FAILED);
        }
        else {
            dataEvents.onNext(event);
            statusEvents.onNext(StatusEvent.UPDATE_COMPLETED);
        }
    }

    protected abstract void onUpdateRequested();

    private final PublishSubject<RefreshRequest> manualRefreshRequests;
    private final Observable<RefreshRequest> refreshRequests;

    public void manualRefreshRequest(){
        manualRefreshRequests.onNext(new RefreshRequest());
    }

    protected Observable<String> prepareSubjectForFX(Subject<String, String> sub){
        return sub.asObservable()
                .map(v -> (v == null) ? "-" : v) //make null values into dashes indicating no given value
                .mergeWith(Observable.just("-")) //add initial value (before first update etc.)
                .observeOn(JavaFxScheduler.getInstance()); //force JavaFX thread
    }
}
