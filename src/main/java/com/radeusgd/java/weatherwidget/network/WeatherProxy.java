package com.radeusgd.java.weatherwidget.network;

import com.radeusgd.java.weatherwidget.event.UpdateStatusEvent;
import com.radeusgd.java.weatherwidget.event.WeatherData;
import com.radeusgd.java.weatherwidget.network.datasources.WeatherDataSource;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import rx.Subscription;
import rx.observables.JavaFxObservable;
import rx.subjects.PublishSubject;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Weather DataProvider that allows to choose DataSource on the fly.
 * It also has streams that unpack WeatherData into particular weather aspects (temperature, humidity etc.)
 */
public class WeatherProxy extends DataProvider<WeatherData> {

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

    /**
     * @return property indicating which source from the list is currently used
     */
    public ObjectProperty<Integer> getChosenSourceProperty(){
        return chosenSource;
    }

    /**
     * Creates a new WeatherProxy using sources from the specified list, by default it uses the first source from that list
     * @param sources list of sources that the proxy will be able to choose from
     */
    public WeatherProxy(List<WeatherDataSource> sources){
        this.sources = new ArrayList<>(sources);
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
        currentSub = currentSrc.getDataStream().subscribe(this::onIncomingData);
    }

    protected void onUpdateRequested(){
        statusEvents.onNext(UpdateStatusEvent.UPDATE_IN_PROGRESS);
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
