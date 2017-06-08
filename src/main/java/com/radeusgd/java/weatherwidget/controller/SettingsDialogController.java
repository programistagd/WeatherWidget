package com.radeusgd.java.weatherwidget.controller;

import com.jfoenix.controls.JFXDialog;
import com.radeusgd.java.weatherwidget.network.WeatherProxy;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import rx.observables.JavaFxObservable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Programistagd on 06.06.2017.
 */
public class SettingsDialogController {
    private final List<String> sourceNames;
    private final WeatherProxy weather;

    public SettingsDialogController(List<String> sourceNames, WeatherProxy weather){
        this.sourceNames = sourceNames;
        this.weather = weather;
    }

    class WeatherSourceNameConverter extends StringConverter<Integer>{

        @Override
        public String toString(Integer object) {
            return sourceNames.get(object);
        }

        @Override
        public Integer fromString(String string) {
            return null;
        }
    }

    @FXML
    private JFXDialog dialog;

    @FXML
    private Button closeButton;

    @FXML
    private ChoiceBox<Integer> weatherSource;

    @FXML
    private void initialize() {
        JavaFxObservable.actionEventsOf(closeButton).subscribe(ignore -> dialog.close());

        List<Integer> ints = new ArrayList<>();
        for(int i = 0; i < sourceNames.size(); ++i){
            ints.add(i);
        }

        weatherSource.setItems(FXCollections.observableList(ints));
        weatherSource.setConverter(new WeatherSourceNameConverter());

        weatherSource.valueProperty().bindBidirectional(weather.getChosenSourceProperty());
    }

    public void show(StackPane pane) {
        dialog.show(pane);
    }
}
