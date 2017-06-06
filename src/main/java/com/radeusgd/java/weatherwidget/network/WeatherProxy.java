package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.StatusEvent;
import com.radeusgd.java.weatherwidget.event.WeatherEvent;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.Observable;

import java.util.List;

/**
 * Created by Radek on 01.06.2017.
 */
public class WeatherProxy extends DataProvider<WeatherEvent> {

    private List<WeatherDataSource> sources;
    private Subscription currentSub;
    private WeatherDataSource currentSrc;

    private PublishSubject<String> temperatures = PublishSubject.create();
    private PublishSubject<String> pressures = PublishSubject.create();
    private PublishSubject<String> clouds = PublishSubject.create();
    private PublishSubject<String> windSpeeds = PublishSubject.create();
    private PublishSubject<String> windDirs = PublishSubject.create();
    private PublishSubject<String> humidities = PublishSubject.create();
    private PublishSubject<String> icons = PublishSubject.create();

    public WeatherProxy(List<WeatherDataSource> sources){
        super();
        this.sources = sources;
        chooseSource(0);
        getUpdateStream().subscribe(weather -> {
            temperatures.onNext(weather.temperature);
            pressures.onNext(weather.pressure);
            clouds.onNext(weather.clouds);
            windSpeeds.onNext(weather.windSpeed);
            windDirs.onNext(weather.windDir);
            humidities.onNext(weather.humidity);
            icons.onNext(weather.icon);
        });
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

    public Observable<String> getTemperatureStream(){
        return prepareSubjectForFX(temperatures);
    }

    public Observable<String> getPressureStream(){
        return prepareSubjectForFX(pressures);
    }

    public Observable<String> getCloudsStream(){
        return prepareSubjectForFX(clouds);
    }

    public Observable<String> getWindSpeedStream(){
        return prepareSubjectForFX(windSpeeds);
    }

    public Observable<String> getWindDirStream(){
        return prepareSubjectForFX(windDirs);
    }

    public Observable<String> getHumidityStream(){
        return prepareSubjectForFX(humidities);
    }

    public Observable<String> getIconStream(){
        return prepareSubjectForFX(icons);
    }

}
