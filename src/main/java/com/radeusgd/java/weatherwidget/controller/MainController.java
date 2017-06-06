package com.radeusgd.java.weatherwidget.controller;

import com.radeusgd.java.weatherwidget.control.ValueControl;
import com.radeusgd.java.weatherwidget.event.StatusEvent;
import com.radeusgd.java.weatherwidget.network.PollutionProxy;
import com.radeusgd.java.weatherwidget.network.WeatherDataSource;
import com.radeusgd.java.weatherwidget.network.WeatherProxy;
import com.radeusgd.java.weatherwidget.network.datasources.MeteoWaw;
import com.radeusgd.java.weatherwidget.network.datasources.OpenWeatherMap;
import com.radeusgd.java.weatherwidget.network.datasources.PowietrzeGiosGov;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import rx.Observable;
import rx.observables.JavaFxObservable;
import rx.subscribers.JavaFxSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Radek on 01.06.2017.
 */
public class MainController {
    private static final int ERROR_MSG_MAX_LENGTH = 400;
    private static final int ERROR_MSG_DURATION = 30;//Show error icon for 30s

    @FXML
    private ValueControl temperature;

    @FXML
    private ValueControl humidity;

    @FXML
    private ValueControl clouds;

    @FXML
    private ValueControl windSpeed;

    @FXML
    private ValueControl pressure;

    @FXML
    private ValueControl pm25;

    @FXML
    private ValueControl pm10;

    @FXML
    private Node errorIcon;

    @FXML
    private Node workingIcon;

    @FXML
    private Button refreshButton;

    @FXML
    private Button settingsButton;

    private WeatherProxy weather;
    private PollutionProxy pollution;

    private void prepareView(){
        List<WeatherDataSource> sources = new ArrayList();
        sources.add(new MeteoWaw());
        sources.add(new OpenWeatherMap("db09a595e245a0ee1640c8e9ecdaff52"));
        weather = new WeatherProxy(sources);
        weather.chooseSource(0);

        pollution = new PollutionProxy(new PowietrzeGiosGov());

        temperature.setSource(weather.getTemperatureStream());
        humidity.setSource(weather.getHumidityStream());
        clouds.setSource(weather.getCloudsStream());

        Observable<String> wind = weather.getWindSpeedStream().zipWith(weather.getWindDirStream(), (speed,dir) -> speed + " " + dir);

        windSpeed.setSource(wind);

        pressure.setSource(weather.getPressureStream());

        //TODO icon

        pm25.setSource(pollution.getPM25());
        pm10.setSource(pollution.getPM10());
    }

    private void prepareControls(){
        JavaFxObservable.actionEventsOf(refreshButton).subscribe(evt -> {
            weather.manualRefreshRequest();
            pollution.manualRefreshRequest();
        });

        JavaFxObservable.actionEventsOf(settingsButton).subscribe(evt -> {
            System.out.println("TODO: show settings");
        });

        Observable<StatusEvent> statusStreams = weather.getStatusStream().mergeWith(pollution.getStatusStream());
        Observable<Boolean> isWorking = statusStreams.map(statusEvent -> {
            switch(statusEvent){
                case UPDATE_COMPLETED:
                case UPDATE_FAILED:
                    return -1;
                case UPDATE_IN_PROGRESS:
                    return 1;
            }
            return 0;//actually unreachable, but IDE doesn't get it?
        }).scan(0, (x,y) -> x + y)
        .map(activeAmount -> (activeAmount > 0));

        workingIcon.visibleProperty().bind(JavaFxSubscriber.toBinding(isWorking));
    }

    @FXML
    private void initialize(){
        prepareView();
        prepareControls();
    }
}
