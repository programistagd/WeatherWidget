package com.radeusgd.java.weatherwidget.controller;

import com.radeusgd.java.weatherwidget.AppMain;
import com.radeusgd.java.weatherwidget.control.ValueControl;
import com.radeusgd.java.weatherwidget.event.ErrorStream;
import com.radeusgd.java.weatherwidget.event.StatusEvent;
import com.radeusgd.java.weatherwidget.network.PollutionProxy;
import com.radeusgd.java.weatherwidget.network.WeatherDataSource;
import com.radeusgd.java.weatherwidget.network.WeatherProxy;
import com.radeusgd.java.weatherwidget.network.datasources.ForceErrorWeatherSource;
import com.radeusgd.java.weatherwidget.network.datasources.MeteoWaw;
import com.radeusgd.java.weatherwidget.network.datasources.OpenWeatherMap;
import com.radeusgd.java.weatherwidget.network.datasources.PowietrzeGiosGov;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rx.Observable;
import rx.observables.JavaFxObservable;
import rx.schedulers.JavaFxScheduler;
import rx.subscribers.JavaFxSubscriber;
;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Radek on 01.06.2017.
 */
public class MainController {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MainController.class);

    private static final long ERROR_MSG_DURATION = 30;
    private static final int ERROR_MAX_LENGTH = 300;

    private static final String FXML_SETTINGS_DIALOG_TEMPLATE = "/fxml/dialog-settings.fxml";

    @FXML
    private StackPane mainPane;

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

    @FXML
    private ImageView weatherIcon;

    private WeatherProxy weather;
    private PollutionProxy pollution;
    private List<String> weatherSourceNames;

    private void prepareView(){
        List<WeatherDataSource> sources = new ArrayList();
        weatherSourceNames = new ArrayList<>();
        sources.add(new MeteoWaw());
        weatherSourceNames.add("meteo.waw.pl");
        sources.add(new OpenWeatherMap("db09a595e245a0ee1640c8e9ecdaff52"));
        weatherSourceNames.add("Open Weather Map");
        sources.add(new ForceErrorWeatherSource());
        weatherSourceNames.add("Test Error Handling (always fails)");
        weather = new WeatherProxy(sources);
        weather.getChosenSourceProperty().setValue(1);//make OpenWeatherMap default

        pollution = new PollutionProxy(new PowietrzeGiosGov());

        temperature.setSource(weather.getTemperatureStream());
        humidity.setSource(weather.getHumidityStream());
        clouds.setSource(weather.getCloudsStream());

        Observable<String> wind = weather.getWindSpeedStream().zipWith(weather.getWindDirStream(), (speed,dir) -> speed + " " + dir);

        windSpeed.setSource(wind);

        pressure.setSource(weather.getPressureStream());

        //TODO icon
        weather.getIconStream().subscribe(icon -> {
            if(icon == "-"){
                weatherIcon.setVisible(false);
            }
            else{
                try {
                    weatherIcon.setImage(new Image(AppMain.class.getResourceAsStream("/icons/" + icon + ".png")));
                    weatherIcon.setVisible(true);
                }
                catch(NullPointerException | IllegalArgumentException e){
                    log.error(e);
                    weatherIcon.setVisible(false);
                }
            }
        });

        pm25.setSource(pollution.getPM25());
        pm10.setSource(pollution.getPM10());
    }

    private void prepareControls(){
        JavaFxObservable.actionEventsOf(refreshButton).subscribe(evt -> {
            weather.manualRefreshRequest();
            pollution.manualRefreshRequest();
        });

        JavaFxObservable.actionEventsOf(settingsButton).subscribe(evt -> {
            showSettings();
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
        workingIcon.managedProperty().bind(workingIcon.visibleProperty());

        Tooltip.install(workingIcon, new Tooltip("Fetching data..."));
    }

    private void prepareErrorHandling(){
        Observable<Exception> errors = ErrorStream.getInstance().getStream().observeOn(JavaFxScheduler.getInstance());

        errorIcon.managedProperty().bind(errorIcon.visibleProperty());
        errorIcon.visibleProperty()
                .bind(JavaFxSubscriber.toBinding(
                        errors.map(ignore -> true).mergeWith(
                        errors.throttleWithTimeout(ERROR_MSG_DURATION, TimeUnit.SECONDS, JavaFxScheduler.getInstance()).map(ignore -> false))
                        ));

        Tooltip errorTooltip = new Tooltip();

        errors.subscribe(e -> {
                    log.error(e);
                    String text = "Error: ";
                    if(e.getMessage() != null){
                        text += e.getMessage();
                    }
                    else{
                        text += e.getClass().getSimpleName();
                    }

                    if(e.getCause() != null){
                        String cause = e.getCause().getClass().getSimpleName();
                        if(e.getCause().getMessage() != null) {
                            cause = e.getCause().getMessage();
                        }
                        text += "\n   Caused by: " + cause;
                    }

                    if(text.length() > ERROR_MAX_LENGTH){
                        text = text.substring(0, ERROR_MAX_LENGTH) + "\u2026";//Add ellipsis (...) at the end
                    }

                    errorTooltip.setText(text);
                });

        Tooltip.install(errorIcon, errorTooltip);
    }

    private SettingsDialogController settingsDialogController;

    private void showSettings(){
        if(settingsDialogController == null){
            settingsDialogController = new SettingsDialogController(weatherSourceNames, weather);
            FXMLLoader loader = new FXMLLoader(AppMain.class.getResource(FXML_SETTINGS_DIALOG_TEMPLATE));
            loader.setController(settingsDialogController);
            try {
                loader.load();
            } catch (IOException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }

        settingsDialogController.show(mainPane);
    }

    @FXML
    private void initialize(){
        prepareView();
        prepareControls();
        prepareErrorHandling();
    }
}
