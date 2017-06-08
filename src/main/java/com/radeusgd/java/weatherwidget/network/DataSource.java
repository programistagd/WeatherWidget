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
 * Exception reported when HTTP status code is not OK (eg. 404, 403 etc.)
 */
class HttpError extends RuntimeException{
    public HttpError(String message){
        super(message);
    }
}

/**
 * DataSource is responsible for fetching data from a (remote) source.
 * Every call to makeRequest should be followed by one piece of data pushed into dataStream in a reasonable amount of time.
 * If the request results in error, null should be pushed to indicate failure (the details of the error are reported using the global ErrorStream).
 * There's no guarantee about ordering of the requests (response to latter request can come earlier on the stream), only the amount of calls and data should eventually be equal.
 * @param <T> type of data that is returned as a response to each request
 */
public abstract class DataSource<T> {
    private final static long TIMEOUT = 10;

    protected final PublishSubject<T> dataStream = PublishSubject.create();

    /**
     * @return stream of responses to requests
     */
    public Observable<T> getDataStream(){
        return dataStream.asObservable();
    }

    private void handleRequestError(Throwable err){
        ErrorStream.getInstance().notifyAboutError(createRequestError(err));
        dataStream.onNext(null);//indicate that the request failed (so the icon stops spinning etc.)
    }

    /**
     * Asks the DataSource to request it's data, the result will be returned on the dataStream
     */
    public void makeRequest(){
        RxNetty.createHttpRequest(HttpClientRequest.createGet(getURL()))
                .compose(this::unpackResponse)
                .map(this::parseHtml)
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .subscribe(dataStream::onNext,
                        this::handleRequestError);
    }

    /**
     * Helper function to prepare an exception explaining what kind of data we failed to fetch
     * @param cause what caused this error
     * @return exception that will be reported to the ErrorStream
     */
    protected abstract Exception createRequestError(Throwable cause);

    /**
     * @return default URL to make HTTP GET request to
     */
    protected abstract String getURL();

    /**
     * Convert server's response into the expected format
     * @param html response data
     * @return resulting piece of data that will be pushed onto the dataStream
     */
    protected abstract T parseHtml(String html);

    /**
     * Converts HttpClientResponse into a String of the data and handles HTTP errors
     * @param responseObservable a stream that will get a HttpClientResponse
     * @return a stream that will yield a String of that response
     */
    private Observable<String> unpackResponse(Observable<HttpClientResponse<ByteBuf>> responseObservable){
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
