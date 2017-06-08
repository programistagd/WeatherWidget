package com.radeusgd.java.weatherwidget.network;


import com.radeusgd.java.weatherwidget.event.UpdateStatusEvent;
import rx.Observable;
import rx.schedulers.JavaFxScheduler;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.concurrent.TimeUnit;

/**
 * DataProvider is responsible for providing up-to-date data for the application.
 * It will request fresh data periodically or when asked by the user.
 * It reports when the data update has started and finished (successfully or not) using the statusStream.
 * Most recent pieces of data land in updateStream (and classes that implement this abstract class should extract these updates and provide streams for each of the unpacked values)
 *
 * If the request failed no data is sent onto updateStream and unpacked values streams.
 * Sometimes the result may be incomplete (some fields of the data piece of type T can be null, we allow this). In that case, the DataProvider should map such values into "-" characters so that the view can indicate a value is missing and not keep the old (possibly stale one). This is achieved by prepareSubjectForFX.
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

    private final PublishSubject<T> dataEvents;
    protected final PublishSubject<UpdateStatusEvent> statusEvents;

    class RefreshRequest{}

    /**
     * @return Stream of responses to periodic requests
     */
    public Observable<T> getUpdateStream(){
        return dataEvents.asObservable();
    }

    /**
     * @return Stream of statuses telling when the update starts and finishes
     */
    public Observable<UpdateStatusEvent> getStatusStream(){
        return statusEvents.asObservable();
    }

    /**
     * Simple helper that checks if provided data is valid and feeds it into dataEvents stream or indicates an update failure
     * @param event
     */
    protected void onIncomingData(T event){
        if(event == null){
            statusEvents.onNext(UpdateStatusEvent.UPDATE_FAILED);
        }
        else {
            dataEvents.onNext(event);
            statusEvents.onNext(UpdateStatusEvent.UPDATE_COMPLETED);
        }
    }

    /**
     * Specifies details of what happens when we want to request fresh data
     */
    protected abstract void onUpdateRequested();

    private final PublishSubject<RefreshRequest> manualRefreshRequests;
    private final Observable<RefreshRequest> refreshRequests;

    /**
     * Used to manually ask for new data (eg. user clicked a button)
     */
    public void manualRefreshRequest(){
        manualRefreshRequests.onNext(new RefreshRequest());
    }

    /**
     * Helper function that wraps a String subject into an observable that runs on JavaFX thread (so it will be able to safely modify view) and
     * @param subject
     * @return
     */
    protected Observable<String> prepareSubjectForFX(Subject<String, String> subject){
        return subject.asObservable()
                .map(v -> (v == null) ? "-" : v) //make null values into dashes indicating no given value
                .mergeWith(Observable.just("-")) //add initial value (before first update etc.)
                .observeOn(JavaFxScheduler.getInstance()); //force JavaFX thread
    }
}
