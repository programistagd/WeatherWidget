package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.StatusEvent;
import com.radeusgd.java.weatherwidget.event.WeatherEvent;
import rx.Subscription;

import java.util.List;

/**
 * Created by Radek on 01.06.2017.
 */
public class WeatherProxy extends DataProvider<WeatherEvent> {

    List<WeatherDataSource> sources;
    Subscription currentSub;
    WeatherDataSource currentSrc;

    public WeatherProxy(List<WeatherDataSource> sources){
        super();
        this.sources = sources;
        chooseSource(0);
    }

    public void chooseSource(int id){
        if(currentSub != null){
            currentSub.unsubscribe();
        }

        currentSrc = sources.get(id);
        currentSub = currentSrc.getEventStream().subscribe(this::onIncomingData);
    }

    private void onIncomingData(WeatherEvent event){
        dataEvents.onNext(event);
        statusEvents.onNext(StatusEvent.UPDATE_COMPLETED);
    }

    protected void onUpdateRequested(){
        statusEvents.onNext(StatusEvent.UPDATE_IN_PROGRESS);
        currentSrc.makeRequest();
    }
}
