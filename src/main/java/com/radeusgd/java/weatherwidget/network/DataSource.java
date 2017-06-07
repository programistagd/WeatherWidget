package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.ErrorStream;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Programistagd on 05.06.2017.
 */
class HttpError extends RuntimeException{
    public HttpError(String message){
        super(message);
    }
}

public abstract class DataSource<T> {
    private final static long TIMEOUT = 10;

    protected PublishSubject<T> dataStream = PublishSubject.create();

    public Observable<T> getEventStream(){
        return dataStream.asObservable();
    }

    private void handleRequestError(Throwable err){
        ErrorStream.getInstance().notifyAboutError(createRequestError(err));
        dataStream.onNext(null);//indicate that the request failed (so the icon stops spinning etc.)
    }

    public void makeRequest(){
        RxNetty.createHttpRequest(HttpClientRequest.createGet(getURL()))
                .compose(this::unpackResponse)
                .map(this::parseHtml)
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .subscribe(d -> dataStream.onNext(d),
                        this::handleRequestError);
    }

    protected abstract Exception createRequestError(Throwable cause);

    protected abstract String getURL();
    protected abstract T parseHtml(String html);

    protected Observable<String> unpackResponse(Observable<HttpClientResponse<ByteBuf>> responseObservable){
        return responseObservable.flatMap(response -> {
            if(response.getStatus().code() == 200){
                return response.getContent();
            }
            else{
                throw new HttpError(response.getStatus().toString());
            }
        }).map(buffer -> buffer.toString(CharsetUtil.UTF_8));
    }
}
