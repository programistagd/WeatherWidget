package com.radeusgd.java.weatherwidget.network;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by Radek on 01.06.2017.
 */
public abstract class DataSource {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DataSource.class);

    private static final int POLL_INTERVAL = 60;
    private static final int INITIAL_DELAY = 3;
    private static final int TIMEOUT = 20;

    public Observable<? extends XChangeEvent> dataSourceStream() {
		/*
		 * This creates a stream of data events. Each event emitted corresponds
		 * to a piece of data fetched from a remote (i.e. Internet) data source.
		 * This class is capable of grabbing data in one of two ways. Firstly,
		 * it can poll the data source every POLL_INTERVAL seconds. Secondly, it
		 * can fetch data on request (e.g. when a user hits the refresh button
		 * which causes a RefreshRequestEvent to be triggered; the event is
		 * handled here). The code below essentially merges events that arrive
		 * via one of the two routes into a single stream of events.
		 */
        return fixedIntervalStream().compose(this::wrapRequest)
                .mergeWith(eventStream().eventsInIO().ofType(RefreshRequestEvent.class).compose(this::wrapRequest));
    }

    protected Observable<Long> fixedIntervalStream() {
        return Observable.interval(INITIAL_DELAY, POLL_INTERVAL, TimeUnit.SECONDS, Schedulers.io());
    }

    protected abstract <T> Observable<? extends RateEvent> makeRequest();

    protected HttpClientRequest<ByteBuf> prepareHttpGETRequest(String url) {
		/*
		 * As the name says, this creates an HTTP GET request (but does not send
		 * it, sending is done elsewhere).
		 */
        return HttpClientRequest.createGet(url);
    }

    protected <T> Observable<String> unpackResponse(Observable<HttpClientResponse<ByteBuf>> responseObservable) {
		/*
		 * Extracts HTTP response's body to a plain Java string
		 */
        return responseObservable.flatMap(HttpClientResponse::getContent)
                .map(buffer -> buffer.toString(CharsetUtil.UTF_8));
    }

    private <T> Observable<?> wrapRequest(Observable<T> observable) {
		/*
		 * Issues an HTTP query but emits an appropriate even before the query
		 * is made and another event when the query is completed. This allows us
		 * to give visual feedback (spinning icon) to the user during the
		 * request.
		 */
        /*return observable.flatMap(ignore -> Observable.concat(Observable.just(new NetworkRequestIssuedEvent()),
                makeRequest().timeout(TIMEOUT, TimeUnit.SECONDS).doOnError(log::error)
                        .cast(XChangeEvent.class).onErrorReturn(ErrorEvent::new),
                Observable.just(new NetworkRequestFinishedEvent()))
        );*/
    }
}
