package com.radeusgd.java.weatherwidget.controller;

import com.radeusgd.java.weatherwidget.control.ValueControl;
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

    @FXML
    private void initialize(){
        List<WeatherDataSource> sources = new ArrayList();
        sources.add(new MeteoWaw());
        sources.add(new OpenWeatherMap("db09a595e245a0ee1640c8e9ecdaff52"));
        weather = new WeatherProxy(sources);
        weather.chooseSource(1);
        weather.getStatusStream().subscribe(evt -> System.out.println(evt));
        weather.getUpdateStream().subscribe(w -> System.out.println(w.temperature+"; "+w.clouds));
        //weather.manualRefreshRequest();

        pollution = new PollutionProxy(new PowietrzeGiosGov());
        pollution.getStatusStream().subscribe(evt -> System.out.println(evt));
        pollution.getUpdateStream().subscribe(p -> System.out.println(p.pm10+"; "+p.pm25));
        //pollution.manualRefreshRequest();

        temperature.setSource(weather.getTemperatureStream());
        humidity.setSource(weather.getHumidityStream());
        clouds.setSource(weather.getCloudsStream());
        windSpeed.setSource(weather.getWindSpeedStream());
        //TODO windSpeed
        pressure.setSource(weather.getPressureStream());

        //TODO icon

        pm25.setSource(pollution.getPM25());
        pm10.setSource(pollution.getPM10());
    }
}
