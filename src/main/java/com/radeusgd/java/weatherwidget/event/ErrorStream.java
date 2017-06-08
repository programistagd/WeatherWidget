package com.radeusgd.java.weatherwidget.event;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Global (shared by whole app) stream of errors that should be reported to the user.
 * Implemented as a singleton.
 */
public class ErrorStream {
    private ErrorStream(){

    }

    private static ErrorStream instance;

    public static ErrorStream getInstance(){
        if(instance == null){
            instance = new ErrorStream();
        }
        return instance;
    }

    private final PublishSubject<Exception> stream = PublishSubject.create();

    /**
     * Push an error into the stream
     * @param e error to report
     */
    public void notifyAboutError(Exception e){
        stream.onNext(e);
    }

    /**
     * @return Stream of errors (Exceptions)
     */
    public Observable<Exception> getStream(){
        return stream.asObservable();
    }
}
