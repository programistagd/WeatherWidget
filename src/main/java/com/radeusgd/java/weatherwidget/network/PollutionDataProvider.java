package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.PollutionEvent;
import com.radeusgd.java.weatherwidget.event.StatusEvent;

/**
 * Created by Programistagd on 05.06.2017.
 */
public class PollutionDataProvider extends DataProvider<PollutionEvent> {
    private PollutionDataSource source;

    public PollutionDataProvider(PollutionDataSource source){
        this.source = source;
        source.getEventStream().subscribe(this::onIncomingData);
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
}
