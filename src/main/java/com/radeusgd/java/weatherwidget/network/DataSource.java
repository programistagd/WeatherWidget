package com.radeusgd.java.weatherwidget.network;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Programistagd on 05.06.2017.
 */
public abstract class DataSource<T> {
    protected PublishSubject<T> dataStream = PublishSubject.create();

    public Observable<T> getEventStream(){
        return dataStream.asObservable();
    }

    public void makeRequest(){
        RxNetty.createHttpRequest(HttpClientRequest.createGet(getURL()))
                .compose(this::unpackResponse)
                .map(this::parseHtml)
                .subscribe(d -> dataStream.onNext(d));
    }

    protected abstract String getURL();
    protected abstract T parseHtml(String html);

    protected Observable<String> unpackResponse(Observable<HttpClientResponse<ByteBuf>> responseObservable){
        return responseObservable.flatMap(HttpClientResponse::getContent).map(buffer -> buffer.toString(CharsetUtil.UTF_8));
    }
}
