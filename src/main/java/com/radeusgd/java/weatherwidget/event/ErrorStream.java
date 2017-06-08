package com.radeusgd.java.weatherwidget.event;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Programistagd on 07.06.2017.
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

    public void notifyAboutError(Exception e){
        stream.onNext(e);
    }

    public Observable<Exception> getStream(){
        return stream.asObservable();
    }
}
