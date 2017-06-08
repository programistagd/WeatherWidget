package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.StatusEvent;
import com.radeusgd.java.weatherwidget.event.WeatherEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import rx.Subscription;
import rx.observables.JavaFxObservable;
import rx.subjects.PublishSubject;
import rx.Observable;

import java.util.List;

/**
 * Created by Radek on 01.06.2017.
 */
public class WeatherProxy extends DataProvider<WeatherEvent> {

    private final List<WeatherDataSource> sources;
    private Subscription currentSub;
    private WeatherDataSource currentSrc;

    private final PublishSubject<String> temperatures = PublishSubject.create();
    private final PublishSubject<String> pressures = PublishSubject.create();
    private final PublishSubject<String> clouds = PublishSubject.create();
    private final PublishSubject<String> windSpeeds = PublishSubject.create();
    private final PublishSubject<String> windDirs = PublishSubject.create();
    private final PublishSubject<String> humidities = PublishSubject.create();
    private final PublishSubject<String> icons = PublishSubject.create();

    private final ObjectProperty<Integer> chosenSource = new SimpleObjectProperty<>(0);

    public ObjectProperty<Integer> getChosenSourceProperty(){
        return chosenSource;
    }

    public WeatherProxy(List<WeatherDataSource> sources){
        super();
        this.sources = sources;
        updateSource(chosenSource.get());
        JavaFxObservable.changesOf(chosenSource).subscribe(change -> updateSource(change.getNewVal()));
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

    private void updateSource(int id){
        if(currentSub != null){
            currentSub.unsubscribe();
        }

        currentSrc = sources.get(id);
        currentSub = currentSrc.getEventStream().subscribe(this::onIncomingData);
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
